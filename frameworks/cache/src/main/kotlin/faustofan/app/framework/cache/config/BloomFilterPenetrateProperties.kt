package faustofan.app.framework.cache.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 此数据类用于配置布隆过滤器(Bloom Filter)防止缓存穿透的属性。
 * 属性包括布隆过滤器的名称、预期插入次数以及假阳性概率。
 *
 * @param name 布隆过滤器的名称，用于标识不同的布隆过滤器实例，默认为"cache_penetration_bloom_filter"
 * @param expectedInsertions 预期插入到布隆过滤器中的条目数量，这个值会影响布隆过滤器的大小和性能，默认为64
 * @param falseProbability 允许的假阳性概率，值越小过滤越精确但占用空间越大，默认为0.03
 */
@ConfigurationProperties(prefix = BloomFilterPenetrateProperties.PREFIX)
data class BloomFilterPenetrateProperties(
	val name: String = "cache_penetration_bloom_filter",
	val expectedInsertions: Long = 64L,
	val falseProbability: Double = 0.03
) {
	companion object {
		const val PREFIX: String = "framework.cache.redis.bloom-filter.default"
	}
}