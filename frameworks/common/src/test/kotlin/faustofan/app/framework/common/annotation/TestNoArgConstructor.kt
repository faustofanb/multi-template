package faustofan.app.framework.common.annotation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestNoArgConstructor {

	@Test
	fun `test annotation no arg constructor`() {
		Assertions.assertDoesNotThrow(TestData::class.java.getDeclaredConstructor()::newInstance)
	}

}

@NoArgConstructor
data class TestData(
	val data: String
)