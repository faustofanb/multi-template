package faustofan.app.framework.cache.util

import com.alibaba.fastjson2.util.ParameterizedTypeImpl
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * FastJson2Util工具类，提供构建复杂类型的能力。
 * 该类作为一个单例对象存在，提供了一个方法用于根据传入的Type数组构建一个复杂的ParameterizedType。
 */
object FastJson2Util {
	/**
	 * 根据传入的Type数组构建一个或多个ParameterizedType。
	 * 此方法用于处理需要序列化或反序列化复杂类型的情况，如泛型嵌套。
	 *
	 * @param types Type数组，代表一个或多个泛型类型。
	 * @return 返回构建的ParameterizedType，如果types为空则返回null。
	 */
	fun buildType(vararg types: Type): Type? {
		// 用于存储构建过程中的中间结果，即部分构建好的ParameterizedType。
		var beforeType: ParameterizedType? = null

		// 检查传入的types是否为空，如果不为空则进行处理。
		if (types.isNotEmpty()) {
			// 根据types的长度采取不同的构建策略。
			when(types.size) {
				// 如果只有一个Type，则直接构建并返回一个简单的ParameterizedType。
				1 -> return ParameterizedTypeImpl(
					arrayOf(null),
					null,
					types[0]
				)
				// 如果有多个Type，则需要构建一个嵌套的ParameterizedType。
				else -> {
					// 从后向前遍历types，构建嵌套的ParameterizedType。
					for (i in types.size - 1 downTo 1) {
						// 使用当前的beforeType和当前的Type构建一个新的ParameterizedType。
						// 如果beforeType为空，则直接使用当前的Type。
						beforeType = ParameterizedTypeImpl(
							arrayOf(beforeType ?: types[i]),
							null,
							types[i - 1]
						)
					}
				}
			}
		}

		// 返回最终构建的ParameterizedType，如果没有任何Type传入，则返回null。
		return beforeType
	}
}