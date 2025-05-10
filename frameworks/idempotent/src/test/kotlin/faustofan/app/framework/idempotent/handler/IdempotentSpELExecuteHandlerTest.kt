package faustofan.app.framework.idempotent.handler

import faustofan.app.framework.idempotent.annotation.Idempotent
import faustofan.app.framework.idempotent.aspect.IdempotentAspect
import faustofan.app.framework.idempotent.core.IdempotentContext
import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import faustofan.app.framework.idempotent.util.SpELUtil
import faustofan.app.framework.web.enums.ErrorCode
import faustofan.app.framework.web.exception.ClientException
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
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.lang.reflect.Method

class IdempotentSpELByRestAPIExecuteHandlerTest {

	@MockK
	private lateinit var redissonClient: RedissonClient

	@InjectMockKs
	private lateinit var handler: IdempotentSpELByRestAPIExecuteHandler

	@MockK
	private lateinit var joinPoint: ProceedingJoinPoint

	@MockK
	private lateinit var methodSignature: MethodSignature

	@MockK
	private lateinit var method: Method

	@MockK
	private lateinit var idempotentAnnotation: Idempotent

	@MockK
	private lateinit var rLock: RLock

	private val lockSlot = slot<RLock>()

	@BeforeEach
	fun setUp() {
		MockKAnnotations.init(this)
		mockkObject(IdempotentAspect)
		mockkObject(SpELUtil)
		mockkObject(IdempotentContext)

		every { joinPoint.signature } returns methodSignature
		every { methodSignature.method } returns method
		every { IdempotentAspect.getIdempotent(joinPoint) } returns idempotentAnnotation
	}

	@AfterEach
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `buildWrapper should parse key and create wrapper`() {
		val spELKey = "testSpELKey"
		val parsedKey = "parsedSpELKey"
		val args: Array<Any?> = arrayOf("arg1", "arg2")

		every { idempotentAnnotation.key } returns spELKey
		every { joinPoint.args } returns args
		every { SpELUtil.parseKey(spELKey, method, args) } returns parsedKey

		val wrapper = handler.buildWrapper(joinPoint)

		assertEquals(parsedKey, wrapper.lockKey)
		assertSame(joinPoint, wrapper.joinPoint)
		verify { SpELUtil.parseKey(spELKey, method, args) }
	}

	@Test
	fun `handler should acquire lock and put it into context when tryLock is successful`() {
		val lockKey = "parsedSpELKey"
		val uniqueKeyPrefix = "prefix:"
		val fullLockKey = uniqueKeyPrefix + lockKey

		val wrapper = IdempotentParamWrapper().apply {
			this.lockKey = lockKey
			this.idempotent = idempotentAnnotation
		}
		every { idempotentAnnotation.uniqueKeyPrefix } returns uniqueKeyPrefix
		every { redissonClient.getLock(fullLockKey) } returns rLock
		every { rLock.tryLock() } returns true
		every { IdempotentContext.put(eq("lock:spEL:restAPI"), capture(lockSlot)) } just Runs

		handler.handler(wrapper)

		verify { redissonClient.getLock(fullLockKey) }
		verify { rLock.tryLock() }
		verify { IdempotentContext.put("lock:spEL:restAPI", rLock) }
		assertSame(rLock, lockSlot.captured)
	}

	@Test
	fun `handler should throw ClientException when tryLock fails`() {
		val lockKey = "parsedSpELKey"
		val uniqueKeyPrefix = "prefix:"
		val fullLockKey = uniqueKeyPrefix + lockKey

		val wrapper = IdempotentParamWrapper().apply {
			this.lockKey = lockKey
			this.idempotent = idempotentAnnotation
		}
		every { idempotentAnnotation.uniqueKeyPrefix } returns uniqueKeyPrefix
		every { redissonClient.getLock(fullLockKey) } returns rLock
		every { rLock.tryLock() } returns false

		val exception = assertThrows<ClientException> {
			handler.handler(wrapper)
		}

		assertEquals(ErrorCode.TOO_MANY_REQUESTS.code, exception.code)
		assertEquals("缓存锁获取异常, 请稍后再试...", exception.message)
		verify(exactly = 0) { IdempotentContext.put(any(), any()) }
	}

	@Test
	fun `postProcessing should unlock if lock exists in context`() {
		every { IdempotentContext.getKey("lock:spEL:restAPI") } returns rLock
		every { rLock.unlock() } just Runs // Ensure unlock doesn't throw

		handler.postProcessing()

		verify { IdempotentContext.getKey("lock:spEL:restAPI") }
		verify { rLock.unlock() }
	}

	@Test
	fun `postProcessing should do nothing if lock does not exist in context`() {
		every { IdempotentContext.getKey("lock:spEL:restAPI") } returns null

		handler.postProcessing()

		verify { IdempotentContext.getKey("lock:spEL:restAPI") }
		verify(exactly = 0) { rLock.unlock() }
	}

	@Test
	fun `postProcessing should handle if lock from context is not RLock type`() {
		every { IdempotentContext.getKey("lock:spEL:restAPI") } returns Any() // Not an RLock

		assertDoesNotThrow {
			handler.postProcessing()
		}
		verify(exactly = 0) { rLock.unlock() }
	}

	@Test
	fun `postProcessing should handle null lock from context gracefully`() {
		every { IdempotentContext.getKey("lock:spEL:restAPI") } returns null

		assertDoesNotThrow {
			handler.postProcessing()
		}
		verify(exactly = 0) { rLock.unlock() } // Verify unlock is not called on the mock rLock
	}


	@Test
	fun `exceptionProcessing should call postProcessing`() {
		val spiedHandler = spyk(handler)
		// Mock the behavior of postProcessing within the spy if it has side effects
		// or ensure IdempotentContext.getKey returns null so postProcessing does little
		every { IdempotentContext.getKey("lock:spEL:restAPI") } returns null


		spiedHandler.exceptionProcessing()

		verify { spiedHandler.postProcessing() }
	}
}

