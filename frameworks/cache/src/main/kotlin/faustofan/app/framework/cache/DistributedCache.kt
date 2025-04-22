package faustofan.app.framework.cache

import org.redisson.api.RBloomFilter
import java.util.concurrent.TimeUnit

/**
 * 分布式缓存接口，继承自Cache接口，提供了在分布式环境下使用缓存的能力。
 */
interface DistributedCache : Cache {

	/**
	 * 从缓存中获取指定键的值，如果缓存中不存在，则使用提供的加载函数加载数据并放入缓存。
	 *
	 * @param key 缓存的键。
	 * @param clazz 值的类类型。
	 * @param cacheLoader 加载数据的函数。
	 * @param timeout 超时时间。
	 * @param timeUnit 时间单位。
	 * @return 缓存的值，如果不存在则为null。
	 */
	fun <T> get(
		key: String,
		clazz: Class<T>,
		cacheLoader: () -> T,
		timeout: Long,
		timeUnit: TimeUnit = TimeUnit.MILLISECONDS
	): T?

	/**
	 * 安全地从缓存中获取值，提供了过滤和缓存不存在时的处理逻辑。
	 *
	 * @param key 缓存的键。
	 * @param clazz 值的类类型。
	 * @param cacheLoader 加载数据的函数。
	 * @param timeout 超时时间。
	 * @param timeUnit 时间单位。
	 * @param bloomFilter 布隆过滤器，用于过滤不存在的键。
	 * @param cacheCheckFilter 数据检查过滤器，用于进一步过滤缓存中的数据。
	 * @param cacheGetIfAbsent 缓存不存在时的处理函数。
	 * @return 缓存的值，如果不存在则为null。
	 */
	fun <T> safeGet(
		key: String,
		clazz: Class<T>,
		cacheLoader: () -> T,
		timeout: Long,
		timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
		bloomFilter: RBloomFilter<String>? = null,
		cacheCheckFilter: ((param: String) -> Boolean)? = null,
		cacheGetIfAbsent: ((param: String) -> Unit)? = null
	): T?

	/**
	 * 将键值对放入缓存，并设置超时时间。
	 *
	 * @param key 缓存的键。
	 * @param value 缓存的值。
	 * @param timeout 超时时间。
	 * @param timeUnit 时间单位。
	 */
	fun put(key: String, value: Any, timeout: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS)

	/**
	 * 安全地将键值对放入缓存，提供了布隆过滤器来避免重复数据。
	 *
	 * @param key 缓存的键。
	 * @param value 缓存的值。
	 * @param timeout 超时时间。
	 * @param timeUnit 时间单位。
	 * @param bloomFilter 布隆过滤器，用于检查是否已存在相同键。
	 */
	fun safePut(
		key: String,
		value: Any,
		timeout: Long,
		timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
		bloomFilter: RBloomFilter<String>?
	)

	/**
	 * 统计现有键的数量。
	 *
	 * @param keys 多个键。
	 * @return 存在的键的数量。
	 */
	fun countExistingKeys(vararg keys: String): Long
}

