package faustofan.app.framework.cache.util

/**
 * 缓存工具类
 */
object CacheUtil {

	/**
	 * 连接符，用于拼接缓存键。
	 */
	private const val SPLICING_OPERATOR = "_"

	/**
	 * 构建缓存键。
	 *
	 * @param keys 组成缓存键的字符串数组。
	 * @return 拼接好的缓存键。
	 * @throws RuntimeException 如果任何键为空，则抛出运行时异常。
	 */
	fun buildKey(vararg keys: String): String {
		// 检查每个键是否为空，如果为空则抛出异常
		keys.forEach {
			if (it.isBlank()) throw RuntimeException("构建缓存 key 不允许为空")
		}
		// 使用连接符拼接所有键成一个缓存键
		return keys.joinToString(SPLICING_OPERATOR)
	}
	/**
	 * 判断缓存值是否为空或空白字符串。
	 *
	 * @param cacheVal 缓存的值。
	 * @return 如果缓存值为空或空白字符串，则返回true；否则返回false。
	 */
	fun isNullOrBlank(cacheVal: Any?): Boolean {
		// 返回缓存值为空或者为空白字符串的判断结果
		return cacheVal == null
				|| (cacheVal is String && cacheVal.isBlank())
				|| (cacheVal is Collection<*> && cacheVal.isEmpty())
				|| (cacheVal is Map<*, *> && cacheVal.isEmpty())
				|| (cacheVal is Array<*> && cacheVal.isEmpty())
	}
}
