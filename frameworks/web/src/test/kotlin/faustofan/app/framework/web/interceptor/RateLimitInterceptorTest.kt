package faustofan.app.framework.web.interceptor

import faustofan.app.framework.web.exception.ServiceException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

class RateLimitInterceptorTest {

    private lateinit var interceptor: RateLimitInterceptor
    private lateinit var redisTemplate: StringRedisTemplate
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var handler: Any

    @BeforeEach
    @Suppress("UNCHECKED_CAST")
    fun setUp() {
        redisTemplate = mock(StringRedisTemplate::class.java)
        valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        handler = mock(Any::class.java)

        // 模拟请求头
        `when`(request.remoteAddr).thenReturn("127.0.0.1")
    }

    @Test
    fun `test preHandle allows request when under limit`() {
        // 设置模拟行为
        `when`(valueOperations.increment(anyString())).thenReturn(1L)
        
        // 创建拦截器，设置较小的限制以便测试
        interceptor = RateLimitInterceptor(redisTemplate, 10)

        // 执行preHandle
        val result = interceptor.preHandle(request, response, handler)

        // 验证结果
        assertTrue(result)
        verify(redisTemplate).expire(anyString(), eq(1L), eq(TimeUnit.MINUTES))
    }

    @Test
    fun `test preHandle blocks request when over limit`() {
        // 设置模拟行为，返回超过限制的值
        `when`(valueOperations.increment(anyString())).thenReturn(11L)
        
        // 创建拦截器，设置较小的限制以便测试
        interceptor = RateLimitInterceptor(redisTemplate, 10)

        // 执行preHandle并验证抛出异常
        val exception = assertThrows(ServiceException::class.java) {
            interceptor.preHandle(request, response, handler)
        }

        // 验证异常信息
        assertEquals("请求过于频繁，请稍后再试", exception.message)
    }

    @Test
    fun `test preHandle with different IP addresses`() {
        // 设置模拟行为
        `when`(valueOperations.increment(anyString())).thenReturn(1L)
        
        // 创建拦截器
        interceptor = RateLimitInterceptor(redisTemplate, 10)

        // 模拟不同IP的请求
        `when`(request.remoteAddr).thenReturn("127.0.0.1")
        assertTrue(interceptor.preHandle(request, response, handler))

        `when`(request.remoteAddr).thenReturn("127.0.0.2")
        assertTrue(interceptor.preHandle(request, response, handler))

        // 验证每个IP都有独立的计数器
        verify(valueOperations, times(2)).increment(anyString())
    }

    @Test
    fun `test preHandle with X-Forwarded-For header`() {
        // 设置模拟行为
        `when`(valueOperations.increment(anyString())).thenReturn(1L)
        `when`(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1")
        
        // 创建拦截器
        interceptor = RateLimitInterceptor(redisTemplate, 10)

        // 执行preHandle
        val result = interceptor.preHandle(request, response, handler)

        // 验证结果
        assertTrue(result)
        verify(valueOperations).increment(contains("192.168.1.1"))
    }
} 