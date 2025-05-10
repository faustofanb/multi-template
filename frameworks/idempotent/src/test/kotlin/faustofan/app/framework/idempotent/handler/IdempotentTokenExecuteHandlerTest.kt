package faustofan.app.framework.idempotent.handler

import faustofan.app.framework.cache.DistributedCache
import faustofan.app.framework.idempotent.annotation.Idempotent
import faustofan.app.framework.idempotent.config.IdempotentProperties
import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.ClientException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

class IdempotentTokenExecuteHandlerTest {

	@MockK
	private lateinit var distributedCache: DistributedCache

	@MockK
	private lateinit var idempotentProperties: IdempotentProperties

	@InjectMockKs
	private lateinit var handler: IdempotentTokenExecuteHandler

	@MockK
	private lateinit var joinPoint: ProceedingJoinPoint // For buildWrapper, though simple

	@MockK
	private lateinit var servletRequestAttributes: ServletRequestAttributes

	@MockK
	private lateinit var httpServletRequest: HttpServletRequest

	@MockK
	private lateinit var idempotentAnnotation: Idempotent


	private val tokenSlot = slot<String>()
	private val valueSlot = slot<String>()
	private val timeoutSlot = slot<Long>()

	@BeforeEach
	fun setUp() {
		MockKAnnotations.init(this)
		mockkStatic(RequestContextHolder::class)
		// mockkStatic(UUID::class)

		every { RequestContextHolder.currentRequestAttributes() } returns servletRequestAttributes
		every { servletRequestAttributes.request } returns httpServletRequest
	}

	@AfterEach
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `buildWrapper should return new IdempotentParamWrapper`() {
		val wrapper = handler.buildWrapper(joinPoint)
		assertNotNull(wrapper)
		// No specific properties to check as it's a plain new object
	}

	@Test
	fun `createToken should use default prefix and timeout if properties are null or blank`() {
		val mockUuidInstance = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
		mockkStatic(UUID::class) // Mock UUID specifically for this test or in setUp if always needed for randomUUID
		every { UUID.randomUUID() } returns mockUuidInstance
		// ... rest of the test
		every { idempotentProperties.prefix } returns null // or ""
		every { idempotentProperties.timeout } returns null
		every { distributedCache.put(capture(tokenSlot), capture(valueSlot), capture(timeoutSlot)) } just Runs

		val createdToken = handler.createToken()

		val expectedToken = IdempotentTokenExecuteHandler.TOKEN_PREFIX_KEY + mockUuidInstance.toString()
		assertEquals(expectedToken, createdToken)
		assertEquals(expectedToken, tokenSlot.captured)
		assertEquals("", valueSlot.captured)
		assertEquals(IdempotentTokenExecuteHandler.TOKEN_EXPIRED_TIME, timeoutSlot.captured)
		unmockkStatic(UUID::class) // Clean up if mocked per test
	}

	@Test
	fun `createToken should use custom prefix from properties`() {
		val mockUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
		mockkStatic(UUID::class) // Mock UUID specifically for this test or in setUp if always needed for randomUUID
		every { UUID.randomUUID() } returns mockUuid
		val customPrefix = "custom:prefix:"
		every { UUID.randomUUID() } returns mockUuid
		every { idempotentProperties.prefix } returns customPrefix
		every { idempotentProperties.timeout } returns null // Use default timeout
		every { distributedCache.put(capture(tokenSlot), any(), any()) } just Runs

		val createdToken = handler.createToken()

		val expectedToken = customPrefix + mockUuid.toString()
		assertEquals(expectedToken, createdToken)
		assertEquals(expectedToken, tokenSlot.captured)
	}

	@Test
	fun `createToken should use custom timeout from properties`() {
		val mockUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174002")
		mockkStatic(UUID::class) // Mock UUID specifically for this test or in setUp if always needed for randomUUID
		every { UUID.randomUUID() } returns mockUuid
		val customTimeout = 12000L
		every { UUID.randomUUID() } returns mockUuid
		every { idempotentProperties.prefix } returns null // Use default prefix
		every { idempotentProperties.timeout } returns customTimeout
		every { distributedCache.put(any(), any(), capture(timeoutSlot)) } just Runs

		handler.createToken()

		assertEquals(customTimeout, timeoutSlot.captured)
	}

