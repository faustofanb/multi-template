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
     * 示例用法:
     * - 单个类型: FastJson2Util.buildType(String::class.java) -> 返回String.class
     * - 简单泛型: FastJson2Util.buildType(List::class.java, String::class.java) -> 返回表示List<String>的泛型类型
     * - 复杂泛型: FastJson2Util.buildType(Map::class.java, String::class.java, List::class.java) -> 返回表示Map<String, List>的泛型类型
     * - 嵌套泛型: 先构建innerType = FastJson2Util.buildType(List::class.java, Int::class.java)，
     *            然后FastJson2Util.buildType(Map::class.java, String::class.java, innerType) -> 返回表示Map<String, List<Int>>的泛型类型
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
                // 如果只有一个Type，则直接返回该类型
                // 示例: FastJson2Util.buildType(String::class.java) 返回 String.class
                1 -> return types[0]
                // 如果有两个Type，构建简单的泛型类型
                // 示例: FastJson2Util.buildType(List::class.java, String::class.java) 构建 List<String>
                2 -> return ParameterizedTypeImpl(
                    arrayOf(types[1]),
                    null,
                    types[0]
                )
                // 如果有多个Type，则需要构建一个嵌套的ParameterizedType。
                else -> {
                    // 先处理Map<K,V>这种需要两个泛型参数的情况
                    // 示例: FastJson2Util.buildType(Map::class.java, String::class.java, List::class.java) 构建 Map<String, List>
                    if (types.size >= 3) {
                        val actualTypeArguments = arrayOf(types[1], types[2])
                        beforeType = ParameterizedTypeImpl(
                            actualTypeArguments,
                            null,
                            types[0]
                        )
                        
                        // 如果还有更多类型，继续构建嵌套结构
                        // 示例: FastJson2Util.buildType(Map::class.java, String::class.java, List::class.java, Integer::class.java) 
                        // 构建 Map<String, List<Integer>>
                        for (i in 3 until types.size) {
                            beforeType = ParameterizedTypeImpl(
                                arrayOf(beforeType),
                                null,
                                types[i]
                            )
                        }
                    }
                }
            }
        }

        // 返回最终构建的ParameterizedType，如果没有任何Type传入，则返回null。
        return beforeType
    }
}