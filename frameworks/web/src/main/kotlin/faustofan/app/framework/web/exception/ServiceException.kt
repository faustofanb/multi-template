package faustofan.app.framework.web.exception

import faustofan.app.framework.web.enums.ErrorCode

/**
 * 服务端异常
 * 用于表示服务端错误，如系统错误、数据库错误等
 */
class ServiceException(
    code: String,
    message: String,
    cause: Throwable? = null
) : AppException(code, message, cause) {

    companion object {
        /**
         * 创建系统执行超时异常
         */
        fun systemTimeout(message: String = "系统执行超时"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_TIMEOUT.code, message)

        /**
         * 创建系统限流异常
         */
        fun systemLimitError(message: String = "系统限流"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_LIMIT_ERROR.code, message)

        /**
         * 创建系统功能降级异常
         */
        fun systemDegradation(message: String = "系统功能降级"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_DEGRADATION.code, message)

        /**
         * 创建系统资源耗尽异常
         */
        fun systemResourceExhaustion(message: String = "系统资源耗尽"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_RESOURCE_EXHAUSTION.code, message)

        /**
         * 创建系统磁盘空间不足异常
         */
        fun systemDiskFull(message: String = "系统磁盘空间不足"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_DISK_FULL.code, message)

        /**
         * 创建系统内存不足异常
         */
        fun systemMemoryFull(message: String = "系统内存不足"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_MEMORY_FULL.code, message)

        /**
         * 创建系统CPU占用过高异常
         */
        fun systemCpuHigh(message: String = "系统CPU占用过高"): ServiceException =
            ServiceException(ErrorCode.SYSTEM_CPU_HIGH.code, message)

        /**
         * 创建数据库服务异常
         */
        fun databaseError(message: String = "数据库服务异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.DATABASE_ERROR.code, message, cause)

        /**
         * 创建缓存服务异常
         */
        fun cacheError(message: String = "缓存服务异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.CACHE_ERROR.code, message, cause)

        /**
         * 创建消息服务异常
         */
        fun messageServiceError(message: String = "消息服务异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.MESSAGE_SERVICE_ERROR.code, message, cause)

        /**
         * 创建消息发送异常
         */
        fun messagePublishError(message: String = "消息发送异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.MESSAGE_PUBLISH_ERROR.code, message, cause)

        /**
         * 创建消息消费异常
         */
        fun messageConsumeError(message: String = "消息消费异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.MESSAGE_CONSUME_ERROR.code, message, cause)

        /**
         * 创建RPC服务异常
         */
        fun rpcServiceError(message: String = "RPC服务异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.RPC_SERVICE_ERROR.code, message, cause)

        /**
         * 创建RPC服务未找到异常
         */
        fun rpcServiceNotFound(message: String = "RPC服务未找到"): ServiceException =
            ServiceException(ErrorCode.RPC_SERVICE_NOT_FOUND.code, message)

        /**
         * 创建RPC服务未注册异常
         */
        fun rpcServiceNotRegistered(message: String = "RPC服务未注册"): ServiceException =
            ServiceException(ErrorCode.RPC_SERVICE_NOT_REGISTERED.code, message)

        /**
         * 创建接口不存在异常
         */
        fun apiNotExist(message: String = "接口不存在"): ServiceException =
            ServiceException(ErrorCode.API_NOT_EXIST.code, message)

        /**
         * 创建网关服务异常
         */
        fun apiGatewayError(message: String = "网关服务异常", cause: Throwable? = null): ServiceException =
            ServiceException(ErrorCode.API_GATEWAY_ERROR.code, message, cause)

        /**
         * 创建网关响应超时异常
         */
        fun apiGatewayTimeout(message: String = "网关响应超时"): ServiceException =
            ServiceException(ErrorCode.API_GATEWAY_TIMEOUT.code, message)

        /**
         * 创建网关服务未找到异常
         */
        fun apiGatewayNotFound(message: String = "网关服务未找到"): ServiceException =
            ServiceException(ErrorCode.API_GATEWAY_NOT_FOUND.code, message)
    }
} 