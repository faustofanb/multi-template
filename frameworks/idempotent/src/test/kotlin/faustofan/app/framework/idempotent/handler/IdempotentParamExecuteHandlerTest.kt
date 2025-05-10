package faustofan.app.framework.idempotent.handler

import cn.hutool.crypto.digest.DigestUtil
import com.alibaba.fastjson2.JSON
import faustofan.app.framework.idempotent.core.IdempotentContext
import faustofan.app.framework.idempotent.core.IdempotentParamWrapper
import faustofan.app.framework.web.context.UserContext
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
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class IdempotentParamExecuteHandlerTest {

	@MockK
	private lateinit var redissonClient: RedissonClient

	@InjectMockKs
	private lateinit var handler: IdempotentParamExecuteHandler

	@MockK
	private lateinit var joinPoint: ProceedingJoinPoint

	@MockK
	private lateinit var servletRequestAttributes: ServletRequestAttributes

	@MockK
	private lateinit var httpServletRequest: HttpServletRequest

	@MockK
	private lateinit var rLock: RLock

	// 用于捕获 IdempotentContext.put 的参数
	private val lockSlot = slot<RLock>()

	@BeforeEach
	fun setUp() {
		MockKAnnotations.init(this)
		mockkObject(IdempotentContext)
		mockkStatic(RequestContextHolder::class)
		mockkObject(UserContext) // 假设 UserContext 是一个 object
		mockkStatic(DigestUtil::class)
		mockkStatic(JSON::class)

		every { RequestContextHolder.getRequestAttributes() } returns servletRequestAttributes
		every { servletRequestAttributes.request } returns httpServletRequest
	}

	@AfterEach
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `buildWrapper should construct correct lockKey and set joinPoint`() {
		val servletPath = "/test/path"
		val userId = "user123"
		val args = arrayOf("arg1", 123)
		val argsJsonBytes = byteArrayOf(1, 2, 3)
		val md5Hex = "md5hash"

		every { httpServletRequest.servletPath } returns servletPath
		every { UserContext.getUserId() } returns userId
		every { joinPoint.args } returns args
		every { JSON.toJSONBytes(args) } returns argsJsonBytes
		every { DigestUtil.md5Hex(argsJsonBytes) } returns md5Hex

		val wrapper = handler.buildWrapper(joinPoint)

		val expectedLockKey = "idempotent:path:$servletPath:currentUserId:$userId:md5:$md5Hex"
		assertEquals(expectedLockKey, wrapper.lockKey)
		assertSame(joinPoint, wrapper.joinPoint)
	}

	@Test
	fun `buildWrapper should throw ClientException if userId is null or empty`() {
		every { httpServletRequest.servletPath } returns "/test/path"
		every { UserContext.getUserId() } returns null // or ""

		val exception = assertThrows<ClientException> {
			handler.buildWrapper(joinPoint)
		}
		assertEquals(ErrorCode.USER_LOGIN_ERROR.code, exception.code)
		assertEquals("用户ID获取失败, 请登录", exception.message)
	}


	@Test
	fun `handler should acquire lock and put it into context when tryLock is successful`() {
		val lockKey = "testLockKey"
		val wrapper = IdempotentParamWrapper().apply { this.lockKey = lockKey }

		every { redissonClient.getLock(lockKey) } returns rLock
		every { rLock.tryLock() } returns true
		every { IdempotentContext.put(eq("lock:param:restAPI"), capture(lockSlot)) } just Runs

		handler.handler(wrapper)

		verify { redissonClient.getLock(lockKey) }
		verify { rLock.tryLock() }
		verify { IdempotentContext.put("lock:param:restAPI", rLock) }
		assertSame(rLock, lockSlot.captured)
	}

	@Test
	fun `handler should throw ClientException when tryLock fails`() {
		val lockKey = "testLockKey"
		val wrapper = IdempotentParamWrapper().apply { this.lockKey = lockKey }

		every { redissonClient.getLock(lockKey) } returns rLock
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
		every { IdempotentContext.getKey("lock:param:restAPI") } returns rLock
		every { rLock.unlock() } just Runs

		handler.postProcessing()

		verify { IdempotentContext.getKey("lock:param:restAPI") }
		verify { rLock.unlock() }
	}

	@Test
	fun `postProcessing should do nothing if lock does not exist in context`() {
		every { IdempotentContext.getKey("lock:param:restAPI") } returns null

		handler.postProcessing()

		verify { IdempotentContext.getKey("lock:param:restAPI") }
		verify(exactly = 0) { rLock.unlock() } // rLock is a mock, so this verifies no unlock on it
	}

	@Test
	fun `postProcessing should handle if lock from context is not RLock type (though unlikely)`() {
		every { IdempotentContext.getKey("lock:param:restAPI") } returns Any() // Not an RLock

		// Expect no exception and no unlock call on a generic RLock mock
		assertDoesNotThrow {
			handler.postProcessing()
		}
		verify(exactly = 0) { rLock.unlock() }
	}


	@Test
	fun `exceptionProcessing should call postProcessing`() {
		// Spy the handler to verify a call to its own method
		val spiedHandler = spyk(
			handler,
			recordPrivateCalls = true
		) // recordPrivateCalls might not be needed if postProcessing is public

		// Mock postProcessing to avoid its actual execution if needed, or let it run
		// For this test, we just want to verify it's called.
		// If postProcessing has side effects we don't want in this specific test, mock it.
		// Here, we'll let it run as its side effects are tested separately.
		// Or, more simply, just verify the call on the original spied handler.
		every { IdempotentContext.getKey("lock:param:restAPI") } returns null // Ensure postProcessing runs simply

		spiedHandler.exceptionProcessing()

		verify { spiedHandler.postProcessing() }
	}
}