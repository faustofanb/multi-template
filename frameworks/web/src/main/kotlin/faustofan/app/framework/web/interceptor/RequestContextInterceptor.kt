package faustofan.app.framework.web.interceptor

import faustofan.app.framework.web.context.UserContext
import faustofan.app.framework.web.util.SnowflakeIdGenerator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 请求上下文拦截器
 * 用于在请求开始时设置请求上下文，在请求结束时清除请求上下文
 */
@Component
class RequestContextInterceptor : HandlerInterceptor {

	private val log = LoggerFactory.getLogger(RequestContextInterceptor::class.java)

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		// 打印请求信息
		logRequestInfo(request, UserContext.getRequestId(), UserContext.getUserId() ?: "用户ID未设置")

		return true
	}

	override fun afterCompletion(
		request: HttpServletRequest,
		response: HttpServletResponse,
		handler: Any,
		ex: Exception?
	) {
		try {
			// 打印响应信息
			logResponseInfo(request, response)
		} finally {
			// 清除请求上下文
			UserContext.removeUser()
		}
	}

	/**
	 * 打印请求信息
	 */
	private fun logRequestInfo(request: HttpServletRequest, requestId: Long, userId: String) {
		val sb = StringBuilder()
		sb.append("\n")
			.append("==================== 请求信息 ====================\n")
			.append("请求ID: ").append(requestId).append("\n")
			.append("用户ID: ").append(userId).append("\n")
			.append("请求方法: ").append(request.method).append("\n")
			.append("请求URL: ").append(request.requestURL).append("\n")
			.append("请求URI: ").append(request.requestURI).append("\n")
			.append("查询参数: ").append(request.queryString ?: "无").append("\n")
			.append("客户端IP: ").append(getClientIp(request)).append("\n")
			.append("用户代理: ").append(request.getHeader("User-Agent") ?: "未知").append("\n")
			.append("请求头: ").append(getRequestHeaders(request)).append("\n")
			.append("=================================================")

		log.info(sb.toString())
	}

	/**
	 * 打印响应信息
	 */
	private fun logResponseInfo(request: HttpServletRequest, response: HttpServletResponse) {
		val sb = StringBuilder()
		sb.append("\n")
			.append("==================== 响应信息 ====================\n")
			.append("用户ID: ").append(UserContext.getUserId()).append("\n")
			.append("用户名: ").append(UserContext.getUsername()).append("\n")
			.append("请求URL: ").append(request.requestURL).append("\n")
			.append("响应状态: ").append(response.status).append("\n")
			.append("=================================================")

		log.info(sb.toString())
	}

	/**
	 * 获取客户端IP地址
	 */
	private fun getClientIp(request: HttpServletRequest): String {
		var ip = request.getHeader("X-Forwarded-For")
		if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
			ip = request.getHeader("Proxy-Client-IP")
		}
		if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
			ip = request.getHeader("WL-Proxy-Client-IP")
		}
		if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
			ip = request.getHeader("HTTP_CLIENT_IP")
		}
		if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR")
		}
		if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
			ip = request.remoteAddr
		}
		return ip
	}

	/**
	 * 获取请求头信息
	 */
	private fun getRequestHeaders(request: HttpServletRequest): String {
		val headerNames = request.headerNames
		val headers = StringBuilder()
		while (headerNames.hasMoreElements()) {
			val headerName = headerNames.nextElement()
			val headerValue = request.getHeader(headerName)
			headers.append(headerName).append(": ").append(headerValue).append(", ")
		}
		return headers.toString().trimEnd(',', ' ')
	}
} 