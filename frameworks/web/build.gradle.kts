plugins {
    id("framework-convention")
}

dependencies {
    // 添加 Spring Boot Web 依赖
    implementation(libs.spring.boot.starter.web)
    // 添加 Redis starter 依赖
    implementation(libs.spring.boot.starter.data.redis)
    
    // 测试依赖
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

tasks.test {
    enabled = true
    useJUnitPlatform()
}
