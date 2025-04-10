plugins {
	id("service-convention")
}

dependencies {
	// 依赖 :frameworks 模块
	api(project(":frameworks:common"))
	api(project(":frameworks:web"))
	// 添加 Spring Boot Web 依赖
	implementation(libs.spring.boot.starter.web)
	// Knife4j 增强版接口文档
	implementation(libs.knife4j.spring.boot.starter)
	// MyBatis-Plus 支持
	implementation(libs.mybatis.plus.boot.starter)
	// MyBatis Spring Boot Starter
	implementation(libs.mybatis.spring.boot.starter)
	// Spring Boot Validation - 使用 libs 引用
	implementation(libs.spring.boot.starter.validation)
	// SQLite JDBC Driver - 使用 libs 引用
	runtimeOnly(libs.sqlite.jdbc)
}

tasks.test {
	enabled = false
}
