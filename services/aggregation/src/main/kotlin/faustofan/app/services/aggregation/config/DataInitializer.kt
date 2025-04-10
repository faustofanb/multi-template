package faustofan.app.services.aggregation.config

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import faustofan.app.services.aggregation.dao.entity.TestDataDO
import faustofan.app.services.aggregation.dao.mapper.TestDataMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

/**
 * 数据初始化配置类。
 * 使用 CommandLineRunner 在应用启动后执行数据插入操作。
 */
@Configuration
class DataInitializer {

	private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

	/**
	 * 创建一个 CommandLineRunner Bean 来初始化数据库。
	 * @return CommandLineRunner 实例。
	 */
	@Bean
	fun initDatabase(testDataMapper: TestDataMapper) = CommandLineRunner {
		logger.info("开始初始化测试数据...")

		// 检查是否已有数据，避免重复插入 (可选，取决于需求)
		val data1 = TestDataDO(
			name = "初始数据 Alpha",
			description = "这是第一条自动生成的测试数据。",
			value = 10.5,
			createdAt = LocalDateTime.now().minusDays(1), // 模拟过去创建
			updatedAt = LocalDateTime.now().minusHours(5)
		)
		val data2 = TestDataDO(
			name = "初始数据 Beta",
			description = "这是第二条自动生成的测试数据，包含更多细节。",
			value = 25.0,
			createdAt = LocalDateTime.now().minusHours(2),
			updatedAt = LocalDateTime.now().minusMinutes(30)
		)
		val data3 = TestDataDO(
			name = "示例数据 Gamma",
			value = 0.0 // 描述为空
		)

		testDataMapper.insert(listOf(data1, data2, data3))
		logger.info("成功插入 ${testDataMapper.selectCount(KtQueryWrapper(TestDataDO::class.java))} 条初始测试数据。")
	}
}