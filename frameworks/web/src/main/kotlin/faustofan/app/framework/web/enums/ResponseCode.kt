package faustofan.app.framework.web.enums

/**
 * 错误码枚举
 *
 * 编码说明：
 * - 成功：00000
 * - 一级宏观错误码：A/B/C0000
 * - 二级错误码：A/B/C0100
 * - 三级错误码：A/B/C0101
 *
 * 分类说明：
 * - A类：客户端错误，以A开头
 * - B类：系统执行错误，以B开头
 * - C类：第三方服务调用错误，以C开头
 */
enum class ErrorCode(
    val code: String,
    val message: String
) {
    /**
     * 成功
     */
    SUCCESS("00000", "成功"),

    /**
     * 一级宏观错误码
     */
    CLIENT_ERROR("A0000", "用户端错误"),
    SYSTEM_ERROR("B0000", "系统执行错误"),
    THIRD_PARTY_ERROR("C0000", "第三方服务错误"),

    /**
     * 二级错误码 - A类用户错误
     */
    USER_REGISTRATION_ERROR("A0100", "用户注册错误"),
    USER_LOGIN_ERROR("A0200", "用户登录异常"),
    ACCESS_PERMISSION_ERROR("A0300", "访问权限异常"),
    REQUEST_PARAM_ERROR("A0400", "用户请求参数错误"),
    USER_REQUEST_ERROR("A0500", "用户请求服务异常"),
    USER_RESOURCE_ERROR("A0600", "用户资源异常"),
    USER_UPLOAD_ERROR("A0700", "用户上传文件异常"),
    USER_OPERATION_ERROR("A0800", "用户当前版本操作异常"),

    /**
     * 三级错误码 - A类具体错误
     */
    INVALID_PARAM("A0401", "请求参数格式不匹配"),
    MISSING_REQUIRED_PARAM("A0402", "请求必填参数为空"),
    PARAM_VALIDATION_FAILED("A0403", "请求参数校验失败"),
    UNAUTHORIZED("A0301", "访问未授权"),
    FORBIDDEN("A0302", "访问被禁止"),
    NOT_FOUND("A0404", "请求资源不存在"),
    METHOD_NOT_ALLOWED("A0405", "请求方法不允许"),
    REQUEST_TIMEOUT("A0504", "请求超时"),
    TOO_MANY_REQUESTS("A0501", "请求次数超出限制"),
    USER_UPLOAD_FILE_TYPE_ERROR("A0701", "用户上传文件类型不匹配"),
    USER_UPLOAD_FILE_SIZE_ERROR("A0702", "用户上传文件太大"),
    USER_UPLOAD_FILE_EMPTY("A0703", "用户上传文件为空"),

    /**
     * 二级错误码 - B类系统错误
     */
    SYSTEM_EXECUTION_ERROR("B0100", "系统执行超时"),
    SYSTEM_DISASTER_RECOVERY_ERROR("B0200", "系统容灾功能被触发"),
    SYSTEM_RESOURCE_ERROR("B0300", "系统资源异常"),

    /**
     * 三级错误码 - B类具体错误
     */
    SYSTEM_TIMEOUT("B0101", "系统执行超时"),
    SYSTEM_LIMIT_ERROR("B0102", "系统限流"),
    SYSTEM_DEGRADATION("B0103", "系统功能降级"),
    SYSTEM_RESOURCE_EXHAUSTION("B0301", "系统资源耗尽"),
    SYSTEM_DISK_FULL("B0302", "系统磁盘空间不足"),
    SYSTEM_MEMORY_FULL("B0303", "系统内存不足"),
    SYSTEM_CPU_HIGH("B0304", "系统CPU占用过高"),

    /**
     * 二级错误码 - C类第三方服务错误
     */
    MIDDLEWARE_SERVICE_ERROR("C0100", "中间件服务出错"),
    THIRD_PARTY_SERVICE_ERROR("C0200", "第三方系统服务出错"),

    /**
     * 三级错误码 - C类具体错误
     */
    DATABASE_ERROR("C0101", "数据库服务异常"),
    CACHE_ERROR("C0102", "缓存服务异常"),
    MESSAGE_SERVICE_ERROR("C0103", "消息服务异常"),
    MESSAGE_PUBLISH_ERROR("C0104", "消息发送异常"),
    MESSAGE_CONSUME_ERROR("C0105", "消息消费异常"),
    RPC_SERVICE_ERROR("C0110", "RPC服务出错"),
    RPC_SERVICE_NOT_FOUND("C0111", "RPC服务未找到"),
    RPC_SERVICE_NOT_REGISTERED("C0112", "RPC服务未注册"),
    API_NOT_EXIST("C0113", "接口不存在"),
    API_GATEWAY_ERROR("C0201", "网关服务出错"),
    API_GATEWAY_TIMEOUT("C0202", "网关响应超时"),
    API_GATEWAY_NOT_FOUND("C0203", "网关服务未找到");

    companion object {
        /**
         * 根据错误码获取枚举
         */
        fun fromCode(code: String): ErrorCode {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("未知的错误码: $code")
        }

        /**
         * 判断是否为成功码
         */
        fun isSuccess(code: String): Boolean = code == SUCCESS.code

        /**
         * 判断是否为客户端错误
         */
        fun isClientError(code: String): Boolean = code.startsWith("A")

        /**
         * 判断是否为系统错误
         */
        fun isSystemError(code: String): Boolean = code.startsWith("B")

        /**
         * 判断是否为第三方服务错误
         */
        fun isThirdPartyError(code: String): Boolean = code.startsWith("C")
    }
} 