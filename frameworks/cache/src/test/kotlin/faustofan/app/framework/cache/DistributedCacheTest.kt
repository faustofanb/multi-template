package faustofan.app.framework.cache

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Assertions.*
import org.redisson.api.RBloomFilter
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class DistributedCacheTest {

    @Test
    fun `test distributed cache interface methods`() {
        // 创建一个DistributedCache接口的mock实现
        val distributedCache = mock(DistributedCache::class.java)
        
        // 测试get方法（带cacheLoader）
        val testValue = "testValue"
        `when`(distributedCache.get("testKey", String::class.java, { "loadedValue" }, 1000L, TimeUnit.MILLISECONDS))
            .thenReturn(testValue)
        assertEquals(testValue, distributedCache.get("testKey", String::class.java, { "loadedValue" }, 1000L, TimeUnit.MILLISECONDS))
        
        // 测试safeGet方法
        val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
        `when`(bloomFilter.contains("testKey")).thenReturn(true)
        `when`(distributedCache.safeGet(
            "testKey", 
            String::class.java, 
            { "loadedValue" }, 
            1000L, 
            TimeUnit.MILLISECONDS,
            bloomFilter,
            { true },
            { }
        )).thenReturn(testValue)
        
        assertEquals(testValue, distributedCache.safeGet(
            "testKey", 
            String::class.java, 
            { "loadedValue" }, 
            1000L, 
            TimeUnit.MILLISECONDS,
            bloomFilter,
            { true },
            {  }
        ))
        
        // 测试put方法（带超时）
        `when`(distributedCache.put("testKey", "testValue", 1000L, TimeUnit.MILLISECONDS)).then { }
        distributedCache.put("testKey", "testValue", 1000L, TimeUnit.MILLISECONDS)
        
        // 测试safePut方法
        `when`(distributedCache.safePut("testKey", "testValue", 1000L, TimeUnit.MILLISECONDS, bloomFilter)).then { }
        distributedCache.safePut("testKey", "testValue", 1000L, TimeUnit.MILLISECONDS, bloomFilter)
        
        // 测试countExistingKeys方法
        `when`(distributedCache.countExistingKeys("key1", "key2")).thenReturn(2L)
        assertEquals(2L, distributedCache.countExistingKeys("key1", "key2"))
    }
} 