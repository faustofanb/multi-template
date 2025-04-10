package faustofan.app.framework.web.exception

import faustofan.app.framework.web.enums.ErrorCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ServiceExceptionTest {

    @Test
    fun `test systemTimeout exception`() {
        val exception = ServiceException.systemTimeout("测试系统超时")
        assertEquals(ErrorCode.SYSTEM_TIMEOUT.code, exception.code)
        assertEquals("测试系统超时", exception.message)
    }

    @Test
    fun `test systemLimitError exception`() {
        val exception = ServiceException.systemLimitError("测试系统限流")
        assertEquals(ErrorCode.SYSTEM_LIMIT_ERROR.code, exception.code)
        assertEquals("测试系统限流", exception.message)
    }

    @Test
    fun `test systemDegradation exception`() {
        val exception = ServiceException.systemDegradation("测试系统降级")
        assertEquals(ErrorCode.SYSTEM_DEGRADATION.code, exception.code)
        assertEquals("测试系统降级", exception.message)
    }

    @Test
    fun `test systemResourceExhaustion exception`() {
        val exception = ServiceException.systemResourceExhaustion("测试资源耗尽")
        assertEquals(ErrorCode.SYSTEM_RESOURCE_EXHAUSTION.code, exception.code)
        assertEquals("测试资源耗尽", exception.message)
    }

    @Test
    fun `test systemDiskFull exception`() {
        val exception = ServiceException.systemDiskFull("测试磁盘空间不足")
        assertEquals(ErrorCode.SYSTEM_DISK_FULL.code, exception.code)
        assertEquals("测试磁盘空间不足", exception.message)
    }

    @Test
    fun `test systemMemoryFull exception`() {
        val exception = ServiceException.systemMemoryFull("测试内存不足")
        assertEquals(ErrorCode.SYSTEM_MEMORY_FULL.code, exception.code)
        assertEquals("测试内存不足", exception.message)
    }

    @Test
    fun `test rpcServiceNotFound exception`() {
        val exception = ServiceException.rpcServiceNotFound("测试RPC服务未找到")
        assertEquals(ErrorCode.RPC_SERVICE_NOT_FOUND.code, exception.code)
        assertEquals("测试RPC服务未找到", exception.message)
    }

    @Test
    fun `test rpcServiceNotRegistered exception`() {
        val exception = ServiceException.rpcServiceNotRegistered("测试RPC服务未注册")
        assertEquals(ErrorCode.RPC_SERVICE_NOT_REGISTERED.code, exception.code)
        assertEquals("测试RPC服务未注册", exception.message)
    }

    @Test
    fun `test apiNotExist exception`() {
        val exception = ServiceException.apiNotExist("测试接口不存在")
        assertEquals(ErrorCode.API_NOT_EXIST.code, exception.code)
        assertEquals("测试接口不存在", exception.message)
    }

    @Test
    fun `test apiGatewayError exception`() {
        val cause = RuntimeException("测试异常")
        val exception = ServiceException.apiGatewayError("测试网关服务异常", cause)
        assertEquals(ErrorCode.API_GATEWAY_ERROR.code, exception.code)
        assertEquals("测试网关服务异常", exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `test apiGatewayTimeout exception`() {
        val exception = ServiceException.apiGatewayTimeout("测试网关响应超时")
        assertEquals(ErrorCode.API_GATEWAY_TIMEOUT.code, exception.code)
        assertEquals("测试网关响应超时", exception.message)
    }

    @Test
    fun `test apiGatewayNotFound exception`() {
        val exception = ServiceException.apiGatewayNotFound("测试网关服务未找到")
        assertEquals(ErrorCode.API_GATEWAY_NOT_FOUND.code, exception.code)
        assertEquals("测试网关服务未找到", exception.message)
    }
} 