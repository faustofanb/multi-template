plugins {
    id("framework-convention")
    id("test-convention")
}

dependencies {
    // 添加 Spring Boot Web 依赖
    implementation(libs.spring.boot.starter.web)
    // 添加 Redis starter 依赖
    implementation(libs.spring.boot.starter.data.redis)
}

