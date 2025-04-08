// Build script for the :tests subproject.
// Applies the 'test-convention' for test configurations.
plugins {
    // 应用测试约定插件
    id("test-convention")
}

// 定义 :tests 模块特有的依赖项
dependencies {
    // 依赖 :frameworks 模块，以便测试框架层的功能
    implementation(project(":frameworks"))
    // 可以在这里添加测试框架或库，例如 Mockito, AssertJ 等
    // testImplementation(...) 
} 