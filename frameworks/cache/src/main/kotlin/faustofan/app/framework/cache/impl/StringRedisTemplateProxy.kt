package faustofan.app.framework.cache.impl

import com.alibaba.fastjson2.JSON
import faustofan.app.framework.base.context.Singleton
import faustofan.app.framework.cache.DistributedCache
import faustofan.app.framework.cache.config.RedisDistributedProperties
import faustofan.app.framework.cache.util.CacheUtil
import faustofan.app.framework.cache.util.FastJson2Util
import org.redisson.api.RBloomFilter
import org.redisson.api.RedissonClient
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scripting.support.ResourceScriptSource
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class StringRedisTemplateProxy(
	private val stringRedisTemplate: StringRedisTemplate,
	private val redisProperties: RedisDistributedProperties,
	private val redissonClient: RedissonClient
) : DistributedCache {

	companion object {
		const val LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua"
		const val SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:"
	}

	override fun <T> get(key: String, clazz: Class<T>, cacheLoader: () -> T, timeout: Long, timeUnit: TimeUnit): T? {
		val result = get(key, clazz)
		if (!CacheUtil.isNullOrBlank(result))
			return result
		return loadAndSet(key, cacheLoader, timeout, timeUnit, true)

	}

	@Suppress("UNCHECKED_CAST")
	override fun <T> get(key: String, clazz: Class<T>): T? {
		val value = stringRedisTemplate.opsForValue().get(key)
		if (String::class.java.isAssignableFrom(clazz))
			return value as? T
		return JSON.parseObject(value, FastJson2Util.buildType(clazz))
	}

	override fun <T> safeGet(
		key: String,
		clazz: Class<T>,
		cacheLoader: () -> T,
		timeout: Long,
		timeUnit: TimeUnit,
		bloomFilter: RBloomFilter<String>?,
		cacheCheckFilter: ((param: String) -> Boolean)?,
		cacheGetIfAbsent: ((param: String) -> Unit)?
	): T? {
		var result = get(key, clazz)
		// 缓存结果不等于空或空字符串直接返回
		if (!CacheUtil.isNullOrBlank(result)
			|| cacheCheckFilter?.invoke(key) == true
			// 布隆过滤器中不存在该键, 且通过前两个判断, 说明该值为null, 那么直接返回null
			|| bloomFilter?.contains(key) == false
		) {
			return result
		}
		// 双重判定锁，减轻获得分布式锁后线程访问数据库压力

		val lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key)
		lock.lock()
		try {
			// 双重判定锁，减轻获得分布式锁后线程访问数据库压力
			result = get(key, clazz)
			if (CacheUtil.isNullOrBlank(result)) {
				result = loadAndSet(key, cacheLoader, timeout, timeUnit, true, bloomFilter)
				if (CacheUtil.isNullOrBlank(result)) {
					//如果查询结果为空，执行逻辑
					cacheGetIfAbsent?.invoke(key)
				}
			}
		} finally {
			lock.unlock()
		}
		return result
	}

	/**
	 * 将键值对存入Redis中，并设置过期时间
	 *
	 * @param key 键
	 * @param value 值，可以是任意类型，如果为String类型则直接存入，否则将其转为JSON字符串存入
	 * @param timeout 过期时间
	 * @param timeUnit 时间单位
	 */
	override fun put(key: String, value: Any, timeout: Long, timeUnit: TimeUnit ) {
		val actualValue = if (value is String) value else JSON.toJSONString(value)
		stringRedisTemplate.opsForValue().set(
			key,
			actualValue,
			timeout,
			timeUnit
		)
	}

	/**
	 * 将键值对存入Redis中，如果value不是String类型则自动转为JSON字符串存入。
	 * 如果没有设置过期时间，则默认使用Long.MAX_VALUE作为过期时间，时间单位由redisProperties.valueTimeUnit决定。
	 *
	 * @param key 键
	 * @param value 值，可以是任意类型
	 */
	override fun put(key: String, value: Any) {
		put(key, value, Long.MAX_VALUE, redisProperties.valueTimeUnit )
	}

	/**
	 * 安全地放入键值对到缓存中，支持设置过期时间和布隆过滤器过滤。
	 *
	 * @param key 键
	 * @param value 值
	 * @param timeout 过期时间
	 * @param timeUnit 时间单位
	 * @param bloomFilter 布隆过滤器
	 */
	override fun safePut(
		key: String,
		value: Any,
		timeout: Long,
		timeUnit: TimeUnit,
		bloomFilter: RBloomFilter<String>?
	) {
		put(key, value, timeout, timeUnit)

		bloomFilter?.add(key)
	}

	/**
	 * 统计指定键在Redis中存在的数量。
	 *
	 * @param keys 键列表
	 * @return 存在的键的数量
	 */
	override fun countExistingKeys(vararg keys: String): Long {
		return stringRedisTemplate.countExistingKeys(keys.toMutableList())
	}

	/**
	 * 尝试同时为多个键设置值，仅当所有键都不存在时才执行设置操作。
	 * 这个方法利用了Lua脚本在Redis中执行，以确保操作的原子性。
	 *
	 * @param keys 需要检查和设置值的键集合。
	 * @return 如果所有键都不存在并成功设置了值，则返回true；否则返回false。
	 */
	override fun putIfAllAbsent(keys: List<String>): Boolean {
		/* 获取Lua脚本，该脚本用于检查所有键是否都不存在并设置它们的值。 */
		/* 如果脚本尚未加载，则通过指定的路径加载脚本。 */
		val actual = Singleton.get(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH) {
			DefaultRedisScript<Boolean>().apply {
				/* 设置脚本源为类路径中的指定Lua脚本文件。 */
				setScriptSource(ResourceScriptSource(ClassPathResource(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH)))
				/* 指定脚本的返回类型为Boolean。 */
				setResultType(Boolean::class.java)
			}
		}

		/* 执行Lua脚本，传入键集合和值的过期时间，返回执行结果。 */
		/* 如果所有键都不存在，脚本将为它们设置值并返回true；否则返回false。 */
		return stringRedisTemplate.execute(
			actual!!,
			keys.toList(),
			redisProperties.valueTimeout.toString()
		)
	}

	/**
	 * 删除Redis中的键值。
	 *
	 * 此函数提供了灵活的方式以删除单个键或一组键。它首先判断传入的参数是单个键还是键集合，
	 * 然后分别执行相应的删除操作。如果同时传入了单个键和键集合，则只删除单个键。
	 *
	 * @param key 单个要删除的键，可能是null。
	 * @param keys 要删除的键的集合，可能是null。
	 * @return 返回实际被删除的键的数量。如果只传入了单个键且删除成功，则返回1；
	 *         如果只传入了键集合，则返回被删除的键的数量；其他情况下返回0。
	 */
	override fun delete(key: String?, keys: Collection<String>?): Long {
		return when {
			key != null && keys == null -> {
				if (stringRedisTemplate.delete(key)) 1 else 0
			}
			key == null && keys != null -> {
				stringRedisTemplate.delete(keys)
			}
			else -> 0
		}
	}

	/**
	 * 检查Redis中是否存在指定的键。
	 *
	 * 本方法通过调用stringRedisTemplate的hasKey方法来判断给定的键是否存在于Redis中。
	 * 这对于需要在操作键值对之前验证键是否存在的情况非常有用。
	 *
	 * @param key 要检查的键。
	 * @return 如果键存在，则返回true；否则返回false。
	 */
	override fun hasKey(key: String): Boolean {
		return stringRedisTemplate.hasKey(key)
	}

	/**
	 * 获取StringRedisTemplate实例。
	 *
	 * 本函数旨在提供一个统一的方式来获取StringRedisTemplate的实例，确保在整个应用程序中使用相同的Redis模板对象。
	 * 这对于需要与Redis进行交互的操作来说是非常重要的，因为它可以保证操作的一致性和性能。
	 *
	 * @return 返回StringRedisTemplate的实例，该实例可用于执行与Redis相关的操作。
	 */
	override fun getInstance(): Any {
		return stringRedisTemplate
	}


	/**
	 * 加载并设置缓存。
	 *
	 * 该方法通过调用提供的缓存加载器来加载数据，并根据安全标志决定是否使用安全方式存储数据到缓存中。
	 * 如果数据为空或者安全标志为false，数据将直接被缓存；如果安全标志为true，数据将通过一个更安全的方式被缓存。
	 * 此外，还可以指定缓存的超时时间和单位，以及可选的布隆过滤器来优化缓存操作。
	 *
	 * @param key 缓存键。
	 * @param cacheLoader 缓存加载器，用于加载缓存值。
	 * @param timeout 缓存项的超时时间。
	 * @param timeUnit 超时时间的单位。
	 * @param safeFlag 安全标志，决定是否使用安全方式存储缓存。
	 * @param bloomFilter 布隆过滤器，用于减少缓存误判，可选。
	 * @param <T> 缓存项的类型。
	 * @return 加载的缓存项，可能为null。
	 */
	private fun <T> loadAndSet(
		key: String,
		cacheLoader: () -> T?,
		timeout: Long,
		timeUnit: TimeUnit,
		safeFlag: Boolean,
		bloomFilter: RBloomFilter<String>? = null
	): T? {
		val result = cacheLoader()
		when {
			CacheUtil.isNullOrBlank(result) -> return result
			safeFlag -> safePut(key, result!!, timeout, timeUnit, bloomFilter)
			else -> put(key, result!!, timeout, timeUnit)
		}
		return result
	}
}