package faustofan.app.framework.web.exception

import faustofan.app.framework.web.enums.ErrorCode

/**
 * 客户端异常
 * 用于表示客户端错误，如参数错误、权限不足等
 */
class ClientException(
    code: String,
    message: String,
    cause: Throwable? = null
) : AppException(code, message, cause) {

    companion object {
        /**
         * 创建参数错误异常
         */
        fun invalidParam(message: String = "参数错误"): ClientException =
            ClientException(ErrorCode.INVALID_PARAM.code, message)

        /**
         * 创建缺少参数异常
         */
        fun missingParam(paramName: String): ClientException =
            ClientException(ErrorCode.MISSING_REQUIRED_PARAM.code, "缺少参数: $paramName")

        /**
         * 创建参数校验失败异常
         */
        fun validationFailed(message: String = "参数校验失败"): ClientException =
            ClientException(ErrorCode.PARAM_VALIDATION_FAILED.code, message)

        /**
         * 创建未授权异常
         */
        fun unauthorized(message: String = "未授权"): ClientException =
            ClientException(ErrorCode.UNAUTHORIZED.code, message)

        /**
         * 创建禁止访问异常
         */
        fun forbidden(message: String = "禁止访问"): ClientException =
            ClientException(ErrorCode.FORBIDDEN.code, message)

        /**
         * 创建资源不存在异常
         */
        fun notFound(message: String = "资源不存在"): ClientException =
            ClientException(ErrorCode.NOT_FOUND.code, message)

        /**
         * 创建方法不允许异常
         */
        fun methodNotAllowed(method: String): ClientException =
            ClientException(ErrorCode.METHOD_NOT_ALLOWED.code, "方法不允许: $method")

        /**
         * 创建请求超时异常
         */
        fun timeout(message: String = "请求超时"): ClientException =
            ClientException(ErrorCode.REQUEST_TIMEOUT.code, message)

        /**
         * 创建请求次数超出限制异常
         */
        fun tooManyRequests(message: String = "请求次数超出限制"): ClientException =
            ClientException(ErrorCode.TOO_MANY_REQUESTS.code, message)

        /**
         * 创建文件上传类型错误异常
         */
        fun uploadFileTypeError(message: String = "文件类型不匹配"): ClientException =
            ClientException(ErrorCode.USER_UPLOAD_FILE_TYPE_ERROR.code, message)

        /**
         * 创建文件上传大小错误异常
         */
        fun uploadFileSizeError(message: String = "文件大小超出限制"): ClientException =
            ClientException(ErrorCode.USER_UPLOAD_FILE_SIZE_ERROR.code, message)

        /**
         * 创建文件上传为空异常
         */
        fun uploadFileEmpty(message: String = "文件为空"): ClientException =
            ClientException(ErrorCode.USER_UPLOAD_FILE_EMPTY.code, message)
    }
} 