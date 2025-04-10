package faustofan.app.framework.web.exception

import faustofan.app.framework.web.enums.ErrorCode

/**
 * 应用程序异常接口
 * 作为ServiceException和ClientException的抽象
 */
abstract class AppException(
    val code: String,
    override val message: String,
    throwable: Throwable? = null
) : RuntimeException(message, throwable) {
    val errCode: ErrorCode = ErrorCode.fromCode(code)
}