package faustofan.app.framework.cache.config

import org.springframework.beans.factory.InitializingBean
import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.charset.Charset

/**
 * RedisKeySerializer类用于序列化和反序列化Redis的键。
 * 它使用指定的字符集和键前缀来处理字符串键。
 */
class RedisKeySerializer(
	/**
	 * 键的前缀，用于序列化时添加到键的前面。
	 */
	private val keyPrefix: String,
	/**
	 * 字符集名称，用于指定如何将字符串序列化为字节数组。
	 */
	private val charsetName: String
): InitializingBean, RedisSerializer<String> {

	/**
	 * 字符集对象，用于将字符串序列化和反序列化为字节数组。
	 */
	private lateinit var charset: Charset

	/**
	 * 初始化字符集。
	 * 在所有必需的属性设置完成后调用，确保charset被正确初始化。
	 */
	override fun afterPropertiesSet() {
		charset = Charset.forName(charsetName)
	}

	/**
	 * 序列化给定的键。
	 * 如果键为null，则返回仅包含前缀的字节数组。
	 *
	 * @param key 待序列化的键。
	 * @return 序列化后的字节数组。
	 */
	override fun serialize(key: String?): ByteArray {
		return (keyPrefix + (key ?: "")).toByteArray(charset)
	}

	/**
	 * 反序列化给定的字节数组为字符串。
	 * 如果字节数组为null，则返回空字符串。
	 *
	 * @param bytes 待反序列化的字节数组。
	 * @return 反序列化后的字符串。
	 */
	override fun deserialize(bytes: ByteArray?): String {
		return String(bytes ?: ByteArray(0), charset)
	}
}