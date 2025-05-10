package faustofan.app.framework.cache.impl

import com.alibaba.fastjson2.JSON
import faustofan.app.framework.cache.config.RedisDistributedProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.redisson.api.RBloomFilter
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.script.DefaultRedisScript
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class StringRedisTemplateProxyTest {

    @Mock
    private lateinit var stringRedisTemplate: StringRedisTemplate

    @Mock
    private lateinit var redisProperties: RedisDistributedProperties

    @Mock
    private lateinit var redissonClient: RedissonClient

    @Mock
    private lateinit var valueOperations: ValueOperations<String, String>

    @InjectMocks
    private lateinit var proxy: StringRedisTemplateProxy

    @BeforeEach
    fun setUp() {
        lenient().`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
    }

    @Test
    fun `test get with string class`() {
        // 准备测试数据
        val key = "testKey"
        val value = "testValue"
        
        // 配置模拟行为
        `when`(valueOperations.get(key)).thenReturn(value)
        
        // 执行测试
        val result = proxy.get(key, String::class.java)
        
        // 验证结果
        assertEquals(value, result)
        verify(valueOperations).get(key)
    }

    @Test
    fun `test get with custom class`() {
        // 准备测试数据
        val key = "testKey"
        val testObject = TestObject("testValue")
        val jsonValue = JSON.toJSONString(testObject)
        
        // 配置模拟行为
        `when`(valueOperations.get(key)).thenReturn(jsonValue)
        
        // 执行测试
        val result = proxy.get(key, TestObject::class.java)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(testObject.value, result!!.value)
        verify(valueOperations).get(key)
    }

    @Test
    fun `test get with cache loader`() {
        // 准备测试数据
        val key = "testKey"
        val loadedValue = "loadedValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 配置模拟行为 - 首次返回null，加载后返回值
        `when`(valueOperations.get(key)).thenReturn(null)
        
        // 执行测试
        val result = proxy.get(key, String::class.java, { loadedValue }, timeout, timeUnit)
        
        // 验证结果
        assertEquals(loadedValue, result)
        verify(valueOperations).get(key)
        verify(valueOperations).set(key, loadedValue, timeout, timeUnit)
    }

    @Test
    fun `test get with existing value`() {
        // 准备测试数据
        val key = "testKey"
        val value = "existingValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 配置模拟行为 - 返回已存在的值
        `when`(valueOperations.get(key)).thenReturn(value)
        
        // 执行测试
        val result = proxy.get(key, String::class.java, { "loadedValue" }, timeout, timeUnit)
        
        // 验证结果
        assertEquals(value, result)
        verify(valueOperations).get(key)
        // 不应调用cacheLoader或set方法
        verify(valueOperations, never()).set(eq(key), any<String>(), eq(timeout), eq(timeUnit))
    }

    @Test
    fun `test safe get with existing value`() {
        // 准备测试数据
        val key = "testKey"
        val value = "existingValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 模拟布隆过滤器
        @Suppress("UNCHECKED_CAST")
        val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
        
        // 配置模拟行为
        `when`(valueOperations.get(key)).thenReturn(value)
        
        // 执行测试
        val result = proxy.safeGet(
            key, 
            String::class.java, 
            { "loadedValue" }, 
            timeout, 
            timeUnit, 
            bloomFilter,
            null,
            null
        )
        
        // 验证结果
        assertEquals(value, result)
        verify(valueOperations).get(key)
        // 不应调用布隆过滤器的contains方法
        verify(bloomFilter, never()).contains(key)
        // 不应获取锁
        verify(redissonClient, never()).getLock(any<String>())
    }

    @Test
    fun `test safe get with non existing value and bloom filter miss`() {
        // 准备测试数据
        val key = "testKey"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 模拟布隆过滤器
        @Suppress("UNCHECKED_CAST")
        val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
        
        // 配置模拟行为
        `when`(valueOperations.get(key)).thenReturn(null)
        `when`(bloomFilter.contains(key)).thenReturn(false) // 布隆过滤器不包含键
        
        // 执行测试
        val result = proxy.safeGet(
            key, 
            String::class.java, 
            { "loadedValue" }, 
            timeout, 
            timeUnit, 
            bloomFilter,
            null,
            null
        )
        
        // 验证结果
        assertNull(result)
        verify(valueOperations).get(key)
        verify(bloomFilter).contains(key)
        // 不应获取锁，因为布隆过滤器已经确认键不存在
        verify(redissonClient, never()).getLock(any<String>())
    }

    @Test
    fun `test safe get with non existing value and bloom filter hit`() {
        // 准备测试数据
        val key = "testKey"
        val loadedValue = "loadedValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 模拟布隆过滤器和锁
        @Suppress("UNCHECKED_CAST")
        val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
        val lock = mock(RLock::class.java)
        
        // 配置模拟行为
        `when`(valueOperations.get(key)).thenReturn(null)
        `when`(bloomFilter.contains(key)).thenReturn(true) // 布隆过滤器包含键
        `when`(redissonClient.getLock(StringRedisTemplateProxy.SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key)).thenReturn(lock)
        doNothing().`when`(lock).lock()
        doNothing().`when`(lock).unlock()
        
        // 执行测试
        val result = proxy.safeGet(
            key, 
            String::class.java, 
            { loadedValue }, 
            timeout, 
            timeUnit, 
            bloomFilter,
            null,
            null
        )
        
        // 验证结果
        assertEquals(loadedValue, result)
        verify(valueOperations, times(2)).get(key) // 两次调用get: 一次在safeGet开始，一次在获得锁后
        verify(bloomFilter).contains(key)
        verify(redissonClient).getLock(StringRedisTemplateProxy.SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key)
        verify(lock).lock()
        verify(lock).unlock()
        verify(valueOperations).set(key, loadedValue, timeout, timeUnit)
        verify(bloomFilter).add(key)
    }

    @Test
    fun `test put with timeout`() {
        // 准备测试数据
        val key = "testKey"
        val value = "testValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 执行测试
        proxy.put(key, value, timeout, timeUnit)
        
        // 验证结果
        verify(valueOperations).set(key, value, timeout, timeUnit)
    }

    @Test
    fun `test put with custom object and timeout`() {
        // 准备测试数据
        val key = "testKey"
        val testObject = TestObject("testValue")
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 执行测试
        proxy.put(key, testObject, timeout, timeUnit)
        
        // 验证结果
        verify(valueOperations).set(eq(key), any<String>(), eq(timeout), eq(timeUnit))
    }

    @Test
    fun `test put without timeout`() {
        // 准备测试数据
        val key = "testKey"
        val value = "testValue"
        
        // 配置模拟行为
        `when`(redisProperties.valueTimeUnit).thenReturn(TimeUnit.SECONDS)
        
        // 执行测试
        proxy.put(key, value)
        
        // 验证结果
        verify(valueOperations).set(key, value, Long.MAX_VALUE, TimeUnit.SECONDS)
    }

    @Test
    fun `test safe put`() {
        // 准备测试数据
        val key = "testKey"
        val value = "testValue"
        val timeout = 1000L
        val timeUnit = TimeUnit.MILLISECONDS
        
        // 模拟布隆过滤器
        @Suppress("UNCHECKED_CAST")
        val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
        
        // 执行测试
        proxy.safePut(key, value, timeout, timeUnit, bloomFilter)
        
        // 验证结果
        verify(valueOperations).set(key, value, timeout, timeUnit)
        verify(bloomFilter).add(key)
    }

    @Test
    fun `test count existing keys`() {
        // 准备测试数据
        val keys = arrayOf("key1", "key2", "key3")
        val keyList = listOf(*keys)
        
        // 配置模拟行为
        `when`(stringRedisTemplate.countExistingKeys(keyList)).thenReturn(2L)
        
        // 执行测试
        val result = proxy.countExistingKeys(*keys)
        
        // 验证结果
        assertEquals(2L, result)
        verify(stringRedisTemplate).countExistingKeys(keyList)
    }

    @Test
    fun `test put if all absent`() {
        // 准备测试数据
        val keys = listOf("key1", "key2")
        val valueTimeout = 3000L

        // 配置模拟行为
        `when`(redisProperties.valueTimeout).thenReturn(valueTimeout)

        // 使用ArgumentCaptor捕获执行脚本时的参数
        val scriptCaptor = ArgumentCaptor.forClass(DefaultRedisScript::class.java)
        val keysCaptor = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<String>>
        val argsCaptor = ArgumentCaptor.forClass(String::class.java)

        // 模拟execute方法的行为
        `when`(stringRedisTemplate.execute(
            scriptCaptor.capture(),
            keysCaptor.capture(),
            argsCaptor.capture()
        )).thenReturn(true)

        // 执行测试
        val result = proxy.putIfAllAbsent(keys)

        // 验证结果
        assertTrue(result)
        verify(stringRedisTemplate).execute(any<DefaultRedisScript<Boolean>>(), eq(keys), eq(valueTimeout.toString()))

        // 可选：验证脚本的返回类型是Boolean
        assertEquals(Boolean::class.java, scriptCaptor.value.resultType)
    }

    @Test
    fun `test delete with single key`() {
        // 准备测试数据
        val key = "testKey"
        
        // 配置模拟行为
        `when`(stringRedisTemplate.delete(key)).thenReturn(true)
        
        // 执行测试
        val result = proxy.delete(key, null)
        
        // 验证结果
        assertEquals(1L, result)
        verify(stringRedisTemplate).delete(key)
    }

    @Test
    fun `test delete with multiple keys`() {
        // 准备测试数据
        val keys = listOf("key1", "key2")
        
        // 配置模拟行为
        `when`(stringRedisTemplate.delete(keys)).thenReturn(2L)
        
        // 执行测试
        val result = proxy.delete(null, keys)
        
        // 验证结果
        assertEquals(2L, result)
        verify(stringRedisTemplate).delete(keys)
    }

    @Test
    fun `test delete with no valid input`() {
        // 准备测试数据 - 两个参数都为空
        
        // 执行测试
        val result = proxy.delete(null, null)
        
        // 验证结果
        assertEquals(0L, result)
        // 不应调用任何删除方法
        verify(stringRedisTemplate, never()).delete(any<String>())
        verify(stringRedisTemplate, never()).delete(any<Collection<String>>())
    }

    @Test
    fun `test has key`() {
        // 准备测试数据
        val key = "testKey"
        
        // 配置模拟行为
        `when`(stringRedisTemplate.hasKey(key)).thenReturn(true)
        
        // 执行测试
        val result = proxy.hasKey(key)
        
        // 验证结果
        assertTrue(result)
        verify(stringRedisTemplate).hasKey(key)
    }

    @Test
    fun `test get instance`() {
        // 执行测试
        val result = proxy.getInstance()
        
        // 验证结果
        assertEquals(stringRedisTemplate, result)
    }

    // 测试辅助类
    class TestObject {
        var value: String? = null
        
        constructor()
        
        constructor(value: String) {
            this.value = value
        }
    }
}