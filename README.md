# Kotlin Spring Boot Multi-Module Template

[![Gradle Build Scan](https://img.shields.io/badge/Gradle%20Build%20Scan-%E2%9E%9A-blue?logo=gradle)](https://gradle.com/terms-of-service)

A Kotlin Spring Boot multi-module application template built with Gradle.

## ✨ Features

*   **Language**: Kotlin (JVM 17)
*   **Framework**: Spring Boot 3.x
*   **Architecture**: Multi-module
*   **Build System**: Gradle (Kotlin DSL)

## 🏗️ Project Structure

*   `buildSrc`: Shared build logic and convention plugins
*   `:frameworks`: Core classes, utilities, and configurations
*   `:services`: Aggregator module for business services
*   `:services:aggregation`: Example Spring Boot Web application
*   `:tests`: Integration and end-to-end tests

## 🚀 Getting Started

### Prerequisites

*   JDK 17+
*   Git

### Build

```bash
./gradlew build
```

### Run

```bash
# Run application
./gradlew :services:aggregation:bootRun

# Build and run JAR
./gradlew :services:aggregation:build
java -jar services/aggregation/build/libs/aggregation-0.0.1-SNAPSHOT.jar
```

### Test

```bash
./gradlew test
```

## 🤝 Contributing

Welcome to submit issues or pull requests.

## 📄 License

(Add your license here)

---

# Kotlin Spring Boot 多模块模板

[![Gradle Build Scan](https://img.shields.io/badge/Gradle%20Build%20Scan-%E2%9E%9A-blue?logo=gradle)](https://gradle.com/terms-of-service)

使用 Gradle 构建的 Kotlin Spring Boot 多模块应用模板。

## ✨ 特性

*   **语言**: Kotlin (JVM 17)
*   **框架**: Spring Boot 3.x
*   **架构**: 多模块
*   **构建系统**: Gradle (Kotlin DSL)

## 🏗️ 项目结构

*   `buildSrc`: 共享构建逻辑和约定插件
*   `:frameworks`: 核心类、工具和配置
*   `:services`: 业务服务聚合模块
*   `:services:aggregation`: Spring Boot Web 应用示例
*   `:tests`: 集成和端到端测试

## 🚀 开始使用

### 先决条件

*   JDK 17+
*   Git

### 构建

```bash
./gradlew build
```

### 运行

```bash
# 运行应用
./gradlew :services:aggregation:bootRun

# 构建并运行 JAR
./gradlew :services:aggregation:build
java -jar services/aggregation/build/libs/aggregation-0.0.1-SNAPSHOT.jar
```

### 测试

```bash
./gradlew test
```

## 🤝 贡献

欢迎提交 Issue 或 Pull Request。

## 📄 许可证

(在此添加许可证信息)