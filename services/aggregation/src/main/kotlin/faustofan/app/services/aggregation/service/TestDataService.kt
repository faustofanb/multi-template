package faustofan.app.services.aggregation.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.ClientException
import faustofan.app.framework.web.exception.ServiceException
import faustofan.app.services.aggregation.dao.entity.TestDataDO
import faustofan.app.services.aggregation.dao.mapper.TestDataMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * TestData 的服务层，处理业务逻辑。
 */
@Service
class TestDataService(private val testDataMapper: TestDataMapper) : ServiceImpl<TestDataMapper, TestDataDO>() {

	/**
	 * 获取所有测试数据。
	 * @return 测试数据列表
	 */
	fun getAllTestData(): List<TestDataDO> {
		return try {
			testDataMapper.selectList(null)
		} catch (e: Exception) {
			throw ServiceException(ErrorCode.DATABASE_ERROR.code, "获取测试数据列表失败", e)
		}
	}

	/**
	 * 根据 ID 获取测试数据。
	 * @param id 数据 ID
	 * @return 找到的测试数据
	 * @throws ServiceException 如果发生数据库错误
	 */
	fun getTestDataById(id: Long): TestDataDO {
		return try {
			testDataMapper.selectById(id)
		} catch (e: Exception) {
			throw ServiceException(ErrorCode.DATABASE_ERROR.code, "获取测试数据失败", e)
		}
	}

	/**
	 * 创建新的测试数据。
	 * @param testData 要创建的测试数据对象 (ID 应为 null)
	 * @return 已创建并保存的测试数据对象 (包含生成的 ID 和时间戳)
	 * @throws ServiceException 如果发生数据库错误
	 */
	@Transactional
	fun createTestData(testData: TestDataDO): TestDataDO {
		return try {
			// 确保传入的对象没有 ID，防止意外更新
			testData.id = null
			testData.createdAt = LocalDateTime.now()
			testData.updatedAt = LocalDateTime.now()

			// 使用MyBatis-Plus插入数据
			testDataMapper.insert(testData)
			testData
		} catch (e: Exception) {
			throw ServiceException(ErrorCode.DATABASE_ERROR.code, "创建测试数据失败", e)
		}
	}

	/**
	 * 更新现有的测试数据。
	 * @param id 要更新的数据 ID
	 * @param updatedTestData 包含更新信息的测试数据对象
	 * @return 已更新的测试数据对象
	 * @throws ServiceException 如果发生数据库错误
	 */
	@Transactional
	fun updateTestData(id: Long, updatedTestData: TestDataDO): TestDataDO {
		return try {
			val existingTestData = getTestDataById(id)

			// 使用 copy() 创建新对象，避免直接修改原对象
			val updatedData = existingTestData.copy(
				name = updatedTestData.name,
				description = updatedTestData.description,
				value = updatedTestData.value,
				updatedAt = LocalDateTime.now()
			)

			testDataMapper.updateById(updatedData)
			updatedData

		} catch (e: Exception) {
			throw ServiceException(ErrorCode.DATABASE_ERROR.code, "更新测试数据失败", e)
		}
	}

	/**
	 * 根据 ID 删除测试数据。
	 * @param id 要删除的数据 ID
	 * @throws ServiceException 如果发生数据库错误
	 */
	@Transactional
	fun deleteTestData(id: Long) {
		val affectedRows = testDataMapper.deleteById(id)
		if (affectedRows == 0) {
			throw ServiceException(ErrorCode.DATABASE_ERROR.code, "删除测试数据失败")
		}
	}

	/**
	 * 根据名称查找测试数据。
	 * @param name 要查找的数据名称
	 * @return 包含匹配名称的测试数据列表
	 * @throws ServiceException 如果发生数据库错误
	 */
	fun findTestDataByName(name: String): List<TestDataDO> {
			// 方式一：使用自定义Mapper方法
			//testDataMapper.findByName(name)
			val resp = testDataMapper.selectList(KtQueryWrapper(TestDataDO::class.java).eq(TestDataDO::name, name))

			if (resp.isEmpty()) throw ClientException.notFound("未找到匹配名称的测试数据")

			return resp
			// 方式二：使用MyBatis-Plus的QueryWrapper（两种方式都可以）
			// val queryWrapper = QueryWrapper<TestDataDAO>()
			// queryWrapper.eq("name", name)
			// return testDataMapper.selectList(queryWrapper)
	}
}
