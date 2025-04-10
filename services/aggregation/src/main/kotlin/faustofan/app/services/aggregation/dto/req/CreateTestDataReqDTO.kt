package faustofan.app.services.aggregation.dto.req

import faustofan.app.framework.common.annotation.NoArgConstructor
import faustofan.app.services.aggregation.dao.entity.TestDataDO
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

/**
 * 用于创建或更新测试数据的请求体 DTO。
 * 包含数据校验注解。
 */
@Schema(description = "创建或更新测试数据的请求模型")
@NoArgConstructor
data class CreateTestDataReqDTO(
    @field:NotBlank(message = "名称不能为空")
    @field:Size(max = 100, message = "名称长度不能超过100个字符")
    @Schema(description = "数据名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "新的测试数据")
    val name: String,

    @field:Size(max = 500, message = "描述长度不能超过500个字符")
    @Schema(description = "数据描述 (可选)", example = "这是新数据的描述信息。")
    val description: String?,

    @field:NotNull(message = "值不能为空")
    @field:PositiveOrZero(message = "值必须大于或等于零")
    @Schema(description = "数据值", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.9")
    val value: Double
) {
    /**
     * 将请求 DTO 转换为 TestDataDAO 实体。
     * 注意：ID 和时间戳不会在此处设置。
     */
    fun toEntity(): TestDataDO {
        return TestDataDO(
            name = this.name,
            description = this.description,
            value = this.value
            // id, createdAt, updatedAt 将由服务层或 MyBatis-Plus 处理
        )
    }
}
