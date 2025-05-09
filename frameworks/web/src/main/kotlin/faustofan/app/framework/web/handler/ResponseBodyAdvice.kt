package faustofan.app.framework.web.handler

import faustofan.app.framework.web.context.UserContext
import faustofan.app.framework.web.result.CommonResp
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * 响应处理器
 * 用于统一处理控制器方法的返回值，并将其包装为CommonResp格式
 */
@RestControllerAdvice
class ResponseBodyAdvice : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        // 如果返回值已经是CommonResp类型，则不需要再次包装
        return returnType.parameterType != CommonResp::class.java
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // 如果返回值为null，则返回空的CommonResp
        if (body == null) {
            return CommonResp.success<Any?>(null)
        }

        // 如果返回值已经是CommonResp类型，则直接返回
        if (body is CommonResp<*>) {
            return body
        }

        // 获取用户ID
        val requestId = UserContext.getRequestId()

        // 包装为CommonResp
        return CommonResp.success(body).copy(
            requestId = requestId.toString()
        )
    }
} 