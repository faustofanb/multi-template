package faustofan.app.framework.web.context

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RequestContextTest {

    @AfterEach
    fun tearDown() {
        RequestContext.clearContext()
    }

    @Test
    fun `test set and get request ID`() {
        val requestId = "test-request-id"
        RequestContext.setRequestId(requestId)
        
        assertEquals(requestId, RequestContext.getRequestId())
    }

    @Test
    fun `test set and get start time`() {
        val startTime = System.currentTimeMillis()
        RequestContext.setStartTime(startTime)
        
        assertEquals(startTime, RequestContext.getStartTime())
    }

    @Test
    fun `test clear context`() {
        // 先设置一些值
        RequestContext.setRequestId("test-request-id")
        RequestContext.setStartTime(System.currentTimeMillis())
        
        // 清除上下文
        RequestContext.clearContext()
        
        // 验证值已被清除
        assertEquals("", RequestContext.getRequestId())
        assertEquals(0L, RequestContext.getStartTime())
    }

    @Test
    fun `test generate request ID`() {
        val requestId = RequestContext.generateRequestId()
        
        assertNotNull(requestId)
        assertTrue(requestId.isNotEmpty())
    }

    @Test
    fun `test context isolation between threads`() {
        // 在主线程中设置值
        RequestContext.setRequestId("main-thread-id")
        RequestContext.setStartTime(1000L)
        
        // 在另一个线程中设置不同的值
        val thread = Thread {
            RequestContext.setRequestId("other-thread-id")
            RequestContext.setStartTime(2000L)
            
            // 验证在另一个线程中的值
            assertEquals("other-thread-id", RequestContext.getRequestId())
            assertEquals(2000L, RequestContext.getStartTime())
        }
        thread.start()
        thread.join()
        
        // 验证主线程中的值保持不变
        assertEquals("main-thread-id", RequestContext.getRequestId())
        assertEquals(1000L, RequestContext.getStartTime())
    }
} 