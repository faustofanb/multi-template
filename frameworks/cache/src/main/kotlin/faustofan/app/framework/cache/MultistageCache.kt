package faustofan.app.framework.cache

/**
 * MultistageCache接口定义了一个多阶段缓存，它是Cache接口的子接口。
 * 多阶段缓存允许在不同的缓存阶段存储和检索数据，以优化访问效率和缓存策略。
 */
interface MultistageCache : Cache