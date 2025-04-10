package faustofan.app.framework.web.context

import faustofan.app.framework.web.util.SnowflakeIdGenerator

/**
 * 请求上下文
 * 用于存储请求相关的信息
 */
class RequestContext private constructor() {

    private var requestId: String? = null

    private var startTime: Long? = null

    companion object {

        private val contextHolder = ThreadLocal.withInitial { RequestContext() }

        fun getContext(): RequestContext {
            return contextHolder.get()
        }

        fun setContext(context: RequestContext) {
            contextHolder.set(context)
        }

        fun clearContext() {
            contextHolder.remove()
        }

        fun generateRequestId(): String {
            return SnowflakeIdGenerator.getInstance().nextId().toString()
        }

        fun getRequestId(): String? {
            return contextHolder.get()?.requestId
        }

        fun setRequestId(requestId: String) {
            contextHolder.get()?.requestId = requestId
        }

        fun getStartTime(): Long? {
            return contextHolder.get()?.startTime
        }

        fun setStartTime(startTime: Long) {
            contextHolder.get()?.startTime = startTime
        }
    }
}
