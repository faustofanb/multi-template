package faustofan.app.framework.idempotent.core

import faustofan.app.framework.common.annotation.NoArgConstructor
import faustofan.app.framework.idempotent.annotation.Idempotent
import org.aspectj.lang.ProceedingJoinPoint

/**
 * 数据类封装了幂等性操作的参数.
 * 这个类主要用于在执行幂等性操作时，传递必要的参数，
 * 包括幂等注解对象，AOP切点对象以及用于加锁的键.
 *
 * @param idempotent 幂等注解对象，用于获取幂等性操作的相关信息.
 * @param joinPoint AOP切点对象，用于获取当前操作的上下文信息.
 * @param lockKey 用于加锁的键，保证并发下同一操作只执行一次.
 */
data class IdempotentParamWrapper(
    var idempotent: Idempotent? = null,
    var joinPoint: ProceedingJoinPoint? = null,
    var lockKey: String? = null
)

