// Base convention plugin providing common configurations for all subprojects.
// Includes group, version, Java/Kotlin toolchains, and dependency resolution strategies.

plugins {
	// 应用 Kotlin JVM 插件，作为项目的基础（如果所有子项目都是 Kotlin）
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.plugin.noarg")
}

// 设置通用的 group
group = "faustofan.app"
// 从 gradle.properties 文件读取项目版本号
version = providers.gradleProperty("projectVersion").get()

// 配置 Java 工具链
java {
	toolchain {
		// 设置所有项目使用的 Java 版本
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

// 配置通用的 Kotlin 编译选项
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		// 设置 Kotlin 编译的目标 JVM 版本
		jvmTarget = "17"
		freeCompilerArgs += "-Xjsr305=strict" // 可以在这里添加通用的编译器参数
	}
}

// 集中处理所有配置（compileClasspath, runtimeClasspath 等）的依赖解析
configurations.all {
	// 定义依赖解析策略
	resolutionStrategy {
		// 定义处理能力冲突 (capability conflict) 的规则
		capabilitiesResolution {
			// 当 org.codehaus.groovy:groovy 和 org.apache.groovy:groovy 提供相同能力时
			withCapability("org.codehaus.groovy:groovy") {
				// 优先选择 org.apache.groovy:groovy，版本 0 表示选择可用的最高版本
				select("org.apache.groovy:groovy:0")
			}
			// 对 groovy-json 也应用相同的规则
			withCapability("org.codehaus.groovy:groovy-json") {
				select("org.apache.groovy:groovy-json:0")
			}
			// 对 groovy-xml 也应用相同的规则
			withCapability("org.codehaus.groovy:groovy-xml") {
				select("org.apache.groovy:groovy-xml:0")
			}
		}
	}
}
