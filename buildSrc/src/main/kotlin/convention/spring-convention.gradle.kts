// Convention plugin for Spring Boot based subprojects.
// Applies base conventions, Spring Boot, Dependency Management, Kotlin JVM, Kotlin Spring plugins.
// Configures common Spring Boot dependencies and BOMs using the version catalog.
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    // 应用基础约定插件
    id("base-convention")
    // 应用 Spring Boot 插件
    id("org.springframework.boot")
    // 应用 Spring Dependency Management 插件，用于管理依赖版本 (通过 BOMs)
    id("io.spring.dependency-management")
    // 应用 Kotlin JVM 插件 (虽然 base-convention 已应用，但再次声明通常无害，有时为了清晰)
    id("org.jetbrains.kotlin.jvm")
    // 应用 Kotlin Spring 插件 (all-open)，使 Kotlin 类默认对 Spring 开放
    id("org.jetbrains.kotlin.plugin.spring")
}

// 获取对名为 "libs" 的版本目录的引用
val libsCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

// 配置 Spring Dependency Management 插件
dependencyManagement {
    // 导入 Maven BOM (Bill of Materials) 文件来管理依赖版本
    imports {
        // 导入 Spring Boot 官方的依赖管理 BOM
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libsCatalog.findVersion("spring.boot").get().requiredVersion}")
        // 导入 Spring Cloud 官方的依赖管理 BOM
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libsCatalog.findVersion("spring.cloud").get().requiredVersion}")
        // 导入 Spring Cloud Alibaba 的依赖管理 BOM
        mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${libsCatalog.findVersion("spring.cloud.alibaba").get().requiredVersion}")
    }
}

// 定义通用的 Spring Boot 项目依赖
dependencies {
    // Spring Boot 核心启动器
    implementation("org.springframework.boot:spring-boot-starter")
    // Kotlin 反射库
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Kotlin 标准库 (JDK 8 版本)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Lombok 库，用于简化样板代码 (需要 annotationProcessor)
    implementation(libsCatalog.findLibrary("lombok").get())
    annotationProcessor(libsCatalog.findLibrary("lombok").get())
    // Spring Boot 测试启动器 (用于测试范围)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// 配置通用的测试任务
tasks.withType<Test> {
    // 使用 JUnit Platform (JUnit 5) 作为测试框架
    useJUnitPlatform()
} 