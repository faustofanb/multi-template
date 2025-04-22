package faustofan.app.framework.cache.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class CacheUtilTest {

    @Test
    fun `test isNullOrBlank with null string`() {
        assertTrue(CacheUtil.isNullOrBlank(null as String?))
    }

    @Test
    fun `test isNullOrBlank with empty string`() {
        assertTrue(CacheUtil.isNullOrBlank(""))
    }

    @Test
    fun `test isNullOrBlank with blank string`() {
        assertTrue(CacheUtil.isNullOrBlank("   "))
    }

    @Test
    fun `test isNullOrBlank with non-blank string`() {
        assertFalse(CacheUtil.isNullOrBlank("test"))
    }

    @Test
    fun `test isNullOrBlank with null object`() {
        assertTrue(CacheUtil.isNullOrBlank(null as Any?))
    }

    @Test
    fun `test isNullOrBlank with empty collection`() {
        assertTrue(CacheUtil.isNullOrBlank(emptyList<Any>()))
    }

    @Test
    fun `test isNullOrBlank with non-empty collection`() {
        assertFalse(CacheUtil.isNullOrBlank(listOf("test")))
    }

    @Test
    fun `test isNullOrBlank with empty map`() {
        assertTrue(CacheUtil.isNullOrBlank(emptyMap<String, Any>()))
    }

    @Test
    fun `test isNullOrBlank with non-empty map`() {
        assertFalse(CacheUtil.isNullOrBlank(mapOf("key" to "value")))
    }

    @Test
    fun `test isNullOrBlank with empty array`() {
        assertTrue(CacheUtil.isNullOrBlank(emptyArray<Any>()))
    }

    @Test
    fun `test isNullOrBlank with non-empty array`() {
        assertFalse(CacheUtil.isNullOrBlank(arrayOf("test")))
    }
} 