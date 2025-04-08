// Convention plugin for microservice modules (like :services).
// Applies Spring Boot conventions and configures packaging.
plugins {
    // 应用 Spring Boot 约定插件 (继承了 base-convention)
    id("spring-convention")
}

// 配置 Spring Boot 打包任务
tasks.bootJar {
    // 启用 bootJar 任务，生成可执行的 Spring Boot jar 包
    enabled = true
}

// 配置标准的 jar 打包任务
tasks.jar {
    // 禁用标准的 jar 任务，因为我们使用 bootJar 来打包服务
    enabled = false
} 