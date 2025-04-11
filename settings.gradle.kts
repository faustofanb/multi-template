// 启用特性预览，例如类型安全的项目访问器（如果需要跨项目类型安全引用）
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// 管理插件版本和仓库的配置块
pluginManagement {
	// 定义查找 Gradle 插件的仓库
	repositories {
		// Gradle 官方插件门户
		gradlePluginPortal()
		// Maven 中央仓库
		mavenCentral()
	}
}

// 应用根项目级别的插件
plugins {
	// Foojay 工具链插件，用于自动下载和管理 JDK
	// https://github.com/gradle/foojay-toolchains
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
	// 添加 Gradle Enterprise 插件以启用 Build Scans
	id("com.gradle.develocity") version "3.17" // 使用一个较新的版本
}

// 集中管理所有项目的依赖解析行为
dependencyResolutionManagement {
	// 强制所有项目都使用这里定义的仓库，禁止在子项目 build.gradle.kts 中单独定义仓库
	@Suppress("UnstableApiUsage")
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	// 定义项目依赖项的查找仓库
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		// 可以在这里添加其他仓库，例如公司私服或镜像
		// maven { url = uri("https://maven.aliyun.com/repository/public") }
	}
}

// 配置 Gradle Enterprise 插件 (Build Scans)
develocity {
	buildScan {
		// 同意 Gradle Build Scan 的服务条款
		// 首次运行时，你可能需要在命令行确认
		termsOfUseUrl = "https://gradle.com/terms-of-service"
		termsOfUseAgree = "yes"
	}
}

// 设置根项目的名称
rootProject.name = "multi-template"

// frameworks模块
include(":frameworks")
include(":frameworks:common")
include(":frameworks:web")
include(":frameworks:idempotent")
include(":frameworks:base")
include(":frameworks:cache")


// services模块
include(":services")
include(":services:aggregation")

// tests模块
include(":tests")
