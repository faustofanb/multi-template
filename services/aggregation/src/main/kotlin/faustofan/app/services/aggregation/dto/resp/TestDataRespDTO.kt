package faustofan.app.services.aggregation.dto.resp

import faustofan.app.framework.common.annotation.NoArgConstructor
import faustofan.app.services.aggregation.dao.entity.TestDataDO
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 用于 API 响应的测试数据传输对象 (DTO)。
 * 通常不包含敏感信息或内部实现细节。
 */
@Schema(description = "测试数据响应模型")
@NoArgConstructor
data class TestDataRespDTO(
    @Schema(description = "数据唯一标识符 (ID)", example = "1")
    val id: Long?,

    @Schema(description = "数据名称", example = "示例数据A")
    val name: String,

    @Schema(description = "数据描述", example = "这是一个关于示例数据A的详细描述。")
    val description: String?,

    @Schema(description = "数据值", example = "123.45")
    val value: Double,

    @Schema(description = "创建时间", example = "2023-10-27T10:15:30")
    val createdAt: LocalDateTime,

    @Schema(description = "最后更新时间", example = "2023-10-28T11:30:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        /**
         * 从 TestDataDAO 实体转换为 TestDataRespDTO。
         */
        fun fromEntity(entity: TestDataDO): TestDataRespDTO {
            return TestDataRespDTO(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                value = entity.value,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
