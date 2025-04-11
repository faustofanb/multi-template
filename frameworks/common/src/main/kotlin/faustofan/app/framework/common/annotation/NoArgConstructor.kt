package faustofan.app.framework.common.annotation

/**
 * 该注解用于标记类，表示该类应生成一个无参构造函数。
 *
 * 该注解的目标为类（`AnnotationTarget.CLASS`），并且保留策略为运行时（`AnnotationRetention.RUNTIME`），
 * 这意味着该注解在编译后的字节码中仍然可用，并且可以通过反射在运行时获取。
 *
 * 使用该注解的类将在编译时自动生成一个无参构造函数，适用于需要无参构造函数的场景，如某些框架或库的依赖注入。
 * **注意: 该注解生成的无参构造器只能通过反射获取，不能直接在代码中调用.**
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoArgConstructor
