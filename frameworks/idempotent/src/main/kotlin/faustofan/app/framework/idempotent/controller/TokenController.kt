package faustofan.app.framework.idempotent.controller

import faustofan.app.framework.idempotent.handler.IdempotentTokenService
import faustofan.app.framework.web.result.CommonResp
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 控制器类，用于处理与idempotent token相关的HTTP请求
 * 主要职责是委托给IdempotentTokenService来生成唯一的token，以确保操作的幂等性
 */
@RestController
class IdempotentTokenController(
    // 服务接口，用于执行与idempotent token相关的业务逻辑
    private val idempotentTokenService: IdempotentTokenService
) {
    /**
     * 创建并返回一个新的idempotent token
     * 该方法通过HTTP GET请求访问"/token"路径来调用
     *
     * @return 一个封装在Result中的新生成的idempotent token字符串，表示一个成功的操作结果
     */
    @GetMapping("/token")
    fun createToken(): CommonResp<String> = CommonResp.success(idempotentTokenService.createToken())
}