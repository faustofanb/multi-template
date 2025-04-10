package faustofan.app.framework.web.result

import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.ClientException
import faustofan.app.framework.web.exception.ServiceException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CommonRespTest {

    @Test
    fun `test success response`() {
        val data = "测试数据"
        val resp = CommonResp.success(data)
        
        assertEquals(ErrorCode.SUCCESS.code, resp.code)
        assertEquals(ErrorCode.SUCCESS.message, resp.message)
        assertEquals(data, resp.data)
        assertTrue(resp.isSuccess())
    }

    @Test
    fun `test client error response`() {
        val exception = ClientException.invalidParam("测试参数错误")
        val resp = CommonResp.clientError(exception)
        
        assertEquals(exception.code, resp.code)
        assertEquals(exception.message, resp.message)
        assertNull(resp.data)
        assertTrue(resp.isClientError())
    }

    @Test
    fun `test service error response`() {
        val exception = ServiceException.systemTimeout("测试系统超时")
        val resp = CommonResp.serviceError(exception)
        
        assertEquals(exception.code, resp.code)
        assertEquals(exception.message, resp.message)
        assertNull(resp.data)
        assertTrue(resp.isSystemError())
    }

    @Test
    fun `test fromResult with Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        val resp = CommonResp.fromResult(result)
        
        assertEquals(ErrorCode.SUCCESS.code, resp.code)
        assertEquals(data, resp.data)
    }

    @Test
    fun `test fromResult with Err`() {
        val exception = ClientException.invalidParam("测试参数错误")
        val result = Result.Err(exception)
        val resp = CommonResp.fromResult(result)
        
        assertEquals(exception.code, resp.code)
        assertEquals(exception.message, resp.message)
        assertNull(resp.data)
    }

    @Test
    fun `test fromOption with Some`() {
        val data = "测试数据"
        val option = Option.Some(data)
        val resp = CommonResp.fromOption(option)
        
        assertEquals(ErrorCode.SUCCESS.code, resp.code)
        assertEquals(data, resp.data)
    }

    @Test
    fun `test fromOption with None`() {
        val option = Option.None
        val resp = CommonResp.fromOption(option)
        
        assertEquals(ErrorCode.NOT_FOUND.code, resp.code)
        assertEquals("资源不存在", resp.message)
        assertNull(resp.data)
    }

    @Test
    fun `test fromErrorCode`() {
        val resp = CommonResp.fromErrorCode<Nothing>(ErrorCode.SYSTEM_ERROR)
        
        assertEquals(ErrorCode.SYSTEM_ERROR.code, resp.code)
        assertEquals(ErrorCode.SYSTEM_ERROR.message, resp.message)
        assertNull(resp.data)
    }

    @Test
    fun `test error response`() {
        val code = "TEST_ERROR"
        val message = "测试错误"
        val resp = CommonResp.error(code, message)
        
        assertEquals(code, resp.code)
        assertEquals(message, resp.message)
    }

    @Test
    fun `test fromException`() {
        val exception = RuntimeException("测试异常")
        val resp = CommonResp.fromException(exception)
        
        assertEquals(ErrorCode.SYSTEM_ERROR.code, resp.code)
        assertEquals(exception.message, resp.message)
        assertNull(resp.data)
    }

    @Test
    fun `test unwrap success`() {
        val data = "测试数据"
        val resp = CommonResp.success(data)
        
        assertEquals(data, resp.unwrap())
    }

    @Test
    fun `test unwrap error`() {
        val resp = CommonResp.error("TEST_ERROR", "测试错误")
        
        assertThrows(IllegalStateException::class.java) {
            resp.unwrap()
        }
    }

    @Test
    fun `test unwrapOr success`() {
        val data = "测试数据"
        val resp = CommonResp.success(data)
        val default = "默认数据"
        
        assertEquals(data, resp.unwrapOr(default))
    }

    @Test
    fun `test unwrapOr error`() {
        val resp  = CommonResp.error("TEST_ERROR", "测试错误") as CommonResp<String>
        val default = "默认数据"
        
        assertEquals(default, resp.unwrapOr(default))
    }

    @Test
    fun `test unwrapOrElse success`() {
        val data = "测试数据"
        val resp = CommonResp.success(data)
        
        assertEquals(data, resp.unwrapOrElse { "默认数据" })
    }

    @Test
    fun `test unwrapOrElse error`() {
        val resp = CommonResp.error("TEST_ERROR", "测试错误") as CommonResp<String>
        
        assertEquals("默认数据", resp.unwrapOrElse { "默认数据" })
    }

    @Test
    fun `test unwrapOrNull success`() {
        val data = "测试数据"
        val resp = CommonResp.success(data)
        
        assertEquals(data, resp.unwrapOrNull())
    }

    @Test
    fun `test unwrapOrNull error`() {
        val resp = CommonResp.error("TEST_ERROR", "测试错误")
        
        assertNull(resp.unwrapOrNull())
    }
} 