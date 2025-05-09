package faustofan.app.framework.web.util

import com.alibaba.fastjson2.JSON
import faustofan.app.framework.common.constant.UserConstant.USER_ID_KEY
import faustofan.app.framework.common.constant.UserConstant.USER_NAME_KEY
import faustofan.app.framework.web.context.UserInfoDTO
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import java.util.*

/**
 * JWT工具类，用于生成和解析JSON Web Token。
 */
object JWTUtil {
	// 定义令牌的过期时间1天，单位为秒
	private const val EXPIRATION = 86400L

	// 定义令牌的前缀
	private const val TOKEN_PREFIX = "Bearer "

	// 定义JWT的颁发者
	private const val ISS = "frameworks:web"

	// 定义JWT的秘钥
	const val SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827"

	private val log = LoggerFactory.getLogger(JWTUtil::class.java)
	/**
	 * 生成访问令牌。
	 *
	 * @param userInfo 用户信息，包含用户ID、用户名和真实姓名。
	 * @return 返回生成的访问令牌字符串。
	 */
	fun generateAccessToken(userInfo: UserInfoDTO): String {
		return TOKEN_PREFIX + Jwts.builder()
			.signWith(SignatureAlgorithm.HS512, SECRET)
			.setIssuedAt(Date())
			.setIssuer(ISS)
			.setSubject(
				JSON.toJSONString(
					hashMapOf(
						USER_ID_KEY to userInfo.userId,
						USER_NAME_KEY to userInfo.username,
					)
				)
			)
			.setExpiration(Date(System.currentTimeMillis() + EXPIRATION * 1000))
			.compact()
	}

	/**
	 * 解析JWT令牌并返回用户信息。
	 *
	 * @param jwtToken JWT令牌字符串。
	 * @return 如果令牌有效且未过期，则返回用户信息；否则返回null。
	 */
	fun parseJwtToken(jwtToken: String): UserInfoDTO? {
		if (jwtToken.isNotBlank()) {
			val actualJwtToken = jwtToken.replace(TOKEN_PREFIX, "")
			try {
				val claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(actualJwtToken).body
				val expiration = claims.expiration
				if (expiration.after(Date())) {
					return JSON.parseObject(claims.subject, UserInfoDTO::class.java)
				}
			} catch (_: ExpiredJwtException) {

			} catch (ex: Exception) {
				log.error("JWT Token解析失败，请检查", ex)
			}
		}
		return null
	}
}

