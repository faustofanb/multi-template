package faustofan.app.services.aggregation.dao.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import faustofan.app.services.aggregation.dao.entity.TestDataDO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

/**
 * TestDataDAO 的 Mapper 接口。
 * 继承自 MyBatis-Plus 的 BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
@Repository
interface TestDataMapper : BaseMapper<TestDataDO> {

    /**
     * 根据名称查找数据
     * @param name 要查找的数据名称
     * @return 包含匹配名称的测试数据列表
     */
    fun findByName(@Param("name") name: String): List<TestDataDO>

    // 可以添加更多自定义查询方法
    // fun findByValueGreaterThan(@Param("value") value: Double): List<TestDataDAO>
}