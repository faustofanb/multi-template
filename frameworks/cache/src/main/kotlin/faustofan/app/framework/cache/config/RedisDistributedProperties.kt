package faustofan.app.framework.cache.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.concurrent.TimeUnit

/**
 * 此数据类用于配置Redis分布式缓存的属性。
 * 属性包括缓存键前缀、前缀字符集、值超时时间以及时间单位。
 *
 * @param prefix 缓存键的前缀，用于区分不同模块或环境的缓存，默认为空字符串
 * @param prefixCharset 缓存键前缀的字符集，用于处理非ASCII字符，默认为"UTF-8"
 * @param valueTimeout 缓存值的超时时间，超过此时间缓存将自动失效，默认为30000毫秒
 * @param valueTimeUnit 超时时间的时间单位，用于解释valueTimeout的值，默认为毫秒
 */
@ConfigurationProperties(prefix = RedisDistributedProperties.PREFIX)
data class RedisDistributedProperties(
	val prefix: String = "",
	val prefixCharset: String = "UTF-8",
	val valueTimeout: Long = 30000L,
	val valueTimeUnit: TimeUnit = TimeUnit.MILLISECONDS
) {
	companion object {
		const val PREFIX: String = "framework.cache.redis"
	}
}