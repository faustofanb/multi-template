package faustofan.app.framework.idempotent.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = IdempotentProperties.PREFIX)
data class IdempotentProperties(
    var prefix: String? = null,
    var timeout: Long? = null
) {
    companion object {
        const val PREFIX = "framework.idempotent.token"
    }
}