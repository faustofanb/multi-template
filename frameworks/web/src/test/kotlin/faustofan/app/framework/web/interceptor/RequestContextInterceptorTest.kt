package faustofan.app.framework.web.interceptor

import faustofan.app.framework.web.context.RequestContext
import faustofan.app.framework.web.context.RequestContext.Companion.setRequestId
import faustofan.app.framework.web.context.RequestContext.Companion.setStartTime
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

class RequestContextInterceptorTest {

    private lateinit var interceptor: RequestContextInterceptor
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var handler: Any

    @BeforeEach
    fun setUp() {
        interceptor = RequestContextInterceptor()
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        handler = mock(Any::class.java)

        // 模拟请求头
        `when`(request.headerNames).thenReturn(java.util.Collections.enumeration(listOf("User-Agent")))
        `when`(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0")
        `when`(request.remoteAddr).thenReturn("127.0.0.1")

        // 先设置上下文
        RequestContext.getContext().apply {
            setRequestId("test-request-id")
            setStartTime(System.currentTimeMillis())
        }
    }

    @AfterEach
    fun tearDown() {
        RequestContext.clearContext()
    }

    @Test
    fun `test preHandle sets request context`() {
        // 执行preHandle
        val result = interceptor.preHandle(request, response, handler)

        // 验证结果
        assertTrue(result)
        assertNotNull(RequestContext.getRequestId())
        assertTrue(RequestContext.getStartTime()!! > 0)
    }

    @Test
    fun `test afterCompletion clears request context`() {

        // 执行afterCompletion
        interceptor.afterCompletion(request, response, handler, null)

        // 验证上下文已被清除
        assertNull(RequestContext.getRequestId())
        assertNull(RequestContext.getStartTime())
    }

    @Test
    fun `test afterCompletion with exception still clears context`() {

        // 执行afterCompletion，传入一个异常
        val exception = RuntimeException("测试异常")
        interceptor.afterCompletion(request, response, handler, exception)

        // 验证上下文已被清除
        assertNull(RequestContext.getRequestId())
        assertNull(RequestContext.getStartTime())
    }
} 