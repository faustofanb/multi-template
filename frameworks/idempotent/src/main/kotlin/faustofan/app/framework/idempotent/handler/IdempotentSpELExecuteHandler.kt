package faustofan.app.framework.idempotent.handler

import AbstractIdempotentExecuteHandler
import IdempotentExecuteHandler
import LogUtil
import faustofan.app.framework.cache.DistributedCache
import faustofan.app.framework.idempotent.aspect.IdempotentAspect
import faustofan.app.framework.idempotent.core.IdempotentContext
import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import faustofan.app.framework.idempotent.enums.IdempotentMQConsumeStatusEnum
import faustofan.app.framework.idempotent.exception.RepeatConsumptionException
import faustofan.app.framework.idempotent.util.SpELUtil
import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.ClientException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

// 定义一个接口，用于处理具有幂等性的SpEL（Spring Expression Language）相关操作
interface IdempotentSpELService: IdempotentExecuteHandler

// 实现通过REST API执行的幂等性SpEL操作处理类
@Component
class IdempotentSpELByRestAPIExecuteHandler(
    private val redissonClient: RedissonClient
): AbstractIdempotentExecuteHandler(), IdempotentSpELService {
    companion object {
        // 定义锁的标识符
        private const val LOCK: String = "lock:spEL:restAPI"
    }
    /**
     * 构建幂等性参数包装器
     *
     * @param joinPoint 当前执行的切点
     * @return 返回填充了必要信息的 IdempotentParamWrapper 实例
     */
    override fun buildWrapper(joinPoint: ProceedingJoinPoint): IdempotentParamWrapper {
        val idempotent = IdempotentAspect.getIdempotent(joinPoint)
        val key = SpELUtil.parseKey(
            idempotent.key,
            (joinPoint.signature as MethodSignature).method,
            joinPoint.args
        ) as String
        return IdempotentParamWrapper().apply {
            this.lockKey = key
            this.joinPoint = joinPoint
        }
    }

    /**
     * 处理幂等性操作的逻辑
     *
     * @param wrapper 包含了锁键和切点的包装器
     */
    override fun handler(wrapper: IdempotentParamWrapper) {
        val uniqueKey = wrapper.idempotent!!.uniqueKeyPrefix + wrapper.lockKey
        val lock = redissonClient.getLock(uniqueKey)
        if(!lock.tryLock())
            throw ClientException(ErrorCode.TOO_MANY_REQUESTS.code, "缓存锁获取异常, 请稍后再试...")
        IdempotentContext.put(LOCK, lock)
    }

    /**
     * 处理执行过程中出现的异常
     */
    override fun exceptionProcessing() {
        postProcessing()
    }

    /**
     * 执行操作后的处理逻辑，主要用于释放资源或锁
     */
    override fun postProcessing() {
        var lock: RLock? = null
        try {
            // 从上下文中获取锁
            lock = IdempotentContext.getKey(LOCK) as RLock
        } finally {
            // 释放锁
            lock?.unlock()
        }
    }
}

/**
 * 基于消息队列的幂等性执行处理器
 * 该类实现了抽象幂等性执行处理器和幂等性 SpEL 服务接口
 * 主要通过分布式缓存来实现消息队列消费的幂等性控制
 */
class IdempotentSpELByMQExecuteHandler(
    private val distributedCache: DistributedCache
): AbstractIdempotentExecuteHandler(), IdempotentSpELService {

    /**
     * 构建幂等性参数包装器
     * 该方法用于解析和构建执行切面操作所需的幂等性参数包装器
     * 主要通过 SpEL 表达式解析出唯一的锁键
     */
    override fun buildWrapper(joinPoint: ProceedingJoinPoint): IdempotentParamWrapper {
        val idempotent = IdempotentAspect.getIdempotent(joinPoint)
        return IdempotentParamWrapper().apply {
            this.lockKey = SpELUtil.parseKey(
                idempotent.key,
                (joinPoint.signature as MethodSignature).method,
                joinPoint.args
            ) as String
            this.joinPoint = joinPoint
        }
    }

    /**
     * 处理幂等性执行前的逻辑
     * 该方法在操作执行前检查并处理幂等性，主要通过分布式缓存实现
     * 如果检测到重复消费，则抛出异常；否则，将幂等性参数包装器存入上下文中
     */
    override fun handler(wrapper: IdempotentParamWrapper) {
        val uniqueKey = wrapper.idempotent!!.uniqueKeyPrefix + wrapper.lockKey
        val setIfAbsent = (distributedCache.getInstance() as StringRedisTemplate)
            .opsForValue()
            .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.code)
        if(setIfAbsent != null && !setIfAbsent) {
            val consumeStatus = distributedCache.get(uniqueKey, String::class.java)
            val error = IdempotentMQConsumeStatusEnum.isError(consumeStatus!!)
            LogUtil.getLog(wrapper.joinPoint!!).warn(
                "[$uniqueKey] MQ repeated consumption, " +
                "${if (error) "Wait for the client to delay consumption" else "Status is completed"}."
            )
            throw RepeatConsumptionException(error)
        }
        IdempotentContext.put(WRAPPER, wrapper)
    }

    /**
     * 处理幂等性执行中的异常
     * 该方法在操作执行出现异常时调用，主要负责清理分布式缓存中的相关状态
     */
    override fun exceptionProcessing() {
        val wrapper = IdempotentContext.getKey(WRAPPER) as IdempotentParamWrapper
        val idempotent = wrapper.idempotent
        val uniqueKey = idempotent!!.uniqueKeyPrefix + wrapper.lockKey
        try {
            distributedCache.delete(uniqueKey)
        } catch (ex: Throwable) {
            LogUtil.getLog(wrapper.joinPoint!!).error("[$uniqueKey] Failed to del MQ anti-heavy token.")
        }
    }

    companion object {
        private const val WRAPPER: String = "wrapper:spEL:MQ"
    }
    /**
     * 处理幂等性执行后的逻辑
     * 该方法在操作执行成功后调用，主要负责更新分布式缓存中的消费状态
     */
    override fun postProcessing() {
        val wrapper = IdempotentContext.getKey(WRAPPER) as IdempotentParamWrapper
        val idempotent = wrapper.idempotent
        val uniqueKey = idempotent!!.uniqueKeyPrefix + wrapper.lockKey
        try {
            distributedCache.put(
                uniqueKey,
                IdempotentMQConsumeStatusEnum.CONSUMED.code,
                idempotent.keyTimeout,
                TimeUnit.SECONDS
            )
        } catch (ex: Throwable) {
            LogUtil.getLog(wrapper.joinPoint!!).error("[$uniqueKey] Failed to set MQ anti-heavy token.")
        }
    }


}

