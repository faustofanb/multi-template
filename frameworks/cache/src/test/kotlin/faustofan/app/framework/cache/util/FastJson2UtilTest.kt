package faustofan.app.framework.cache.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.reflect.ParameterizedType
import kotlin.collections.Set

class FastJson2UtilTest {

    @Test
    fun `test buildType with simple class`() {
        val type = FastJson2Util.buildType(String::class.java)
        assertNotNull(type)
        assertEquals(String::class.java, type)
    }

    @Test
    fun `test buildType with primitive class`() {
        val type = FastJson2Util.buildType(Int::class.java)
        assertNotNull(type)
        assertEquals(Int::class.java, type)
    }

    @Test
    fun `test buildType with generic class`() {
        val type = FastJson2Util.buildType(List::class.java, String::class.java)
        assertNotNull(type)
        assertTrue(type is ParameterizedType)
        assertEquals(List::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, type.actualTypeArguments[0])
    }

    @Test
    fun `test buildType with complex generic class`() {
        val type = FastJson2Util.buildType(Map::class.java, String::class.java, List::class.java)
        assertNotNull(type)
        assertTrue(type is ParameterizedType)
        assertEquals(Map::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, type.actualTypeArguments[0])
        assertEquals(List::class.java, type.actualTypeArguments[1])
    }

    @Test
    fun `test buildType with nested generic class`() {
        val innerType = FastJson2Util.buildType(List::class.java, Int::class.java)
        val type = FastJson2Util.buildType(Map::class.java, String::class.java, innerType!!)
        
        assertNotNull(type)
        assertTrue(type is ParameterizedType)
        assertEquals(Map::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, type.actualTypeArguments[0])
        
        val nestedType = type.actualTypeArguments[1]
        assertTrue(nestedType is ParameterizedType)
        assertEquals(List::class.java, (nestedType as ParameterizedType).rawType)
        assertEquals(Int::class.java, nestedType.actualTypeArguments[0])
    }
    
    @Test
    fun `test buildType with deeply nested generic class`() {
        // 构建 Set<Integer>
        val innerType1 = FastJson2Util.buildType(Set::class.java, Int::class.java)
        
        // 构建 List<Set<Integer>>
        val innerType2 = FastJson2Util.buildType(List::class.java, innerType1!!)
        
        // 构建 Map<String, List<Set<Integer>>>
        val type = FastJson2Util.buildType(Map::class.java, String::class.java, innerType2!!)
        
        assertNotNull(type)
        assertTrue(type is ParameterizedType)
        assertEquals(Map::class.java, (type as ParameterizedType).rawType)
        assertEquals(String::class.java, type.actualTypeArguments[0])
        
        // 验证 List<Set<Integer>>
        val level1Type = type.actualTypeArguments[1]
        assertTrue(level1Type is ParameterizedType)
        assertEquals(List::class.java, (level1Type as ParameterizedType).rawType)
        
        // 验证 Set<Integer>
        val level2Type = level1Type.actualTypeArguments[0]
        assertTrue(level2Type is ParameterizedType)
        assertEquals(Set::class.java, (level2Type as ParameterizedType).rawType)
        assertEquals(Int::class.java, level2Type.actualTypeArguments[0])
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
        assertEquals(TestObject::class.java, type)
    }
    
    @Test
    fun `test buildType with array class`() {
        // 使用Java的数组类型表示
        val arrayClass = Array<String>(0) { "" }.javaClass
        val type = FastJson2Util.buildType(arrayClass)
        assertNotNull(type)
        assertEquals(arrayClass, type)
    }
    
    @Test
    fun `test buildType with generic array`() {
        // 先构建List<String>类型
        val innerType = FastJson2Util.buildType(List::class.java, String::class.java)
        
        // 使用Java的原始数组类型
        val arrayClass = Array<Any>(0) { Any() }.javaClass
        val type = FastJson2Util.buildType(arrayClass, innerType!!)
        
        assertNotNull(type)
        assertTrue(type is ParameterizedType)
        assertEquals(arrayClass, (type as ParameterizedType).rawType)
        
        val paramType = type.actualTypeArguments[0]
        assertTrue(paramType is ParameterizedType)
        assertEquals(List::class.java, (paramType as ParameterizedType).rawType)
        assertEquals(String::class.java, paramType.actualTypeArguments[0])
    }
}

class TestObject