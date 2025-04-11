package faustofan.app.framework.idempotent.enums

/**
 * 定义消息消费状态的枚举类
 */
enum class IdempotentMQConsumeStatusEnum(val code: String) {
    // 消息正在被消费的状态
    CONSUMING("0"),
    // 消费完成的状态
    CONSUMED("1");

    companion object {
        /**
         * 判断消息消费状态是否为正在消费
         *
         * @param consumeStatus 消费状态字符串
         * @return 如果是正在消费状态则返回true，否则返回false
         */
        fun isError(consumeStatus: String): Boolean {
            return CONSUMING.code == consumeStatus
        }
    }
}