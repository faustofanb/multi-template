package faustofan.app.framework.cache

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension::class)
class MultistageCacheTest {

    @Test
    fun `test multistage cache interface methods`() {
        // 创建一个MultistageCache接口的mock实现
        val multistageCache = mock(MultistageCache::class.java)
        
        // 测试从Cache接口继承的方法
        val testValue = "testValue"
        `when`(multistageCache.get("testKey", String::class.java)).thenReturn(testValue)
        assertEquals(testValue, multistageCache.get("testKey", String::class.java))
        
        // 测试put方法
        `when`(multistageCache.put("testKey", "testValue")).then { }
        multistageCache.put("testKey", "testValue")
        
        // 测试putIfAllAbsent方法
        `when`(multistageCache.putIfAllAbsent(listOf("key1", "key2"))).thenReturn(true)
        assertEquals(true, multistageCache.putIfAllAbsent(listOf("key1", "key2")))
        
        // 测试delete方法
        `when`(multistageCache.delete("testKey")).thenReturn(1L)
        assertEquals(1L, multistageCache.delete("testKey"))
        
        // 测试hasKey方法
        `when`(multistageCache.hasKey("testKey")).thenReturn(true)
        assertEquals(true, multistageCache.hasKey("testKey"))
        
        // 测试getInstance方法
        val instance = Any()
        `when`(multistageCache.getInstance()).thenReturn(instance)
        assertNotNull(multistageCache.getInstance())
        assertEquals(instance, multistageCache.getInstance())
    }
} 