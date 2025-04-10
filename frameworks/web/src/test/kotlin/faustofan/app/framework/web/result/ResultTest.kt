package faustofan.app.framework.web.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ResultTest {

    @Test
    fun `test Ok creation`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        
        assertTrue(result.isOk())
        assertFalse(result.isErr())
        assertEquals(data, result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun `test Err creation`() {
        val error = "测试错误"
        val result = Result.Err(error)
        
        assertFalse(result.isOk())
        assertTrue(result.isErr())
        assertNull(result.getOrNull())
        assertEquals(error, result.errorOrNull())
    }

    @Test
    fun `test onSuccess callback`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        var callbackCalled = false
        
        result.onSuccess { 
            assertEquals(data, it)
            callbackCalled = true
        }
        
        assertTrue(callbackCalled)
    }

    @Test
    fun `test onFailure callback`() {
        val error = "测试错误"
        val result = Result.Err(error)
        var callbackCalled = false
        
        result.onFailure { 
            assertEquals(error, it)
            callbackCalled = true
        }
        
        assertTrue(callbackCalled)
    }

    @Test
    fun `test map on Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        val mapped = result.map { it.length }
        
        assertTrue(mapped.isOk())
        assertEquals(data.length, mapped.getOrNull())
    }

    @Test
    fun `test map on Err`() {
        val error = "测试错误"
        val result: Result<String, String> = Result.Err(error)
        val mapped = result.map { it.length }
        
        assertTrue(mapped.isErr())
        assertEquals(error, mapped.errorOrNull())
    }

    @Test
    fun `test mapError on Ok`() {
        val data = "测试数据"
        val result: Result<String, String> = Result.Ok(data)
        val mapped = result.mapError { it.length }
        
        assertTrue(mapped.isOk())
        assertEquals(data, mapped.getOrNull())
    }

    @Test
    fun `test mapError on Err`() {
        val error = "测试错误"
        val result = Result.Err(error)
        val mapped = result.mapError { it.length }
        
        assertTrue(mapped.isErr())
        assertEquals(error.length, mapped.errorOrNull())
    }

    @Test
    fun `test flatMap on Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        val flatMapped = result.flatMap { Result.Ok(it.length) }
        
        assertTrue(flatMapped.isOk())
        assertEquals(data.length, flatMapped.getOrNull())
    }

    @Test
    fun `test flatMap on Err`() {
        val error = "测试错误"
        val result: Result<String, String> = Result.Err(error)
        val flatMapped = result.flatMap { Result.Ok(it.length) }
        
        assertTrue(flatMapped.isErr())
        assertEquals(error, flatMapped.errorOrNull())
    }

    @Test
    fun `test unwrap on Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        
        assertEquals(data, result.unwrap())
    }

    @Test
    fun `test unwrap on Err`() {
        val error = "测试错误"
        val result = Result.Err(error)
        
        assertThrows(IllegalStateException::class.java) {
            result.unwrap()
        }
    }

    @Test
    fun `test unwrapOr on Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        val default = "默认数据"
        
        assertEquals(data, result.unwrapOr(default))
    }

    @Test
    fun `test unwrapOr on Err`() {
        val error = "测试错误"
        val result: Result<String, String> = Result.Err(error)
        val default = "默认数据"
        
        assertEquals(default, result.unwrapOr(default))
    }

    @Test
    fun `test unwrapOrElse on Ok`() {
        val data = "测试数据"
        val result = Result.Ok(data)
        
        assertEquals(data, result.unwrapOrElse { "默认数据" })
    }

    @Test
    fun `test unwrapOrElse on Err`() {
        val error = "测试错误"
        val result: Result<String, String> = Result.Err(error)
        
        assertEquals("默认数据", result.unwrapOrElse { "默认数据" })
    }

    @Test
    fun `test runCatching success`() {
        val result = Result.runCatching { "测试数据" }
        
        assertTrue(result.isOk())
        assertEquals("测试数据", result.getOrNull())
    }

    @Test
    fun `test runCatching failure`() {
        val result = Result.runCatching { throw RuntimeException("测试异常") }
        
        assertTrue(result.isErr())
        assertTrue(result.errorOrNull() is RuntimeException)
    }

    @Test
    fun `test combine success`() {
        val results = listOf(
            Result.Ok("数据1"),
            Result.Ok("数据2"),
            Result.Ok("数据3")
        )
        val combined = Result.combine(*results.toTypedArray())
        
        assertTrue(combined.isOk())
        assertEquals(listOf("数据1", "数据2", "数据3"), combined.getOrNull())
    }

    @Test
    fun `test combine failure`() {
        val results = listOf(
            Result.Ok("数据1"),
            Result.Err("错误"),
            Result.Ok("数据3")
        )
        val combined = Result.combine(*results.toTypedArray())
        
        assertTrue(combined.isErr())
        assertEquals("错误", combined.errorOrNull())
    }
} 