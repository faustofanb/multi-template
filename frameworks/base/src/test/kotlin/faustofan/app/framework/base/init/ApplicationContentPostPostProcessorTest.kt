package faustofan.app.framework.base.init

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.ArgumentMatchers.any
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext

class ApplicationContentPostPostProcessorTest {

    @Test
    fun `test onApplicationEvent publishes ApplicationInitializingEvent only once`() {
        // 创建mock对象
        val applicationContext = mock(ApplicationContext::class.java)
        val processor = ApplicationContentPostPostProcessor(applicationContext)
        val event = mock(ApplicationReadyEvent::class.java)

        // 第一次调用
        processor.onApplicationEvent(event)
        verify(applicationContext).publishEvent(any(ApplicationInitializingEvent::class.java))

        // 第二次调用不应该发布事件
        processor.onApplicationEvent(event)
        verify(applicationContext).publishEvent(any(ApplicationInitializingEvent::class.java))
    }

    @Test
    fun `test executeOnlyOnce flag is atomic`() {
        val applicationContext = mock(ApplicationContext::class.java)
        val processor = ApplicationContentPostPostProcessor(applicationContext)
        val event = mock(ApplicationReadyEvent::class.java)

        // 创建多个线程同时调用onApplicationEvent
        val threads = List(10) {
            Thread {
                processor.onApplicationEvent(event)
            }
        }

        // 启动所有线程
        threads.forEach { it.start() }

        // 等待所有线程完成
        threads.forEach { it.join() }

        // 验证事件只被发布了一次
        verify(applicationContext).publishEvent(any(ApplicationInitializingEvent::class.java))
    }
} 