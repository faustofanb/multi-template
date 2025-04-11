package faustofan.app.framework.cache.config

import faustofan.app.framework.cache.impl.StringRedisTemplateProxy
import org.redisson.api.RBloomFilter
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * 自动配置类，用于缓存相关的配置。
 *
 * @param redisDistributedProperties Redis分布式属性配置，用于配置Redis相关的属性。
 */
@AutoConfiguration
@EnableConfigurationProperties(BloomFilterPenetrateProperties::class, RedisDistributedProperties::class)
class CacheAutoConfiguration(
	private val redisDistributedProperties: RedisDistributedProperties
) {

	/**
	 * 配置Redis键的序列化方式。
	 *
	 * @return RedisKeySerializer 实例，用于序列化Redis的键。
	 */
	@Bean
	fun redisKeySerializer(): RedisKeySerializer {
		return RedisKeySerializer(redisDistributedProperties.prefix, redisDistributedProperties.prefixCharset)
	}

	/**
	 * 创建布隆过滤器，用于防止缓存穿透。
	 *
	 * 此@Bean只在BloomFilterPenetrateProperties中enabled属性为true时被创建。
	 *
	 * @param redissonClient Redisson客户端，用于操作Redis。
	 * @param bloomFilterPenetrateProperties 布隆过滤器穿透属性配置，用于配置布隆过滤器的相关属性。
	 * @return RBloomFilter<String> 布隆过滤器实例，用于过滤可能不存在的数据。
	 */
	@Bean
	@ConditionalOnProperty(
		prefix = BloomFilterPenetrateProperties.PREFIX,
		name = ["enabled"],
		havingValue = "true",
	)
	fun cachePenetrationBloomFilter(
		redissonClient: RedissonClient,
		bloomFilterPenetrateProperties: BloomFilterPenetrateProperties
	): RBloomFilter<String> {
		return redissonClient.getBloomFilter<String>(bloomFilterPenetrateProperties.name)
			.apply {
				this.tryInit(
					bloomFilterPenetrateProperties.expectedInsertions,
					bloomFilterPenetrateProperties.falseProbability
				)
			}
	}

	/**
	 * 创建一个StringRedisTemplate的代理类，用于增强StringRedisTemplate的功能。
	 *
	 * @param redisKeySerialize Redis键的序列化器，用于序列化和反序列化Redis的键。
	 * @param stringRedisTemplate 原始的StringRedisTemplate实例。
	 * @param redissonClient Redisson客户端，用于操作Redis。
	 * @return StringRedisTemplateProxy StringRedisTemplate的代理类，用于缓存操作。
	 */
	@Bean
	fun stringRedisTemplateProxy(
		redisKeySerialize: RedisKeySerializer,
		stringRedisTemplate: StringRedisTemplate,
		redissonClient: RedissonClient
	): StringRedisTemplateProxy {
		return StringRedisTemplateProxy(
			stringRedisTemplate.apply { keySerializer = redisKeySerialize },
			redisDistributedProperties,
			redissonClient
		)
	}
}