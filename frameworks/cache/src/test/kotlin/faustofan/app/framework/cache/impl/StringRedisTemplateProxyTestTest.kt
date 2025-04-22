package faustofan.app.framework.cache.impl

import com.alibaba.fastjson2.JSON
import faustofan.app.framework.cache.config.RedisDistributedProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations

//@ExtendWith(MockitoExtension::class)
//class StringRedisTemplateProxyTestTest {
//
//	@Mock
//	private lateinit var stringRedisTemplate: StringRedisTemplate
//	@Mock
//	private lateinit var redisProperties: RedisDistributedProperties
//	@Mock
//	private lateinit var redissonClient: RedissonClient
//	@Mock
//	private lateinit var valueOperations: ValueOperations<String, String?>
//	@InjectMocks
//	private lateinit var proxy: StringRedisTemplateProxyTest
//
//	@BeforeEach
//	fun setup() {
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//	}
//
//	@Test
//	fun `test get with string class`() {
//		val key = "testKey"
//		val value = "testValue"
//
//		`when`(stringRedisTemplate.opsForValue().get(key)).thenReturn(value)
//
//		val result = proxy.get(key, String::class.java)
//		assertEquals(value, result)
//	}
//
//	@Test
//	fun `test get with non-string class`() {
//		val key = "testKey"
//		val testObject = TestObject("testValue")
//		val jsonValue = JSON.toJSONString(testObject)
//
//		`when`(stringRedisTemplate.opsForValue().get(key)).thenReturn(jsonValue)
//
//		val result = proxy.get(key, TestObject::class.java)
//		assertNotNull(result)
//		assertEquals(testObject.value, result!!.value)
//	}
//
//	@Test
//	fun `test get with cacheLoader`() {
//		val key = "testKey"
//		val clazz = String::class.java
//		val cacheLoader = { "loadedValue" }
//		val timeout = 1000L
//		val timeUnit = TimeUnit.MILLISECONDS
//
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//		`when`(stringRedisTemplate.opsForValue().get(key)).thenReturn(null)
//		lenient().`when`(stringRedisTemplate.opsForValue().set(key, "loadedValue", timeout, timeUnit)).then { }
//		`when`(stringRedisTemplate.opsForValue().get(key)).thenReturn("loadedValue")
//
//		val result = proxy.get(key, clazz, cacheLoader, timeout, timeUnit)
//		assertEquals("loadedValue", result)
//	}
//
//	@Test
//	fun `test safeGet with bloom filter`() {
//		val key = "testKey"
//		val clazz = String::class.java
//		val cacheLoader = { "loadedValue" }
//		val timeout = 1000L
//		val timeUnit = TimeUnit.MILLISECONDS
//		val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
//		val cacheCheckFilter = { _: String -> true }
//		val cacheGetIfAbsent = { _: String -> }
//
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//		lenient().`when`(bloomFilter.contains(key)).thenReturn(true)
//		`when`(stringRedisTemplate.opsForValue().get(key)).thenReturn("cachedValue")
//
//		val result =
//			proxy.safeGet(key, clazz, cacheLoader, timeout, timeUnit, bloomFilter, cacheCheckFilter, cacheGetIfAbsent)
//		assertEquals("cachedValue", result)
//	}
//
//	@Test
//	fun `test put with timeout`() {
//		val key = "testKey"
//		val value = "testValue"
//		val timeout = 1000L
//		val timeUnit = TimeUnit.MILLISECONDS
//
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//		`when`(stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit)).then { }
//
//		proxy.put(key, value, timeout, timeUnit)
//		verify(stringRedisTemplate.opsForValue(), times(1)).set(key, value, timeout, timeUnit)
//	}
//
//	@Test
//	fun `test put without timeout`() {
//		val key = "testKey"
//		val value = "testValue"
//
//		`when`(redisProperties.valueTimeUnit).thenReturn(TimeUnit.MILLISECONDS)
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//
//		proxy.put(key, value)
//
//		verify(valueOperations).set(key, value, Long.MAX_VALUE, redisProperties.valueTimeUnit)
//	}
//
//	@Test
//	fun `test safePut`() {
//		val key = "testKey"
//		val value = "testValue"
//		val bloomFilter = mock(RBloomFilter::class.java) as RBloomFilter<String>
//
//		`when`(stringRedisTemplate.opsForValue()).thenReturn(valueOperations)
//
//		proxy.safePut(key, value, 1000L, TimeUnit.MILLISECONDS, bloomFilter)
//
//		verify(valueOperations).set(key, value, 1000L, TimeUnit.MILLISECONDS)
//		verify(bloomFilter).add(key)
//	}
//
//	@Test
//	fun `test countExistingKeys`() {
//		val keys = arrayOf("key1", "key2")
//		val script = DefaultRedisScript<Long>()
//
//		`when`(stringRedisTemplate.countExistingKeys(keys.toMutableList())).thenReturn(1)
//		val result = proxy.countExistingKeys(*keys)
//		assertEquals(1L, result)
//
//	}
//
//	@Test
//	fun `test putIfAllAbsent`() {
//		// 准备测试数据
//		val keys = listOf("key1", "key2")
//		val valueTimeout = 3000L
//
//		// 创建脚本对象
//		val redisScript = DefaultRedisScript<Boolean>()
//
//		// 使用mockStatic处理静态方法调用
//		mockStatic(Singleton::class.java).use { singletonMock ->
//			// 使用标准的Mockito语法设置静态方法行为
//			`when`(Singleton.get<DefaultRedisScript<Boolean>>(eq(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH), any())).thenReturn(
//				redisScript
//			)
//
//			// 配置redisProperties.valueTimeout返回我们设置的值
//			`when`(redisProperties.valueTimeout).thenReturn(valueTimeout)
//
//			// 配置stringRedisTemplate.execute返回true
//			`when`(
//				stringRedisTemplate.execute(
//					same(redisScript),
//					eq(keys),
//					eq(valueTimeout.toString())
//				)
//			).thenReturn(true)
//
//			// 执行测试
//			val result = proxy.putIfAllAbsent(keys)
//
//			// 验证结果
//			assertTrue(result)
//
//			// 验证方法调用
//			verify(stringRedisTemplate).execute(
//				same(redisScript),
//				eq(keys),
//				eq(valueTimeout.toString())
//			)
//		}
//	}
//
//	@Test
//	fun `test delete with single key`() {
//		val key = "testKey"
//
//		`when`(stringRedisTemplate.delete(key)).thenReturn(true)
//
//		val result = proxy.delete(key)
//		assertEquals(1L, result)
//	}
//
//	@Test
//	fun `test delete with multiple keys`() {
//		val keys = listOf("key1", "key2")
//
//		`when`(stringRedisTemplate.delete(keys)).thenReturn(2L)
//
//		val result = proxy.delete(keys = keys)
//		assertEquals(2L, result)
//	}
//
//	@Test
//	fun `test hasKey`() {
//		val key = "testKey"
//
//		`when`(stringRedisTemplate.hasKey(key)).thenReturn(true)
//
//		val result = proxy.hasKey(key)
//		assertTrue(result)
//	}
//
//	@Test
//	fun `test getInstance`() {
//		val result = proxy.getInstance()
//		assertEquals(stringRedisTemplate, result)
//	}
//}
//
//class TestObject(val value: String)