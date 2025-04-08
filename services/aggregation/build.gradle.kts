plugins {
	id("service-convention")
}

dependencies {
	// 依赖 :frameworks 模块
//	implementation(project(":frameworks"))
	// 添加 Spring Boot Web 依赖
	implementation(libs.spring.boot.starter.web)
	// Spring Boot Actuator，提供监控端点
//	implementation(libs.spring.boot.starter.actuator)
	// Micrometer Prometheus 注册表，用于将指标暴露给 Prometheus
//	implementation(libs.micrometer.prometheus)
}

tasks.test {
	enabled = false
}