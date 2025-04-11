package faustofan.app.framework.idempotent.core


/**
 * 幂等上下文对象，用于在多线程环境下提供线程安全的上下文存储。
 * 主要用于在并发环境下保证某些操作的幂等性，即多次执行同样操作不产生副作用。
 */
object IdempotentContext {
    // 使用ThreadLocal存储每个线程的上下文信息，避免并发冲突
    private val CONTEXT = ThreadLocal<MutableMap<String?, Any?>>()

    /**
     * 获取当前线程的上下文信息。
     *
     * @return 当前线程的上下文，一个键值对的集合。
     */
    fun get(): MutableMap<String?, Any?> {
        return CONTEXT.get() ?: mutableMapOf()
    }

    /**
     * 根据键名获取上下文中的值。
     *
     * @param key 上下文中的键名。
     * @return 键名对应的值，如果键名不存在则返回null。
     */
    fun getKey(key: String?): Any? {
        val context: Map<String?, Any?> = get()
        return context[key]
    }

    /**
     * 根据键名获取上下文中的字符串值。
     *
     * @param key 上下文中的键名。
     * @return 键名对应的字符串值，如果键名不存在或值为null则返回null。
     */
    fun getString(key: String?): String? {
        val actual = getKey(key)
        return actual?.toString()
    }

    /**
     * 在当前线程的上下文中放入键值对。
     *
     * @param key   上下文中的键名。
     * @param val   上下文中的值。
     */
    fun put(key: String?, `val`: Any?) {
        val context = get()
        context[key] = `val`
        putContext(context)
    }

    /**
     * 更新当前线程的上下文信息。
     *
     * @param context 需要设置的新上下文，一个键值对的集合。
     */
    fun putContext(context: MutableMap<String?, Any?>) {
        val threadContext = CONTEXT.get() ?: mutableMapOf()
        threadContext.putAll(context)
        CONTEXT.set(threadContext)
    }

    /**
     * 清空当前线程的上下文信息。
     */
    fun clean() {
        CONTEXT.remove()
    }
}
