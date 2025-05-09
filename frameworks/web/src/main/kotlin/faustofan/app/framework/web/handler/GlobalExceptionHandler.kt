package faustofan.app.framework.web.handler

import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.AppException
import faustofan.app.framework.web.exception.ClientException
import faustofan.app.framework.web.exception.ServiceException
import faustofan.app.framework.web.result.CommonResp
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.NoHandlerFoundException

/**
 * 全局异常处理器
 */
@RestControllerAdvice
class GlobalExceptionHandler {
	private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

	/**
	 * 处理应用程序异常
	 */
	@ExceptionHandler(AppException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleAppException(request: HttpServletRequest, exception: AppException): CommonResp<Nothing> {
		val status = when (exception) {
			is ClientException -> HttpStatus.BAD_REQUEST
			is ServiceException -> HttpStatus.INTERNAL_SERVER_ERROR
			else -> HttpStatus.INTERNAL_SERVER_ERROR
		}

		when (exception) {
			is ClientException -> logger.warn("[${status.reasonPhrase}]${exception::class.simpleName}: ${exception.message}")
			else -> logger.error("[${status.reasonPhrase}]${exception::class.simpleName}: ${exception.message}")
		}


		return CommonResp.appError(exception)
	}

	/**
	 * 处理客户端异常
	 */
	@ExceptionHandler(ClientException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleClientException(request: HttpServletRequest, exception: ClientException): CommonResp<Nothing> {
		logger.warn("客户端异常: {}", exception.message)
		return CommonResp.clientError(exception)
	}

	/**
	 * 处理服务端异常
	 */
	@ExceptionHandler(ServiceException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	fun handleServiceException(request: HttpServletRequest, exception: ServiceException): CommonResp<Nothing> {
		logger.error("服务端异常: {}", exception.message, exception)
		return CommonResp.serviceError(exception)
	}

	/**
	 * 处理参数校验异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleMethodArgumentNotValidException(
		request: HttpServletRequest,
		exception: MethodArgumentNotValidException
	): CommonResp<Map<String, String>> {
		val errors = exception.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "参数错误") }
		logger.warn("参数校验异常: {}", errors)
		return CommonResp(
			ErrorCode.PARAM_VALIDATION_FAILED.code,
			"参数校验失败",
			errors
		)
	}

	/**
	 * 处理参数绑定异常
	 */
	@ExceptionHandler(BindException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleBindException(
		request: HttpServletRequest,
		exception: BindException
	): CommonResp<Map<String, String>> {
		val errors = exception.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "参数错误") }
		logger.warn("参数绑定异常: {}", errors)
		return CommonResp(
			ErrorCode.PARAM_VALIDATION_FAILED.code,
			"参数绑定失败",
			errors
		)
	}

	/**
	 * 处理参数类型不匹配异常
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleMethodArgumentTypeMismatchException(
		request: HttpServletRequest,
		exception: MethodArgumentTypeMismatchException
	): CommonResp<Nothing> {
		logger.warn("参数类型不匹配: {}", exception.message)
		return CommonResp.error(
			ErrorCode.INVALID_PARAM.code,
			"参数类型不匹配: ${exception.name}"
		)
	}

	/**
	 * 处理缺少请求参数异常
	 */
	@ExceptionHandler(MissingServletRequestParameterException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleMissingServletRequestParameterException(
		request: HttpServletRequest,
		exception: MissingServletRequestParameterException
	): CommonResp<Nothing> {
		logger.warn("缺少请求参数: {}", exception.message)
		return CommonResp.error(
			ErrorCode.MISSING_REQUIRED_PARAM.code,
			"缺少请求参数: ${exception.parameterName}"
		)
	}

	/**
	 * 处理请求方法不支持异常
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	fun handleHttpRequestMethodNotSupportedException(
		request: HttpServletRequest,
		exception: HttpRequestMethodNotSupportedException
	): CommonResp<Nothing> {
		logger.warn("请求方法不支持: {}", exception.message)
		return CommonResp.error(
			ErrorCode.METHOD_NOT_ALLOWED.code,
			"请求方法不支持: ${exception.method}"
		)
	}

	/**
	 * 处理请求资源不存在异常
	 */
	@ExceptionHandler(NoHandlerFoundException::class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	fun handleNoHandlerFoundException(
		request: HttpServletRequest,
		exception: NoHandlerFoundException
	): CommonResp<Nothing> {
		logger.warn("请求资源不存在: {}", exception.message)
		return CommonResp.error(
			ErrorCode.NOT_FOUND.code,
			"请求资源不存在: ${exception.requestURL}"
		)
	}

	/**
	 * 处理请求体不可读异常
	 */
	@ExceptionHandler(HttpMessageNotReadableException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleHttpMessageNotReadableException(
		request: HttpServletRequest,
		exception: HttpMessageNotReadableException
	): CommonResp<Nothing> {
		logger.warn("请求体不可读: {}", exception.message)
		return CommonResp.error(
			ErrorCode.INVALID_PARAM.code,
			"请求体不可读"
		)
	}

	/**
	 * 处理文件上传大小超限异常
	 */
	@ExceptionHandler(MaxUploadSizeExceededException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleMaxUploadSizeExceededException(
		request: HttpServletRequest,
		exception: MaxUploadSizeExceededException
	): CommonResp<Nothing> {
		logger.warn("文件上传大小超限: {}", exception.message)
		return CommonResp.error(
			ErrorCode.USER_UPLOAD_FILE_SIZE_ERROR.code,
			"文件上传大小超限"
		)
	}

	/**
	 * 处理其他未知异常
	 */
	@ExceptionHandler(Exception::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	fun handleException(request: HttpServletRequest, exception: Exception): CommonResp<Nothing> {
		// 对异常信息进行脱敏处理，避免敏感信息泄露
		val sanitizedMessage = "An unexpected error occurred"

		// 记录更多上下文信息，便于问题定位
		logger.error(
			"未知异常: {}, URL: {}, Method: {}",
			sanitizedMessage,
			request.requestURL,
			request.method,
			exception
		)

		return CommonResp.error(ErrorCode.SYSTEM_ERROR.code, sanitizedMessage)
	}

} 