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