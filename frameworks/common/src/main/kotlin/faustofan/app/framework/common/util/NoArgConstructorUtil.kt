package faustofan.app.framework.common.util

/**
 * 工具类，用于通过无参构造函数创建类的实例。
 */
object NoArgConstructorUtil {
    /**
     * 通过无参构造函数创建指定类型的实例。
     *
     * @param T 要创建的实例的类型。
     * @return 返回通过无参构造函数创建的实例。
     */
    inline fun <reified T> createInstance(): T {
        return T::class.java.getDeclaredConstructor().newInstance()
    }
}