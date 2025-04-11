plugins {
	id("framework-convention")
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(project(":frameworks:base"))
	implementation(libs.spring.boot.starter.data.redis)
	implementation(libs.redisson.spring.boot.starter)
	implementation(libs.fastjson2)
	annotationProcessor(libs.spring.boot.configuration.processor)
}
