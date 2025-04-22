plugins {
    id("framework-convention")
    id("test-convention")
}

dependencies {
    //frameworks
    implementation(project(":frameworks:base"))
    implementation(project(":frameworks:common"))
    implementation(project(":frameworks:web"))
    implementation(project(":frameworks:cache"))

    // Spring Boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.configuration.processor)
    
    // Redisson
    implementation(libs.redisson.spring.boot.starter)

    //Tool
    implementation(libs.hutool.all)
    implementation(libs.fastjson2)

}

