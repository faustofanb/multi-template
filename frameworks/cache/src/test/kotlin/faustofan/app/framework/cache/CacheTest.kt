package faustofan.app.framework.cache

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension::class)
class CacheTest {

    @Test
    fun `test cache interface methods`() {
        // 创建一个Cache接口的mock实现
        val cache = mock(Cache::class.java)
        
        // 测试get方法
        val testValue = "testValue"
        `when`(cache.get("testKey", String::class.java)).thenReturn(testValue)
        assertEquals(testValue, cache.get("testKey", String::class.java))
        
        // 测试put方法
        `when`(cache.put("testKey", "testValue")).then { }
        cache.put("testKey", "testValue")
        
        // 测试putIfAllAbsent方法
        `when`(cache.putIfAllAbsent(listOf("key1", "key2"))).thenReturn(true)
        assertTrue(cache.putIfAllAbsent(listOf("key1", "key2")))
        
        // 测试delete方法
        `when`(cache.delete("testKey")).thenReturn(1L)
        assertEquals(1L, cache.delete("testKey"))
        
        // 测试hasKey方法
        `when`(cache.hasKey("testKey")).thenReturn(true)
        assertTrue(cache.hasKey("testKey"))
        
        // 测试getInstance方法
        val instance = Any()
        `when`(cache.getInstance()).thenReturn(instance)
        assertNotNull(cache.getInstance())
        assertEquals(instance, cache.getInstance())
    }
} 