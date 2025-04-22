package faustofan.app.framework.base.config

import faustofan.app.framework.base.context.ApplicationContextHolder
import faustofan.app.framework.base.init.ApplicationContentPostPostProcessor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class ApplicationBaseAutoConfigurationTest {

    @Test
    fun `test applicationContextHolder bean creation`() {
        val configuration = ApplicationBaseAutoConfiguration()
        val holder = configuration.applicationContextHolder()
        
        Assertions.assertNotNull(holder)
        Assertions.assertEquals(ApplicationContextHolder, holder)
    }

    @Test
    fun `test applicationContentPostProcessor bean creation`() {
        val configuration = ApplicationBaseAutoConfiguration()
        val applicationContext = AnnotationConfigApplicationContext()
        val processor = configuration.applicationContentPostProcessor(applicationContext)
        
        Assertions.assertNotNull(processor)
        Assertions.assertTrue(processor is ApplicationContentPostPostProcessor)
    }

    @Test
    fun `test auto configuration loads successfully`() {
        val applicationContext = AnnotationConfigApplicationContext(ApplicationBaseAutoConfiguration::class.java)
        
        Assertions.assertNotNull(applicationContext.getBean(ApplicationContextHolder::class.java))
        Assertions.assertNotNull(applicationContext.getBean(ApplicationContentPostPostProcessor::class.java))
    }
} 