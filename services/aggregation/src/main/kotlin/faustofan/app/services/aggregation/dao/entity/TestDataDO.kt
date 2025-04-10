package faustofan.app.services.aggregation.dao.entity

import com.baomidou.mybatisplus.annotation.FieldFill.*
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import faustofan.app.framework.common.annotation.NoArgConstructor
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 测试数据DAO类，使用MyBatis-Plus注解映射到数据库中的 test_data 表。
 */
@TableName("test_data")
@Schema(description = "测试数据模型")
@NoArgConstructor
data class TestDataDO(

	@TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
	@Schema(
		description = "数据唯一标识符 (ID)",
		example = "1",
		accessMode = Schema.AccessMode.READ_ONLY
	)
	var id: Long? = null,

	@TableField(value = "name")
	@Schema(
		description = "数据名称",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "示例数据A"
	)
	var name: String,

	@TableField(value = "description")
	@Schema(description = "数据描述", example = "这是一个关于示例数据A的详细描述。")
	var description: String? = null,

	@TableField(value = "value")
	@Schema(
		description = "数据值",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "123.45"
	)
	var value: Double,

	@TableField(
		value = "created_at",
		fill = INSERT
	)
	@Schema(
		description = "创建时间",
		example = "2023-10-27T10:15:30",
		accessMode = Schema.AccessMode.READ_ONLY
	)
	var createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),

	@TableField(
		value = "updated_at",
		fill = INSERT_UPDATE
	)
	@Schema(
		description = "最后更新时间",
		example = "2023-10-28T11:30:00",
		accessMode = Schema.AccessMode.READ_ONLY
	)
	var updatedAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)