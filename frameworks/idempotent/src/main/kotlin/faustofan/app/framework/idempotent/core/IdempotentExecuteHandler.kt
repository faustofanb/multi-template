import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import faustofan.app.framework.idempotent.annotation.Idempotent
import org.aspectj.lang.ProceedingJoinPoint

/**
 * IdempotentExecuteHandler 接口定义了处理幂等执行的核心逻辑。
 * 它提供了处理请求、执行幂等操作、处理异常和执行后处理的方法。
 */
interface IdempotentExecuteHandler {
    /**
     * 处理封装的请求参数。
     * @param wrapper 封装的请求参数对象。
     */
    fun handler(wrapper: IdempotentParamWrapper)

    /**
     * 执行幂等操作。
     * @param joinPoint 切入点对象，用于获取方法执行的相关信息。
     * @param idempotent 幂等对象，包含幂等相关的逻辑和信息。
     */
    fun execute(joinPoint: ProceedingJoinPoint, idempotent: Idempotent)

    /**
     * 处理执行过程中可能出现的异常。
     * 默认实现为空，子类可根据需要重写以处理特定的异常情况。
     */
    fun exceptionProcessing() {}

    /**
     * 执行后处理。
     * 默认实现为空，子类可以重写此方法以执行操作后的必要处理。
     */
    fun postProcessing() {}
}