plugins {
	// 应用 Kotlin DSL 插件，使得可以在 src/main/kotlin 中编写 .gradle.kts 约定插件
	`kotlin-dsl`
}

// 配置 buildSrc 项目本身的 Java 工具链
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

// 配置 buildSrc 项目本身的 Kotlin 工具链
kotlin {
	jvmToolchain(17)
}

// 定义 buildSrc 项目编译时所需的仓库 (主要是为了解析下面的 dependencies)
repositories {
	gradlePluginPortal()
	mavenCentral()
}

// 定义 buildSrc 项目本身的依赖项
// 这些依赖项是为了能够成功编译 src/main/kotlin 中的约定插件
dependencies {
	// Kotlin Gradle 插件是编写 Kotlin 约定插件的基础
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
	// 改回 implementation 依赖，根据之前的错误信息建议
	implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.7")
	implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.4")
	// Kotlin All-Open 插件
	implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.0")
}
