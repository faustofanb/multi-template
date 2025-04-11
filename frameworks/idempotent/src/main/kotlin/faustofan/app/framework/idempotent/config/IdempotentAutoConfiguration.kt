package faustofan.app.framework.idempotent.config

import IdempotentProperties
import faustofan.app.framework.idempotent.aspect.IdempotentAspect
import faustofan.app.framework.idempotent.controller.IdempotentTokenController
import faustofan.app.framework.idempotent.handler.*
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

// 启用配置属性
@EnableConfigurationProperties(IdempotentProperties::class)
class IdempotentAutoConfiguration {

    /**
     * 创建并返回一个IdempotentAspect实例
     * 该实例用于处理幂等性的切面逻辑
     *
     * @return IdempotentAspect实例
     */
    @Bean
    fun idempotentAspect(): IdempotentAspect = IdempotentAspect()

    /**
     * 当系统中未定义IdempotentParamService时，创建并返回一个IdempotentParamExecuteHandler实例
     * 该实例使用RedissonClient来处理请求参数的幂等性执行逻辑
     *
     * @param redissonClient Redisson客户端，用于分布式锁的实现
     * @return IdempotentParamService实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun idempotentParamExecuteHandler(redissonClient: RedissonClient): IdempotentParamService
        = IdempotentParamExecuteHandler(redissonClient)

    /**
     * 当系统中未定义IdempotentTokenService时，创建并返回一个IdempotentTokenExecuteHandler实例
     * 该实例使用分布式缓存和IdempotentProperties配置来处理令牌的幂等性执行逻辑
     *
     * @param distributedCache 分布式缓存，用于存储和检索幂等性令牌
     * @param idempotentProperties 幂等性配置属性
     * @return IdempotentTokenService实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun idempotentTokenExecuteHandler(
        distributedCache: DistributedCache,
        idempotentProperties: IdempotentProperties
    ): IdempotentTokenService
        = IdempotentTokenExecuteHandler(distributedCache, idempotentProperties)

    /**
     * 创建并返回一个IdempotentTokenController实例
     * 该实例用于处理与幂等性令牌相关的控制器逻辑
     *
     * @param idempotentTokenService 幂等性令牌服务，用于处理令牌的验证和执行
     * @return IdempotentTokenController实例
     */
    @Bean
    fun idempotentTokenController(idempotentTokenService: IdempotentTokenService): IdempotentTokenController
        = IdempotentTokenController(idempotentTokenService)

    /**
     * 当系统中未定义IdempotentSpELService时，创建并返回一个IdempotentSpELByRestAPIExecuteHandler实例
     * 该实例使用RedissonClient来通过REST API处理SpEL（表达式语言）的幂等性执行逻辑
     *
     * @param redissonClient Redisson客户端，用于分布式锁的实现
     * @return IdempotentSpELService实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun idempotentSpELByRestAPIExecuteHandler(redissonClient: RedissonClient): IdempotentSpELService
        = IdempotentSpELByRestAPIExecuteHandler(redissonClient)

    /**
     * 当系统中未定义IdempotentSpELService时，创建并返回一个IdempotentSpELByMQExecuteHandler实例
     * 该实例使用分布式缓存来通过消息队列处理SpEL（表达式语言）的幂等性执行逻辑
     *
     * @param distributedCache 分布式缓存，用于存储和检索幂等性相关的数据
     * @return IdempotentSpELService实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun idempotentSpELByMQExecuteHandler(distributedCache: DistributedCache): IdempotentSpELService
        = IdempotentSpELByMQExecuteHandler(distributedCache = distributedCache)

}