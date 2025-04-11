import faustofan.app.framework.base.context.ApplicationContextHolder
import faustofan.app.framework.idempotent.enums.IdempotentSceneEnum
import faustofan.app.framework.idempotent.enums.IdempotentTypeEnum
import faustofan.app.framework.idempotent.handler.IdempotentParamService
import faustofan.app.framework.idempotent.handler.IdempotentSpELByMQExecuteHandler
import faustofan.app.framework.idempotent.handler.IdempotentSpELByRestAPIExecuteHandler
import faustofan.app.framework.idempotent.handler.IdempotentTokenService


object IdempotentExecuteHandlerFactory {
    fun getInstance(scene: IdempotentSceneEnum, type: IdempotentTypeEnum): IdempotentExecuteHandler? {
        return when(scene) {
            IdempotentSceneEnum.RESTAPI -> {
                when(type) {
                    IdempotentTypeEnum.TOKEN ->
                        ApplicationContextHolder.getBean(clazz = IdempotentParamService::class.java)
                    IdempotentTypeEnum.PARAM ->
                        ApplicationContextHolder.getBean(clazz = IdempotentTokenService::class.java)
                    IdempotentTypeEnum.SPEL ->
                        ApplicationContextHolder.getBean(clazz = IdempotentSpELByRestAPIExecuteHandler::class.java)
                }
            }
            IdempotentSceneEnum.MQ -> 
                ApplicationContextHolder.getBean(clazz = IdempotentSpELByMQExecuteHandler::class.java)
        }
    }
}