	@Test
	fun `handler should process successfully if token from header is valid and deleted`() {
		val tokenValue = "valid-header-token"
		val wrapper = IdempotentParamWrapper() // Not used much in this handler's logic directly

		every { httpServletRequest.getHeader(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns tokenValue
		every { httpServletRequest.getParameter(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns null // Ensure it's from header
		every { distributedCache.delete(tokenValue) } returns 1L // delete returns number of keys deleted

		assertDoesNotThrow {
			handler.handler(wrapper)
		}
		verify { distributedCache.delete(tokenValue) }
	}

	@Test
	fun `handler should process successfully if token from parameter is valid and deleted`() {
		val tokenValue = "valid-parameter-token"
		val wrapper = IdempotentParamWrapper()

		every { httpServletRequest.getHeader(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns null
		every { httpServletRequest.getParameter(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns tokenValue
		every { distributedCache.delete(tokenValue) } returns 1L

		assertDoesNotThrow {
			handler.handler(wrapper)
		}
		verify { distributedCache.delete(tokenValue) }
	}

	@Test
	fun `handler should throw ClientException if token is not found`() {
		val wrapper = IdempotentParamWrapper()
		every { httpServletRequest.getHeader(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns null
		every { httpServletRequest.getParameter(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns null

		val exception = assertThrows<ClientException> {
			handler.handler(wrapper)
		}

		assertEquals(ErrorCode.UNAUTHORIZED.code, exception.code)
		assertEquals("Token状态异常, 请先获取Token", exception.message)
		verify(exactly = 0) { distributedCache.delete(any()) }
	}

	@Test
	fun `handler should throw ClientException with default message if token deletion fails`() {
		val tokenValue = "fail-delete-token"
		val wrapper = IdempotentParamWrapper().apply {
			this.idempotent = idempotentAnnotation // Attach the annotation mock
		}
		every { idempotentAnnotation.message } returns "" // Blank custom message

		every { httpServletRequest.getHeader(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns tokenValue
		every { distributedCache.delete(tokenValue) } returns 0L // Deletion failed

		val exception = assertThrows<ClientException> {
			handler.handler(wrapper)
		}

		assertEquals(ErrorCode.UNAUTHORIZED.code, exception.code)
		assertEquals("幂等Token删除失败, 请先获取Token", exception.message)
	}

	@Test
	fun `handler should throw ClientException with custom message if token deletion fails`() {
		val tokenValue = "fail-delete-custom-msg-token"
		val customMessage = "Custom error: Token already processed."
		val wrapper = IdempotentParamWrapper().apply {
			this.idempotent = idempotentAnnotation
		}
		every { idempotentAnnotation.message } returns customMessage

		every { httpServletRequest.getHeader(IdempotentTokenExecuteHandler.TOKEN_KEY) } returns tokenValue
		every { distributedCache.delete(tokenValue) } returns 0L // Deletion failed

		val exception = assertThrows<ClientException> {
			handler.handler(wrapper)
		}

		assertEquals(ErrorCode.UNAUTHORIZED.code, exception.code)
		assertEquals(customMessage, exception.message)
	}

	@Test
	fun `postProcessing should do nothing`() {
		// postProcessing in AbstractIdempotentExecuteHandler is empty by default
		// This test is more for completeness or if it were overridden
		assertDoesNotThrow {
			handler.postProcessing()
		}
		// No specific verification needed unless it had behavior
	}

	@Test
	fun `exceptionProcessing should do nothing`() {
		// exceptionProcessing in AbstractIdempotentExecuteHandler is empty by default
		assertDoesNotThrow {
			handler.exceptionProcessing()
		}
		// No specific verification needed unless it had behavior
	}
}