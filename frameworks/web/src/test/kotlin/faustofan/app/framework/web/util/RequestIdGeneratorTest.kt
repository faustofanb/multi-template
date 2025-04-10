package faustofan.app.framework.web.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RequestIdGeneratorTest {

    @Test
    fun `test generateRequestId format`() {
        val requestId = RequestIdGenerator.generateRequestId()
        
        // 验证生成的请求ID格式
        assertNotNull(requestId)
        assertEquals(32, requestId.length) // UUID去掉横线后的长度
        assertTrue(requestId.matches(Regex("[0-9a-f]{32}"))) // 只包含小写十六进制字符
    }

    @Test
    fun `test generateRequestId uniqueness`() {
        val requestIds = (1..1000).map { RequestIdGenerator.generateRequestId() }.toSet()
        
        // 验证生成的请求ID都是唯一的
        assertEquals(1000, requestIds.size)
    }
} 