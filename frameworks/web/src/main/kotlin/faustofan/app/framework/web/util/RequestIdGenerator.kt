package faustofan.app.framework.web.util

import java.util.UUID

/**
 * 请求ID生成器
 * 用于生成唯一的请求ID
 */
object RequestIdGenerator {
    /**
     * 生成请求ID
     * 使用UUID生成唯一的请求ID
     */
    fun generateRequestId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
} 