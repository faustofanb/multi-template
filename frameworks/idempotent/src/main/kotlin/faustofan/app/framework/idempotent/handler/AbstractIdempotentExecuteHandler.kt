import faustofan.app.framework.idempotent.annotation.Idempotent
import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import org.aspectj.lang.ProceedingJoinPoint

/**
 * AbstractIdempotentExecuteHandler 是 IdempotentExecuteHandler 的抽象实现类。
 * 它提供了一个通用的框架，用于构建幂等执行处理器。
 */
abstract class AbstractIdempotentExecuteHandler: IdempotentExecuteHandler {
    /**
     * 构建封装的请求参数。
     * @param joinPoint 切入点对象，用于获取方法执行的相关信息。
     * @return 返回封装的请求参数对象。
     */
    abstract fun buildWrapper(joinPoint: ProceedingJoinPoint): IdempotentParamWrapper

    /**
     * 执行幂等操作。
     * 该方法首先通过 buildWrapper 构建封装的请求参数，
     * 然后调用 handler 方法处理这些参数。
     * @param joinPoint 切入点对象，用于获取方法执行的相关信息。
     * @param idempotent 幂等对象，包含幂等相关的逻辑和信息。
     */
    override fun execute(joinPoint: ProceedingJoinPoint, idempotent: Idempotent) {
        val idempotentParamWrapper = buildWrapper(joinPoint).apply { this.idempotent = idempotent }
        handler(idempotentParamWrapper)
    }
}