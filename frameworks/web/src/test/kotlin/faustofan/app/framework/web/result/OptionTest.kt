package faustofan.app.framework.web.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OptionTest {

    @Test
    fun `test Some creation`() {
        val value = "测试数据"
        val option = Option.Some(value)
        
        assertEquals(value, option.getOrNull())
    }

    @Test
    fun `test None creation`() {
        val option = Option.None
        
        assertNull(option.getOrNull())
    }

    @Test
    fun `test onSome callback`() {
        val value = "测试数据"
        val option = Option.Some(value)
        var callbackCalled = false
        
        option.onSome { 
            assertEquals(value, it)
            callbackCalled = true
        }
        
        assertTrue(callbackCalled)
    }

    @Test
    fun `test onNone callback`() {
        val option = Option.None
        var callbackCalled = false
        
        option.onNone { 
            callbackCalled = true
        }
        
        assertTrue(callbackCalled)
    }

    @Test
    fun `test map on Some`() {
        val value = "测试数据"
        val option = Option.Some(value)
        val mapped = option.map { it.length }
        
        assertEquals(value.length, mapped.getOrNull())
    }

    @Test
    fun `test map on None`() {
        val option = Option.None
        val mapped = option.map { it.toString() }
        
        assertNull(mapped.getOrNull())
    }

    @Test
    fun `test flatMap on Some`() {
        val value = "测试数据"
        val option = Option.Some(value)
        val flatMapped = option.flatMap { Option.Some(it.length) }
        
        assertEquals(value.length, flatMapped.getOrNull())
    }

    @Test
    fun `test flatMap on None`() {
        val option = Option.None
        val flatMapped = option.flatMap { Option.Some(it.toString()) }
        
        assertNull(flatMapped.getOrNull())
    }

    @Test
    fun `test orElse with Some`() {
        val value = "测试数据"
        val option = Option.Some(value)
        val other = Option.Some("其他数据")
        
        assertEquals(value, option.orElse(other).getOrNull())
    }

    @Test
    fun `test orElse with None`() {
        val option: Option<String> = Option.None
        val other = Option.Some("其他数据")
        
        assertEquals("其他数据", option.orElse(other).getOrNull())
    }

    @Test
    fun `test orElseWith with Some`() {
        val value = "测试数据"
        val option: Option<String> = Option.Some(value)
        
        assertEquals(value, option.orElseWith { Option.Some("其他数据") }.getOrNull())
    }

    @Test
    fun `test orElseWith with None`() {
        val option: Option<String> = Option.None
        
        assertEquals("其他数据", option.orElseWith { Option.Some("其他数据") }.getOrNull())
    }

    @Test
    fun `test combine success`() {
        val options = listOf(
            Option.Some("数据1"),
            Option.Some("数据2"),
            Option.Some("数据3")
        )
        val combined = Option.combine(options[0], options[1], options[2]) { it.joinToString(",") }
        
        assertEquals("数据1,数据2,数据3", combined.getOrNull())
    }

    @Test
    fun `test combine failure`() {
        val options = listOf(
            Option.Some("数据1"),
            Option.None,
            Option.Some("数据3")
        )
        val combined = Option.combine(options[0], options[1], options[2]) { it.joinToString(",") }
        
        assertNull(combined.getOrNull())
    }
} 