package faustofan.app.framework.cache

/**
 * 缓存接口，定义了缓存操作的基本方法。
 */
interface Cache {
	/**
	 * 从缓存中获取指定键的值。
	 *
	 * @param key 键的字符串表示。
	 * @param clazz 值的类类型，用于反序列化。
	 * @param <T> 值的泛型类型。
	 * @return 缓存中对应键的值，如果不存在则返回null。
	 */
	fun <T> get(key: String, clazz: Class<T>): T?

	/**
	 * 将键值对放入缓存。
	 *
	 * @param key 键的字符串表示。
	 * @param value 要缓存的值。
	 */
	fun put(key: String, value: Any)

	/**
	 * 如果所有给定的键都不存在于缓存中，则将它们全部放入缓存。
	 *
	 * @param keys 要放入缓存的键的集合。
	 * @return 如果所有键都不存在且成功放入缓存，则返回true；否则返回false。
	 */
	fun putIfAllAbsent(keys: List<String>): Boolean

	/**
	 * 删除缓存中的键值对。
	 *
	 * @param key 要删除的键，可以为null，此时keys不能为null。
	 * @param keys 要删除的键的集合，可以为null，此时key不能为null。
	 * @return 被删除的键的数量。
	 */
	fun delete(key: String? = null, keys: Collection<String>? = null): Long

	/**
	 * 检查缓存中是否存在指定的键。
	 *
	 * @param key 要检查的键。
	 * @return 如果缓存中存在指定的键，则返回true；否则返回false。
	 */
	fun hasKey(key: String): Boolean

	/**
	 * 获取缓存实例。
	 *
	 * @return 缓存的实例。
	 */
	fun getInstance(): Any
}