package faustofan.app.framework.web.exception

import faustofan.app.framework.web.enums.ErrorCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ClientExceptionTest {

    @Test
    fun `test invalidParam exception`() {
        val exception = ClientException.invalidParam("测试参数错误")
        assertEquals(ErrorCode.INVALID_PARAM.code, exception.code)
        assertEquals("测试参数错误", exception.message)
    }

    @Test
    fun `test missingParam exception`() {
        val exception = ClientException.missingParam("username")
        assertEquals(ErrorCode.MISSING_REQUIRED_PARAM.code, exception.code)
        assertEquals("缺少参数: username", exception.message)
    }

    @Test
    fun `test validationFailed exception`() {
        val exception = ClientException.validationFailed("测试校验失败")
        assertEquals(ErrorCode.PARAM_VALIDATION_FAILED.code, exception.code)
        assertEquals("测试校验失败", exception.message)
    }

    @Test
    fun `test unauthorized exception`() {
        val exception = ClientException.unauthorized("测试未授权")
        assertEquals(ErrorCode.UNAUTHORIZED.code, exception.code)
        assertEquals("测试未授权", exception.message)
    }

    @Test
    fun `test forbidden exception`() {
        val exception = ClientException.forbidden("测试禁止访问")
        assertEquals(ErrorCode.FORBIDDEN.code, exception.code)
        assertEquals("测试禁止访问", exception.message)
    }

    @Test
    fun `test notFound exception`() {
        val exception = ClientException.notFound("测试资源不存在")
        assertEquals(ErrorCode.NOT_FOUND.code, exception.code)
        assertEquals("测试资源不存在", exception.message)
    }

    @Test
    fun `test methodNotAllowed exception`() {
        val exception = ClientException.methodNotAllowed("POST")
        assertEquals(ErrorCode.METHOD_NOT_ALLOWED.code, exception.code)
        assertEquals("方法不允许: POST", exception.message)
    }

    @Test
    fun `test timeout exception`() {
        val exception = ClientException.timeout("测试请求超时")
        assertEquals(ErrorCode.REQUEST_TIMEOUT.code, exception.code)
        assertEquals("测试请求超时", exception.message)
    }

    @Test
    fun `test tooManyRequests exception`() {
        val exception = ClientException.tooManyRequests("测试请求次数超出限制")
        assertEquals(ErrorCode.TOO_MANY_REQUESTS.code, exception.code)
        assertEquals("测试请求次数超出限制", exception.message)
    }

    @Test
    fun `test uploadFileTypeError exception`() {
        val exception = ClientException.uploadFileTypeError("测试文件类型不匹配")
        assertEquals(ErrorCode.USER_UPLOAD_FILE_TYPE_ERROR.code, exception.code)
        assertEquals("测试文件类型不匹配", exception.message)
    }

    @Test
    fun `test uploadFileSizeError exception`() {
        val exception = ClientException.uploadFileSizeError("测试文件大小超出限制")
        assertEquals(ErrorCode.USER_UPLOAD_FILE_SIZE_ERROR.code, exception.code)
        assertEquals("测试文件大小超出限制", exception.message)
    }

    @Test
    fun `test uploadFileEmpty exception`() {
        val exception = ClientException.uploadFileEmpty("测试文件为空")
        assertEquals(ErrorCode.USER_UPLOAD_FILE_EMPTY.code, exception.code)
        assertEquals("测试文件为空", exception.message)
    }
} 