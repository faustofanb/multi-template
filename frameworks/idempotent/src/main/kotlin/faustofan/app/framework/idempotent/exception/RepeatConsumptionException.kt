package faustofan.app.framework.idempotent.exception

/**
 * 重复消费异常
 * 用于处理MQ场景下的重复消费情况
 *
 * @property error 布尔值，true表示消息处理中，false表示已处理成功
 */
class RepeatConsumptionException(val error: Boolean) : RuntimeException() 