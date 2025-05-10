package faustofan.app.framework.idempotent.aspect

import IdempotentExecuteHandler
import IdempotentExecuteHandlerFactory
import faustofan.app.framework.idempotent.annotation.Idempotent
import faustofan.app.framework.idempotent.core.IdempotentContext
import faustofan.app.framework.idempotent.enums.IdempotentSceneEnum
import faustofan.app.framework.idempotent.enums.IdempotentTypeEnum
import faustofan.app.framework.idempotent.exception.RepeatConsumptionException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class IdempotentAspectTest {

    @InjectMockKs
    private lateinit var idempotentAspect: IdempotentAspect

    @MockK
    private lateinit var joinPoint: ProceedingJoinPoint

    @MockK
    private lateinit var methodSignature: MethodSignature

    @MockK
    private lateinit var idempotentExecuteHandler: IdempotentExecuteHandler

    private lateinit var idempotent: Idempotent

    companion object {
        // 参数化测试的数据源
        @JvmStatic
        fun idempotentCombinations(): Stream<Array<Any>> {
            return Stream.of(
                arrayOf(IdempotentSceneEnum.MQ, IdempotentTypeEnum.SPEL, "annotatedMethodMQSPEL"),
                arrayOf(IdempotentSceneEnum.MQ, IdempotentTypeEnum.PARAM, "annotatedMethodMQPARAM"),
                arrayOf(IdempotentSceneEnum.MQ, IdempotentTypeEnum.TOKEN, "annotatedMethodMQTOKEN"),
                arrayOf(IdempotentSceneEnum.RESTAPI, IdempotentTypeEnum.SPEL, "annotatedMethodRESTSPEL"),
                arrayOf(IdempotentSceneEnum.RESTAPI, IdempotentTypeEnum.PARAM, "annotatedMethodRESTPARAM"),
                arrayOf(IdempotentSceneEnum.RESTAPI, IdempotentTypeEnum.TOKEN, "annotatedMethodRESTTOKEN")
            )
        }
    }

@BeforeEach
fun setUp() {
    // 初始化所有MockK注解
    MockKAnnotations.init(this)
    
    // 配置测试目标类
    val testClass = TestIdempotentClass::class.java
    val testMethod = testClass.getDeclaredMethod("testMethod")

    // 获取注解
    idempotent = testMethod.getAnnotation(Idempotent::class.java)!!

    // 配置连接点
    every { joinPoint.signature } returns methodSignature
    every { joinPoint.target } returns TestIdempotentClass()
    every { methodSignature.name } returns "testMethod"
    every { methodSignature.method } returns testMethod

    // 配置工厂方法返回模拟的处理器
    mockkObject(IdempotentExecuteHandlerFactory)
    every { 
        IdempotentExecuteHandlerFactory.getInstance(
            IdempotentSceneEnum.MQ,
            IdempotentTypeEnum.SPEL
        )
    } returns idempotentExecuteHandler

    // 添加这两行 - 配置处理器的所有方法行为
    every { idempotentExecuteHandler.execute(any(), any()) } just Runs
    every { idempotentExecuteHandler.postProcessing() } just Runs
    every { idempotentExecuteHandler.exceptionProcessing() } just Runs  // 添加这行

    // 模拟IdempotentContext静态方法
    mockkObject(IdempotentContext)
    every { IdempotentContext.clean() } just Runs
}

    @AfterEach
    fun tearDown() {
        // 移除所有模拟
        unmockkAll()
    }

    @Test
    fun `test normal execution flow`() {
        // 配置预期行为
        val expectedResult = "success"
        every { joinPoint.proceed() } returns expectedResult
    
        // 执行测试
        val result = idempotentAspect.idempotentHandler(joinPoint)
    
        // 验证结果
        assertEquals(expectedResult, result)
    
        // 验证方法调用顺序 - 使用 verifyOrder 而不是 verifySequence
        verifyOrder {
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            joinPoint.proceed()
            idempotentExecuteHandler.postProcessing()
            IdempotentContext.clean()
        }
    }

    @Test
    fun `test repeat consumption exception without error`() {
        // 使用含结果的构造函数，表示这是之前处理的结果
        every { joinPoint.proceed() } throws RepeatConsumptionException(false)

        // 执行测试 - 不再期望异常
        idempotentAspect.idempotentHandler(joinPoint)

        // 验证执行顺序
        verify {
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            joinPoint.proceed()
            IdempotentContext.clean()
        }

        // 验证没有调用后处理方法
        verify(exactly = 0) { idempotentExecuteHandler.postProcessing() }
    }

    @Test
    fun `test repeat consumption exception with error`() {
        // 配置预期行为
        val exception = RepeatConsumptionException(true)
        every { joinPoint.proceed() } throws exception
        
        // 验证抛出异常
        val thrownException = assertThrows<RepeatConsumptionException> {
            idempotentAspect.idempotentHandler(joinPoint)
        }
        
        // 验证是同一个异常实例
        assertSame(exception, thrownException)
        assertTrue(thrownException.error)
        
        // 验证执行顺序
        verify { 
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            joinPoint.proceed()
            IdempotentContext.clean()
        }
        
        // 验证没有调用后处理方法
        verify(exactly = 0) { idempotentExecuteHandler.postProcessing() }
    }

    @Test
    fun `test other runtime exception`() {
        // 配置预期行为
        val exception = RuntimeException("test exception")
        every { joinPoint.proceed() } throws exception
        
        // 验证抛出异常
        val thrownException = assertThrows<RuntimeException> {
            idempotentAspect.idempotentHandler(joinPoint)
        }
        
        // 验证是同一个异常实例
        assertSame(exception, thrownException)
        
        // 验证执行顺序
        verify { 
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            joinPoint.proceed()
            idempotentExecuteHandler.exceptionProcessing()
            IdempotentContext.clean()
        }
        
        // 验证没有调用后处理方法
        verify(exactly = 0) { idempotentExecuteHandler.postProcessing() }
    }

    @Test
    fun `test checked exception`() {
        // 配置预期行为
        val exception = Exception("checked exception")
        every { joinPoint.proceed() } throws exception
        
        // 验证抛出异常
        val thrownException = assertThrows<Exception> {
            idempotentAspect.idempotentHandler(joinPoint)
        }
        
        // 验证是同一个异常实例
        assertSame(exception, thrownException)
        
        // 验证执行顺序
        verify { 
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            joinPoint.proceed()
            idempotentExecuteHandler.exceptionProcessing()
            IdempotentContext.clean()
        }
        
        // 验证没有调用后处理方法
        verify(exactly = 0) { idempotentExecuteHandler.postProcessing() }
    }

    @Test
    fun `test context cleanup on handler execution exception`() {
        // 模拟处理器执行时抛出异常
        val exception = RuntimeException("handler exception")
        every { idempotentExecuteHandler.execute(any(), any()) } throws exception
        
        // 验证抛出异常
        val thrownException = assertThrows<RuntimeException> {
            idempotentAspect.idempotentHandler(joinPoint)
        }
        
        // 验证是同一个异常实例
        assertSame(exception, thrownException)
        
        // 验证执行顺序 - 确保执行了异常处理
        verify { 
            idempotentExecuteHandler.execute(joinPoint, idempotent)
            idempotentExecuteHandler.exceptionProcessing()
            IdempotentContext.clean()
        }
        
        // 验证没有调用proceed方法和后处理方法
        verify(exactly = 0) { 
            joinPoint.proceed()
            idempotentExecuteHandler.postProcessing() 
        }
    }

    @Test
    fun `test getIdempotent method`() {
        // 正确获取Companion类中的方法
        val companionClass = IdempotentAspect::class.java.getDeclaredClasses()
            .first { it.simpleName == "Companion" }

        val staticMethod = companionClass.getDeclaredMethod("getIdempotent", ProceedingJoinPoint::class.java)
        staticMethod.isAccessible = true

        // 获取Companion实例 - 在kotlin中是单例对象
        val companionInstance = IdempotentAspect::class.java.getDeclaredField("Companion")
            .apply { isAccessible = true }
            .get(null)

        // 调用方法 - 第一个参数是companion实例
        val result = staticMethod.invoke(companionInstance, joinPoint) as Idempotent

        assertEquals(IdempotentSceneEnum.MQ, result.scene)
        assertEquals(IdempotentTypeEnum.SPEL, result.type)
    }
    
    /**
     * 参数化测试：测试不同幂等场景和类型组合的处理
     */
    @ParameterizedTest
    @MethodSource("idempotentCombinations")
    fun `test different idempotent combinations`(
        scene: IdempotentSceneEnum,
        type: IdempotentTypeEnum,
        methodName: String
    ) {
        // 准备测试环境：新的连接点配置和处理器
        val testClass = MultiAnnotationTestClass::class.java
        val testMethod = testClass.getDeclaredMethod(methodName)
        val testIdempotent = testMethod.getAnnotation(Idempotent::class.java)!!
        
        // 清除之前的调用记录
        clearMocks(joinPoint, methodSignature)
        
        // 配置连接点
        every { joinPoint.signature } returns methodSignature
        every { joinPoint.target } returns MultiAnnotationTestClass()
        every { methodSignature.name } returns methodName
        every { methodSignature.method } returns testMethod
        
        // 为当前场景和类型组合配置专用的处理器
        val handler = mockk<IdempotentExecuteHandler>()
        every { handler.execute(any(), any()) } just Runs
        every { handler.postProcessing() } just Runs
        
        // 配置工厂方法返回新的处理器
        every { 
            IdempotentExecuteHandlerFactory.getInstance(scene, type)
        } returns handler
        
        // 执行测试
        val expectedResult = "success for $scene $type"
        every { joinPoint.proceed() } returns expectedResult
        
        val result = idempotentAspect.idempotentHandler(joinPoint)
        
        // 验证结果
        assertEquals(expectedResult, result)
        verify { 
            handler.execute(joinPoint, testIdempotent)
            handler.postProcessing()
            IdempotentContext.clean()
        }
    }

    // 测试用的带有幂等注解的类
    class TestIdempotentClass {
        @Idempotent(scene = IdempotentSceneEnum.MQ, type = IdempotentTypeEnum.SPEL)
        fun testMethod(): String {
            return "success"
        }
    }
    
    // 带有多个注解的测试类
    class MultiAnnotationTestClass {
        @Idempotent(scene = IdempotentSceneEnum.MQ, type = IdempotentTypeEnum.SPEL)
        fun annotatedMethodMQSPEL(): String {
            return "multiple annotations"
        }
        
        @Idempotent(scene = IdempotentSceneEnum.MQ, type = IdempotentTypeEnum.PARAM)
        fun annotatedMethodMQPARAM(): String {
            return "multiple annotations"
        }
        
        @Idempotent(scene = IdempotentSceneEnum.MQ, type = IdempotentTypeEnum.TOKEN)
        fun annotatedMethodMQTOKEN(): String {
            return "multiple annotations"
        }
        
        @Idempotent(scene = IdempotentSceneEnum.RESTAPI, type = IdempotentTypeEnum.SPEL)
        fun annotatedMethodRESTSPEL(): String {
            return "multiple annotations"
        }
        
        @Idempotent(scene = IdempotentSceneEnum.RESTAPI, type = IdempotentTypeEnum.PARAM)
        fun annotatedMethodRESTPARAM(): String {
            return "multiple annotations"
        }
        
        @Idempotent(scene = IdempotentSceneEnum.RESTAPI, type = IdempotentTypeEnum.TOKEN)
        fun annotatedMethodRESTTOKEN(): String {
            return "multiple annotations"
        }
    }
}