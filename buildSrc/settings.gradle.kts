import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

// buildSrc 构建自身的插件管理配置
// 定义 buildSrc 内部编译约定插件 (.gradle.kts 文件) 时所需的插件及其版本
pluginManagement {
	// 定义查找 Gradle 插件的仓库 (仅用于 buildSrc 内部编译)
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
	// 定义 buildSrc 内部编译时使用的插件版本
	// 注意：这里使用硬编码版本，因为在 settings 文件配置阶段，版本目录可能尚不可用
	plugins {
		id("org.springframework.boot") version "3.0.7"
		id("io.spring.dependency-management") version "1.1.4"
		id("org.jetbrains.kotlin.jvm") version "1.9.0"
		id("org.jetbrains.kotlin.plugin.spring") version "1.9.0"
	}
}

// 设置 buildSrc 项目的名称 (通常保持 "buildSrc")
rootProject.name = "buildSrc"