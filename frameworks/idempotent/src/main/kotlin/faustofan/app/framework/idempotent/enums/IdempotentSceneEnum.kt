package faustofan.app.framework.idempotent.enums

/**
 * 幂等场景枚举
 */
enum class IdempotentSceneEnum {
    /**
     * RestApi场景
     */
    RESTAPI,

    /**
     * 消息队列场景
     */
    MQ
} 