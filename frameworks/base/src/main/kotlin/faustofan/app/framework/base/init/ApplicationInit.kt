package faustofan.app.framework.base.init

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 表示应用程序初始化事件的类。
 *
 * 此类继承自ApplicationEvent，用于在应用程序初始化过程中触发事件。
 * 初始化事件可以在应用程序准备好后执行一次，以确保初始化逻辑只执行一次。
 *
 * @param source 事件的来源对象。
 */
class ApplicationInitializingEvent(source: Any) : ApplicationEvent(source)

/**
 * 应用程序内容后处理程序类。
 *
 * 此类用于在Spring应用程序上下文准备好后执行特定的初始化逻辑。
 * 它实现了ApplicationListener接口，以监听ApplicationReadyEvent事件。
 * 当接收到ApplicationReadyEvent事件时，它会发布ApplicationInitializingEvent事件，确保初始化逻辑在应用程序准备好后执行。
 *
 * @param applicationContext 应用程序上下文，用于发布事件和访问应用程序上下文相关信息。
 */
@Component
class ApplicationContentPostPostProcessor(
	private val applicationContext: ApplicationContext,
) : ApplicationListener<ApplicationReadyEvent> {

	/**
	 * 用于确保初始化逻辑只执行一次的标志。
	 * 使用AtomicBoolean确保线程安全，因为在多线程环境下，初始化逻辑的执行顺序可能会不确定。
	 */
	private val executeOnlyOnce = AtomicBoolean(false)

	/**
	 * 当监听到ApplicationReadyEvent事件时触发的方法。
	 *
	 * 此方法用于检查初始化逻辑是否已经执行过。如果没有执行过，则发布ApplicationInitializingEvent事件，
	 * 以触发初始化逻辑。通过这种方式，可以确保初始化逻辑在应用程序准备好后且只执行一次。
	 *
	 * @param event 触发的事件对象，此处为ApplicationReadyEvent。
	 */
	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		if (executeOnlyOnce.compareAndSet(false, true)) {
			applicationContext.publishEvent(ApplicationInitializingEvent(this))
		}
	}
}
