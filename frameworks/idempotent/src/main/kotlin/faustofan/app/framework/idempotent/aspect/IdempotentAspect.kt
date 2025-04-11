package faustofan.app.framework.idempotent.aspect

import faustofan.app.framework.idempotent.annotation.Idempotent
import faustofan.app.framework.idempotent.core.IdempotentContext
import faustofan.app.framework.idempotent.exception.RepeatConsumptionException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

// 定义一个切面类，用于处理幂等性操作
@Aspect
class IdempotentAspect {
    // 当方法上使用了@Idempotent注解时，执行该切面方法
    @Around("@annotation(faustofan.app.framework.idempotent.annotation.Idempotent)")
    fun idempotentHandler(joinPoint: ProceedingJoinPoint): Any? {
        // 获取幂等性注解信息
        val idempotent = getIdempotent(joinPoint)
        // 根据幂等性场景和类型获取对应的执行处理器实例
        val instance = IdempotentExecuteHandlerFactory.getInstance(
            idempotent.scene,
            idempotent.type
        )!!
        var result: Any? = null

        try {
            // 执行前置处理逻辑
            instance.execute(joinPoint, idempotent)
            // 执行目标方法
            result = joinPoint.proceed()
            // 执行后置处理逻辑
            instance.postProcessing()
        } catch (ex: RepeatConsumptionException) {
            // 如果捕获到重复消费异常，且不是错误状态，则返回之前的结果
            if(!ex.error)
                // 已消费
                return result
            // 否则抛出异常, 消费中
            throw ex
        } catch (ex: Throwable) {
            // 执行异常处理逻辑
            instance.exceptionProcessing()
            // 抛出异常
            throw ex
        } finally {
            // 清理幂等性上下文环境
            IdempotentContext.clean()
        }
        return result
    }

    // 伴生对象，用于存放静态方法
    companion object {
        // 获取ProceedingJoinPoint中的@Idempotent注解信息
        fun getIdempotent(joinPoint: ProceedingJoinPoint): Idempotent {
            // 从连接点签名中获取方法签名
            val methodSignature = joinPoint.signature as MethodSignature
            // 通过反射获取目标对象中的目标方法
            val targetMethod = joinPoint.target.javaClass.getDeclaredMethod(
                methodSignature.name,
                *methodSignature.method.parameterTypes
            )
            // 获取目标方法上的@Idempotent注解
            return targetMethod.getAnnotation(Idempotent::class.java)
        }
    }
}