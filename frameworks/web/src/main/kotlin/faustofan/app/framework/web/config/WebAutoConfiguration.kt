package faustofan.app.framework.web.config

import faustofan.app.framework.common.annotation.FilterOrderConstant.USER_TRANSMIT_FILTER_ORDER
import faustofan.app.framework.web.context.UserTransmitFilter
import faustofan.app.framework.web.handler.GlobalExceptionHandler
import faustofan.app.framework.web.handler.ResponseBodyAdvice
import faustofan.app.framework.web.interceptor.RateLimitInterceptor
import faustofan.app.framework.web.interceptor.RequestContextInterceptor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web自动配置类
 * 用于自动配置Web相关的组件
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class WebAutoConfiguration {

	@Bean
	fun globalExceptionHandler(): GlobalExceptionHandler =
		GlobalExceptionHandler()

	@Bean
	fun responseBodyAdvice(): ResponseBodyAdvice =
		ResponseBodyAdvice()

	/**
	 * 注册一个用户传输过滤器，用于在每个请求中传递用户信息。
	 *
	 * @return FilterRegistrationBean<UserTransmitFilter> 对象，用于配置和注册过滤器。
	 */
	@Bean
	fun globalUserTransmitFilter(): FilterRegistrationBean<UserTransmitFilter> {
		return FilterRegistrationBean<UserTransmitFilter>().apply {
			filter = UserTransmitFilter()
			urlPatterns = listOf("/*")
			order = USER_TRANSMIT_FILTER_ORDER
		}
	}
	/**
	 * 配置请求上下文拦截器
	 */
	@Bean
	fun requestContextInterceptor(): RequestContextInterceptor {
		return RequestContextInterceptor()
	}

	/**
	 * 配置请求限流拦截器
	 */
	@Bean
	@ConditionalOnBean(StringRedisTemplate::class)
	fun rateLimitInterceptor(redisTemplate: StringRedisTemplate): RateLimitInterceptor {
		return RateLimitInterceptor(redisTemplate)
	}

	/**
	 * 配置Web MVC
	 */
	@Bean
	fun webMvcConfigurer(
		requestContextInterceptor: RequestContextInterceptor,
		rateLimitInterceptor: RateLimitInterceptor?
	): WebMvcConfigurer {
		return object : WebMvcConfigurer {
			override fun addInterceptors(registry: InterceptorRegistry) {
				// 请求上下文拦截器（最先执行）
				registry.addInterceptor(requestContextInterceptor)
					.addPathPatterns("/api/**")  // 只拦截API路径
					.excludePathPatterns(         // 排除文档路径
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/doc.html",
						"/webjars/**"
					)
					.order(1)

				// 请求限流拦截器
				rateLimitInterceptor?.let {
					registry.addInterceptor(it)
						.addPathPatterns("/api/**")  // 只拦截API路径
						.excludePathPatterns(         // 排除文档路径
							"/swagger-ui/**",
							"/v3/api-docs/**",
							"/doc.html",
							"/webjars/**"
						)
						.order(2)
				}

			}
		}
	}
} 