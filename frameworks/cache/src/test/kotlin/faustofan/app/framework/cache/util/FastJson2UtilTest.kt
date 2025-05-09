package faustofan.app.framework.cache.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import java.lang.reflect.ParameterizedType

class FastJson2UtilTest {

    @Test
    fun `test buildType with simple class`() {
        val type = FastJson2Util.buildType(String::class.java)
        assertNotNull(type)
        assertEquals(String::class.java, type!!.javaClass)
    }

    @Test
    fun `test buildType with generic class`() {
        val type = FastJson2Util.buildType(List::class.java, String::class.java)
        assertNotNull(type)
        assert(type is ParameterizedType)
        assertEquals(List::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, (type as ParameterizedType).actualTypeArguments[0])
    }

    @Test
    fun `test buildType with complex generic class`() {
        val type = FastJson2Util.buildType(Map::class.java, String::class.java, List::class.java)
        assertNotNull(type)
        assert(type is ParameterizedType)
        assertEquals(Map::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, (type as ParameterizedType).actualTypeArguments[0])
        assertEquals(List::class.java, (type as ParameterizedType).actualTypeArguments[1])
    }

    @Test
    fun `test buildType with nested generic class`() {
        val innerType = FastJson2Util.buildType(List::class.java, Int::class.java)
        val type = FastJson2Util.buildType(Map::class.java, String::class.java, innerType!!)
        
        assertNotNull(type)
        assert(type is ParameterizedType)
        assertEquals(Map::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, (type as ParameterizedType).actualTypeArguments[0])
        
        val nestedType = (type as ParameterizedType).actualTypeArguments[1]
        assert(nestedType is ParameterizedType)
        assertEquals(List::class.java, (nestedType as ParameterizedType).rawType)
        assertEquals(Int::class.java, (nestedType as ParameterizedType).actualTypeArguments[0])
    }

    @Test
    fun `test buildType with empty types`() {
        val type = FastJson2Util.buildType()
        assertNull(type)
    }

    @Test
    fun `test buildType with custom class`() {
        val type = FastJson2Util.buildType(TestObject::class.java)
        assertNotNull(type)
        assertEquals(TestObject::class.java, type!!.javaClass)
    }
}

class TestObject(val value: String) 
fun buildType(vararg types: Type): Type? {
	if (types.isEmpty()) {
		return null
	}
	
	if (types.size == 1) {
		// For single type, return the type directly
		return types[0]
	}
	
	// For generic types (e.g., List<String>)
	if (types.size == 2) {
		return ParameterizedTypeImpl(
			arrayOf(types[1]),  // Actual type argument
			null,
			types[0]  // Raw type
		)
	}
	
	// For complex generic types (e.g., Map<String, List>)
	if (types.size == 3) {
		return ParameterizedTypeImpl(
			arrayOf(types[1], types[2]),  // Multiple type arguments
			null,
			types[0]  // Raw type
		)
	}
	
	// For more complex nested generic types, build it according to test expectations
	// by handling each level of nesting appropriately
	val actualTypeArguments = mutableListOf<Type>()
	for (i in 1 until types.size) {
		actualTypeArguments.add(types[i])
	}
	
	return ParameterizedTypeImpl(
		actualTypeArguments.toTypedArray(),
		null,
		types[0]
	)
}