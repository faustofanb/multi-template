package faustofan.app.framework.web.result

import faustofan.app.framework.web.context.UserContext
import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.AppException
import faustofan.app.framework.web.exception.ClientException
import faustofan.app.framework.web.exception.ServiceException
import java.time.LocalDateTime

/**
 * 通用Web响应包装类
 * @param T 响应数据类型
 */
data class CommonResp<T>(
	val code: String,
	val message: String,
	val data: T?,
	val timestamp: LocalDateTime = LocalDateTime.now(),
	val requestId: String? = UserContext.getRequestId().toString(),
) {
    companion object {
        /**
         * 成功响应
         */
        fun <T> success(data: T? = null, message: String = ErrorCode.SUCCESS.message): CommonResp<T> =
            CommonResp(ErrorCode.SUCCESS.code, message, data)

        /**
         * 客户端错误响应
         */
        fun clientError(exception: ClientException): CommonResp<Nothing> =
            CommonResp(exception.code, exception.message, null)

        /**
         * 服务端错误响应
         */
        fun serviceError(exception: ServiceException): CommonResp<Nothing> =
            CommonResp(exception.code, exception.message, null)
            
        /**
         * 应用程序错误响应
         */
        fun appError(exception: AppException): CommonResp<Nothing> =
            CommonResp(exception.code, exception.message, null)

        /**
         * 从Result创建响应
         */
        fun <T, E> fromResult(result: Result<T, E>): CommonResp<*> = when (result) {
            is Result.Ok -> success(result.value)
            is Result.Err -> when (result.error) {
                is AppException -> appError(result.error)
                else -> CommonResp(ErrorCode.SYSTEM_ERROR.code, result.error.toString(), null)
            }
        }

        /**
         * 从Option创建响应
         */
        fun <T> fromOption(option: Option<T>, notFoundMessage: String = "资源不存在"): CommonResp<T> =
            when (option) {
                is Option.Some -> success(option.value)
                is Option.None -> CommonResp(ErrorCode.NOT_FOUND.code, notFoundMessage, null)
            }
            
        /**
         * 从ErrorCode创建响应
         */
        fun <T> fromErrorCode(errorCode: ErrorCode, data: T? = null): CommonResp<T> =
            CommonResp(errorCode.code, errorCode.message, data)
            
        /**
         * 从错误码和消息创建响应
         */
        fun error(code: String, message: String): CommonResp<Nothing> =
            CommonResp(code, message, null)
            
        /**
         * 从异常创建响应
         */
        fun fromException(exception: Throwable): CommonResp<Nothing> =
            CommonResp(ErrorCode.SYSTEM_ERROR.code, exception.message ?: "系统异常", null)
    }
    
    /**
     * 判断是否为成功响应
     */
    fun isSuccess(): Boolean = code == ErrorCode.SUCCESS.code
    
    /**
     * 判断是否为客户端错误
     */
    fun isClientError(): Boolean = ErrorCode.isClientError(code)
    
    /**
     * 判断是否为系统错误
     */
    fun isSystemError(): Boolean = ErrorCode.isSystemError(code)
    
    /**
     * 判断是否为第三方服务错误
     */
    fun isThirdPartyError(): Boolean = ErrorCode.isThirdPartyError(code)
    
    /**
     * 转换为Result
     */
    fun toResult(): Result<T, String> = when {
        isSuccess() -> Result.Ok(data!!)
        else -> Result.Err(message)
    }
    
    /**
     * 转换为Option
     */
    fun toOption(): Option<T> = when {
        isSuccess() -> Option.Some(data!!)
        else -> Option.None
    }
    
    /**
     * 获取数据，如果失败则抛出异常
     */
    fun unwrap(): T = when {
        isSuccess() -> data!!
        else -> throw IllegalStateException("CommonResp.unwrap(): $message")
    }
    
    /**
     * 获取数据，如果失败则抛出带有自定义消息的异常
     */
    fun expect(message: String): T = when {
        isSuccess() -> data!!
        else -> throw IllegalStateException("$message: $message")
    }
    
    /**
     * 获取数据，如果失败则返回默认值
     */
    fun unwrapOr(default: T): T = when {
        isSuccess() -> data!!
        else -> default
    }
    
    /**
     * 获取数据，如果失败则使用给定的函数计算默认值
     */
    fun unwrapOrElse(defaultFn: () -> T): T = when {
        isSuccess() -> data!!
        else -> defaultFn()
    }
    
    /**
     * 获取数据，如果失败则返回null
     */
    fun unwrapOrNull(): T? = when {
        isSuccess() -> data
        else -> null
    }
} 