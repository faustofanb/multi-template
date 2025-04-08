// Build script for the :frameworks subproject.
// Applies the 'framework-convention' which includes base and Spring Boot configurations.
plugins {
    // 应用框架层约定插件
    id("framework-convention")
}

// 定义 :frameworks 模块特有的依赖项
dependencies {
    // 数据库和持久层相关
    implementation(libs.mybatis.spring.boot.starter)
    implementation(libs.mybatis.plus.boot.starter)
    implementation(libs.shardingsphere.jdbc.core) // 分库分表
    // JSON 处理
    implementation(libs.fastjson2)
    // 对象映射
    implementation(libs.dozer.core)
    // 工具类库
    implementation(libs.hutool.all)
    // Redis 客户端
    implementation(libs.redisson.spring.boot.starter)
    // Guava 库
    implementation(libs.guava)
    // 分布式任务调度
    implementation(libs.xxl.job.core)
    // 支付宝 SDK
    implementation(libs.alipay.sdk.java)
    // 消息队列
    implementation(libs.rocketmq.spring.boot.starter)
    // 线程变量传递
    implementation(libs.transmittable.thread.local)
    // 动态线程池
    implementation(libs.hippo4j.threadpool.config)
    // 指标监控 (Prometheus)
    implementation(libs.micrometer.prometheus)
    // 分布式追踪 (SkyWalking)
    implementation(libs.skywalking.trace)
}