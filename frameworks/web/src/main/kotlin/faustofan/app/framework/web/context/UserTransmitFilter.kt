package faustofan.app.framework.web.context

import faustofan.app.framework.common.constant.UserConstant.DEFAULT_USER_ID
import faustofan.app.framework.common.constant.UserConstant.DEFAULT_USER_NAME
import faustofan.app.framework.common.constant.UserConstant.USER_ID_KEY
import faustofan.app.framework.common.constant.UserConstant.USER_NAME_KEY
import faustofan.app.framework.common.constant.UserConstant.USER_TOKEN_KEY
import faustofan.app.framework.web.util.SnowflakeIdGenerator
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

/**
 * 用户传输过滤器，用于在请求传递过程中提取和处理用户相关信息。
 * 该过滤器实现了Servlet的Filter接口，用于拦截请求并执行特定的处理逻辑。
 */
@Component
class UserTransmitFilter : Filter {

	/**
	 * 执行过滤操作。
	 * 当请求通过此过滤器时，会尝试从请求头中提取用户ID、用户名、和用户令牌。
	 * 这确保了后续的处理链能够访问到用户的相关信息。
	 *
	 * @param request  Servlet请求对象，用于提取用户信息。
	 * @param response Servlet响应对象，当前过滤器不直接操作响应。
	 * @param chain    过滤器链，用于继续传递请求到下一个过滤器或目标资源。
	 */
	override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		// 将ServletRequest转换为HttpServletRequest，以便使用特定于HTTP的功能。
		val httpServletRequest = request as HttpServletRequest
		// 从请求头中提取用户ID。
		val userId = httpServletRequest.getHeader(USER_ID_KEY)
		val username = httpServletRequest.getHeader(USER_NAME_KEY)
		// 从请求头中提取用户令牌。
		val token = httpServletRequest.getHeader(USER_TOKEN_KEY)
		// 将提取到的用户信息设置到用户上下文中。
		UserContext.setUser(
			UserInfoDTO(
				userId ?: DEFAULT_USER_ID,
				username ?: DEFAULT_USER_NAME,
				SnowflakeIdGenerator.getInstance().nextId(),
				token
			)
		)
		try {
			// 继续请求处理链。
			chain.doFilter(request, response)
		} finally {
			// 请求处理完成后，清除用户上下文中的用户信息。
			UserContext.removeUser()
		}
	}
}
