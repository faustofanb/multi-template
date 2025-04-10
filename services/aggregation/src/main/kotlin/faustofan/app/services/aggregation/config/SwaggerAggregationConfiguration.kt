package faustofan.app.services.aggregation.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger 配置类，用于配置 API 文档的相关信息。
 *
 * @param serverPort 服务器端口号，默认为 8080。
 * @param contextPath 服务器的上下文路径，默认为空。
 */
@Configuration
class SwaggerAggregationConfiguration (
	@Value("\${server.port:8080}")
	private val serverPort: String,

	@Value("\${server.servlet.context-path:}")
	private val contextPath: String
): ApplicationRunner {
		val logger = LoggerFactory.getLogger(SwaggerAggregationConfiguration::class.java)!!

		/**
		 * 在应用启动时运行，输出 API 文档的访问地址。
		 *
		 * @param args 应用启动参数，可为空。
		 */
		override fun run(args: ApplicationArguments?) {
			logger.info("API Document: http://127.0.0.1:$serverPort$contextPath/doc.html")
		}

		/**
		 * 配置 OpenAPI 信息，包括标题、描述和版本号。
		 *
		 * @return 配置好的 OpenAPI 对象。
		 */
		@Bean
		fun aggregationOpenAPI(): OpenAPI {
			return OpenAPI().info(
				Info()
					.title("Gradle多模块模板")
					.version("v1.0.0")
			)
		}
}