package faustofan.app.framework.web.context

/**
 * 用户信息数据传输对象类
 * 用于在系统中传递和存储用户的基本信息。
 *
 * @param userId 用户的唯一标识符，可能为空。
 * @param username 用户的登录名，可能为空。
 * @param requestId 请求ID。
 * @param token 用户的认证令牌，用于身份验证，可能为空。
 */
data class UserInfoDTO(
	val userId: String? = null,
	val username: String? = null,
	val requestId: Long? = null,
	val token: String? = null
)
