package faustofan.app.framework.web.interceptor

import faustofan.app.framework.web.exception.ServiceException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.TimeUnit

/**
 * 请求限流拦截器
 * 基于Redis实现分布式限流
 */
class RateLimitInterceptor(
    private val redisTemplate: StringRedisTemplate,
    private val limitPerMinute: Int = 60
) : HandlerInterceptor {
    
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = getClientIp(request)
        val key = "rate_limit:$ip"
        
        // 获取当前时间窗口内的请求数
        val count = redisTemplate.opsForValue().increment(key) ?: 0
        
        // 如果是第一次请求，设置过期时间
        if (count == 1L) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES)
        }
        
        // 如果超过限制，抛出异常
        if (count > limitPerMinute) {
            throw ServiceException.systemLimitError("请求过于频繁，请稍后再试")
        }
        
        return true
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
} 