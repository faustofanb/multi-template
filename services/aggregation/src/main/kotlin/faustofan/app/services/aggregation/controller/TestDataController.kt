package faustofan.app.services.aggregation.controller

import faustofan.app.framework.web.result.CommonResp
import faustofan.app.services.aggregation.dto.req.CreateTestDataReqDTO
import faustofan.app.services.aggregation.dto.resp.TestDataRespDTO
import faustofan.app.services.aggregation.service.TestDataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * TestData 的 RESTful API 控制器。
 * 提供对测试数据的 CRUD 操作接口。
 */
@RestController
@RequestMapping("/api/v1/test-data") // API 基础路径
@Tag(name = "TestData API", description = "用于管理测试数据的 API") // Swagger Tag
class TestDataController(private val testDataService: TestDataService) {

    @Operation(summary = "获取所有测试数据", description = "返回数据库中所有测试数据的列表。")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", 
            description = "成功获取数据列表",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = TestDataRespDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllTestData(): CommonResp<*> {
        val testDataList = testDataService.getAllTestData()
        return CommonResp.success(testDataList.map { TestDataRespDTO.fromEntity(it) })
    }

    @Operation(summary = "根据 ID 获取测试数据", description = "根据提供的 ID 返回单个测试数据。")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "成功找到数据",
            content = [Content(mediaType = "application/json",schema = Schema(implementation = TestDataRespDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "未找到指定 ID 的数据", content = [Content()])
    ])
    @GetMapping("/{id}")
    fun getTestDataById(
        @Parameter(description = "要获取的数据的 ID", required = true, example = "1")
        @PathVariable id: Long
    ): CommonResp<*> {
        val testData = testDataService.getTestDataById(id)
        return CommonResp.success(TestDataRespDTO.fromEntity(testData))
    }

    @Operation(summary = "创建新的测试数据", description = "创建一个新的测试数据条目。")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "数据创建成功",
            content = [Content(mediaType = "application/json",schema = Schema(implementation = TestDataRespDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "请求体无效 (例如，校验失败)", content = [Content()])
    ])
    @PostMapping
    fun createTestData(
        @Parameter(description = "要创建的数据详情", required = true, schema = Schema(implementation = CreateTestDataReqDTO::class))
        @Valid @RequestBody request: CreateTestDataReqDTO
    ): CommonResp<*> {
        val testDataToCreate = request.toEntity()
        val createdTestData = testDataService.createTestData(testDataToCreate)
        return CommonResp.success(TestDataRespDTO.fromEntity(createdTestData))
    }

    @Operation(summary = "更新现有的测试数据", description = "根据 ID 更新一个现有的测试数据条目。")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "数据更新成功",
            content = [Content(mediaType = "application/json",schema = Schema(implementation = TestDataRespDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "请求体无效", content = [Content()]),
        ApiResponse(responseCode = "404", description = "未找到指定 ID 的数据", content = [Content()])
    ])
    @PutMapping("/{id}")
    fun updateTestData(
        @Parameter(description = "更新后的数据详情", required = true,schema = Schema(implementation = CreateTestDataReqDTO::class))
        @PathVariable id: Long,
        @Valid
        @RequestBody
        request: CreateTestDataReqDTO
    ): CommonResp<*> {
        val testDataToUpdate = request.toEntity()
        val updatedTestData = testDataService.updateTestData(id, testDataToUpdate)
        return CommonResp.success(TestDataRespDTO.fromEntity(updatedTestData))
    }

    @Operation(summary = "删除测试数据", description = "根据 ID 删除一个测试数据条目。")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "数据删除成功", content = [Content()]),
        ApiResponse(responseCode = "404", description = "未找到指定 ID 的数据", content = [Content()])
    ])
    @DeleteMapping("/{id}")
    fun deleteTestData(
        @Parameter(description = "要删除的数据的 ID", required = true, example = "1")
        @PathVariable id: Long
    ): CommonResp<*> {
        testDataService.deleteTestData(id)
        return CommonResp.success(null)
    }

    @Operation(summary = "根据名称查找测试数据", description = "查找名称与给定字符串匹配的测试数据。")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "成功找到数据或返回空列表",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TestDataRespDTO::class))]
        )
    ])
    @GetMapping("/search")
    fun findTestDataByName(
        @Parameter(description = "要搜索的数据名称", required = true, example = "示例数据")
        @RequestParam name: String
    ): CommonResp<*> {
        val testDataList = testDataService.findTestDataByName(name)
        return CommonResp.success(testDataList.map { TestDataRespDTO.fromEntity(it) })
    }

    @Operation(summary = "简单 Ping 测试", description = "一个简单的 API 端点，用于测试服务是否正在运行。")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "服务运行正常",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TestDataRespDTO::class))]
        )
    ])
    @GetMapping("/ping")
    fun ping(): CommonResp<String> {
        return CommonResp.success("Pong!")
    }
}


