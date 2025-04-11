package faustofan.app.framework.idempotent.enums

/**
 * 幂等方式枚举
 */
enum class IdempotentTypeEnum {
    /**
     * 基于TOKEN
     */
    TOKEN,

    /**
     * 基于请求参数
     */
    PARAM,

    /**
     * 基于SPEL表达式
     */
    SPEL
} 