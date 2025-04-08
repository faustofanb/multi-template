// Convention plugin specifically for the :frameworks module.
// Applies base and Spring Boot conventions.
plugins {
    id("base-convention")
    id("spring-convention")
}

// 配置打包任务
tasks.bootJar {
    // 禁用 bootJar，因为 :frameworks 不是可执行应用
    enabled = false
}
tasks.jar {
    // 启用标准 jar 打包
    enabled = true
}
