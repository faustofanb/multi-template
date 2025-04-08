// Convention plugin for the :tests module.
// Applies Spring Boot conventions and configures testing and packaging.
plugins {
    // 应用 Spring Boot 约定插件 (继承了 base-convention)
    id("spring-convention")
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