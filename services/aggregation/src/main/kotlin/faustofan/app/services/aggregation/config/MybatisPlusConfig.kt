package faustofan.app.services.aggregation.config

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.apache.ibatis.reflection.MetaObject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

/**
 * MyBatis-Plus 配置类
 * 提供自定义的插件和处理器配置
 */
@Configuration
class MybatisPlusConfig {

    /**
     * 创建 MyBatis-Plus 插件拦截器
     * 用于添加分页插件等功能
     */
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        // 添加分页插件
        interceptor.addInnerInterceptor(PaginationInnerInterceptor())
        return interceptor
    }

    /**
     * 自动填充字段处理器
     * 用于自动处理创建时间和更新时间
     */
    @Bean
    fun metaObjectHandler(): MetaObjectHandler {
        return object : MetaObjectHandler {
            override fun insertFill(metaObject: MetaObject) {
                // 插入时自动填充创建时间和更新时间
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime::class.java, LocalDateTime.now())
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
            }

            override fun updateFill(metaObject: MetaObject) {
                // 更新时自动填充更新时间
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::class.java, LocalDateTime.now())
            }
        }
    }
}
