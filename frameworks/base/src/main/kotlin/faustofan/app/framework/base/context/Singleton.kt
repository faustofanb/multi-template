package faustofan.app.framework.base.context

import java.util.concurrent.ConcurrentHashMap

/**
 * 单例对象池。
 * 使用ConcurrentHashMap来实现线程安全的对象池，确保在多线程环境下对对象池的访问是安全的。
 */
object Singleton {
	// 使用ConcurrentHashMap来存储单例对象，确保线程安全
	private val SINGLE_OBJECT_POOL = ConcurrentHashMap<String, Any?>()

	/**
	 * 根据 key 获取单例对象。
	 * 如果对象不存在，则使用 supplier 构建并存储。
	 *
	 * @param key 用于查找对象的键。
	 * @param supplier 如果对象不存在时构建对象的供给者。
	 * @return 返回单例对象或 null（如果 supplier 为 null 或构建的对象为 null）。
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T> get(key: String, supplier: (() -> T?) = {null}): T? =
		 SINGLE_OBJECT_POOL[key] as? T ?: supplier.invoke()

	/**
	 * 将给定的键值对放入单例对象池中。
	 * 如果未提供键，则使用值的类名作为键。
	 *
	 * @param key 可选参数，用于标识对象的字符串。如果为null，则使用值的类名作为键。
	 * @param value 要放入对象池的值。
	 */
	fun put(key: String? = null, value: Any) {
		// 如果没有提供key，则使用value的类名作为key
		// 将值放入单例对象池中，使用实际的键进行索引
		SINGLE_OBJECT_POOL[key ?: value.javaClass.name] = value
	}
}
