package faustofan.app.framework.base.context

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * 应用程序上下文持有者对象，实现了ApplicationContextAware接口，以便于在整个应用中访问Spring应用程序上下文。
 */
@Component
object ApplicationContextHolder : ApplicationContextAware {

	/** 存储ApplicationContext实例的变量，用于后续的bean获取和其他操作。 */
	private lateinit var CONTEXT: ApplicationContext

	/**
	 * 设置应用程序上下文。
	 * 当Spring容器初始化完成后，会调用此方法，将ApplicationContext注入到当前对象中。
	 *
	 * @param applicationContext Spring应用程序上下文实例。
	 */
	override fun setApplicationContext(applicationContext: ApplicationContext) {
		CONTEXT = applicationContext
	}

	/**
	 * 获取IOC容器中的bean。
	 *
	 * 如果提供了类型clazz，则根据类型获取bean。
	 * 如果提供了名称name，则根据名称获取bean。
	 * 如果同时提供了名称name和类型clazz，则根据名称和类型获取bean。
	 *
	 * @param name 可选参数，要获取的bean的名称。
	 * @param clazz 可选参数，要获取的bean的类型。
	 * @return 根据提供的参数获取的bean。
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T> getBean(name: String? = null, clazz: Class<out T>? = null): T? {
		return when {
			clazz != null && name == null -> CONTEXT.getBean(clazz)
			name != null && clazz == null -> CONTEXT.getBean(name) as? T
			name != null && clazz != null -> CONTEXT.getBean(name, clazz)
			else -> throw IllegalArgumentException("Either name or clazz must be provided.")
		}
	}

	/**
	 * 根据类型从ApplicationContext中获取所有bean的映射。
	 *
	 * @param clazz 需要获取的bean的类型。
	 * @return 所有匹配类型bean的映射，key为bean名称，value为bean实例。
	 */
	fun <T> getBeansOfType(clazz: Class<T>): Map<String, T> {
		return CONTEXT.getBeansOfType(clazz)
	}

	/**
	 * 在指定的bean上查找指定类型的注解。
	 *
	 * @param beanName bean的名称。
	 * @param annotationType 需要查找的注解类型。
	 * @return 符合条件的注解实例。
	 */
	fun <T : Annotation> findAnnotationOnBean(beanName: String, annotationType: Class<T>): T? {
		return CONTEXT.findAnnotationOnBean(beanName, annotationType)
	}

	/**
	 * 获取当前的ApplicationContext实例。
	 *
	 * @return ApplicationContext实例。
	 */
	fun getInstance(): ApplicationContext {
		return CONTEXT
	}
}
