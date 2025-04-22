package faustofan.app.framework.base.context

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class ApplicationContextHolderTest {

    private lateinit var mockApplicationContext: ApplicationContext

    @BeforeEach
    fun setup() {
        mockApplicationContext = mock(ApplicationContext::class.java)
        ApplicationContextHolder.setApplicationContext(mockApplicationContext)
    }

    @Test
    fun `test setApplicationContext`() {
        val context = AnnotationConfigApplicationContext()
        ApplicationContextHolder.setApplicationContext(context)
        assertEquals(context, ApplicationContextHolder.getInstance())
    }

    @Test
    fun `test getBean by class`() {
        val testBean = TestBean()
        `when`(mockApplicationContext.getBean(TestBean::class.java)).thenReturn(testBean)

        val result = ApplicationContextHolder.getBean(clazz = TestBean::class.java)
        assertNotNull(result)
        assertEquals(testBean, result)
    }

    @Test
    fun `test getBean by name`() {
        val testBean = TestBean()
        `when`(mockApplicationContext.getBean("testBean")).thenReturn(testBean)

        val result = ApplicationContextHolder.getBean<TestBean>(name = "testBean")
        assertNotNull(result)
        assertEquals(testBean, result)
    }

    @Test
    fun `test getBean by name and class`() {
        val testBean = TestBean()
        `when`(mockApplicationContext.getBean("testBean", TestBean::class.java)).thenReturn(testBean)

        val result = ApplicationContextHolder.getBean(name = "testBean", clazz = TestBean::class.java)
        assertNotNull(result)
        assertEquals(testBean, result)
    }

    @Test
    fun `test getBean throws exception when no parameters provided`() {
        assertThrows<IllegalArgumentException> {
            ApplicationContextHolder.getBean<Nothing>()
        }
    }

    @Test
    fun `test getBeansOfType`() {
        val testBeans = mapOf("bean1" to TestBean(), "bean2" to TestBean())
        `when`(mockApplicationContext.getBeansOfType(TestBean::class.java)).thenReturn(testBeans)

        val result = ApplicationContextHolder.getBeansOfType(TestBean::class.java)
        assertEquals(testBeans, result)
    }

    @Test
    fun `test findAnnotationOnBean`() {
        val testAnnotation = TestAnnotation()
        `when`(mockApplicationContext.findAnnotationOnBean("testBean", TestAnnotation::class.java))
            .thenReturn(testAnnotation)

        val result = ApplicationContextHolder.findAnnotationOnBean("testBean", TestAnnotation::class.java)
        assertNotNull(result)
        assertEquals(testAnnotation, result)
    }

    @Test
    fun `test getInstance`() {
        assertEquals(mockApplicationContext, ApplicationContextHolder.getInstance())
    }
}

class TestBean

@Retention(AnnotationRetention.RUNTIME)
annotation class TestAnnotation 