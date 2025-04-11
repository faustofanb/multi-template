package faustofan.app.framework.idempotent.annotation

import faustofan.app.framework.idempotent.enums.IdempotentSceneEnum
import faustofan.app.framework.idempotent.enums.IdempotentTypeEnum

/**
 * 幂等注解
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
annotation class Idempotent(
    /**
     * 幂等Key，只有在 [Idempotent.type] 为 [IdempotentTypeEnum.SPEL] 时生效
     */
    val key: String = "",
    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    val message: String = "您操作太快，请稍后再试",
    /**
     * 验证幂等类型，支持多种幂等方式
     * RestAPI 建议使用 [IdempotentTypeEnum.TOKEN] 或 [IdempotentTypeEnum.PARAM]
     * 其它类型幂等验证，使用 [IdempotentTypeEnum.SPEL]
     */
    val type: IdempotentTypeEnum = IdempotentTypeEnum.PARAM,
    /**
     * 验证幂等场景，支持多种 [IdempotentSceneEnum]
     */
    val scene: IdempotentSceneEnum = IdempotentSceneEnum.RESTAPI,
    /**
     * 设置防重令牌 Key 前缀，MQ 幂等去重可选设置
     * [IdempotentSceneEnum.MQ] and [IdempotentTypeEnum.SPEL] 时生效
     */
    val uniqueKeyPrefix: String = "",
    /**
     * 设置防重令牌 Key 过期时间，单位秒，默认 1 小时，MQ 幂等去重可选设置
     * [IdempotentSceneEnum.MQ] and [IdempotentTypeEnum.SPEL] 时生效
     */
    val keyTimeout: Long = 3600L
)