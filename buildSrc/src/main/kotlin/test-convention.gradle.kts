// Convention plugin for the :tests module.
// Applies Spring Boot conventions and configures testing and packaging.
plugins {
    // 应用 Spring Boot 约定插件 (继承了 base-convention)
    id("spring-convention")
}

// 获取对名为 "libs" 的版本目录的引用
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

// 定义通用的 测试 依赖
dependencies {
    // Spring Boot 测试启动器 (用于测试范围)
    testImplementation(libs.findLibrary("spring.boot.starter.test").orElse(provider { null }))
    // 使用test bundle引入测试相关依赖
    testImplementation(libs.findBundle("test").orElse(provider { null }))
}

// 配置 Spring Boot 打包任务
tasks.bootJar {
    // 禁用 bootJar 任务，因为 :tests 模块通常不需要打成可执行 Spring Boot 包
    enabled = false
}

// 配置标准的 jar 打包任务
tasks.jar {
    // 启用标准的 jar 任务，允许将 :tests 打包成普通 jar (如果需要)
    enabled = true
}

tasks.test {
    enabled = true
    useJUnitPlatform()
}