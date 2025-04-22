package faustofan.app.framework.base.context

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class SingletonTest {

    @Test
    fun `test put and get with key`() {
        val testObject = TestObject()
        Singleton.put("testKey", testObject)
        
        val result = Singleton.get<TestObject>("testKey")
        assertEquals(testObject, result)
    }

    @Test
    fun `test put and get without key`() {
        val testObject = TestObject()
        Singleton.put(value = testObject)
        
        val result = Singleton.get<TestObject>(TestObject::class.java.name)
        assertEquals(testObject, result)
    }

    @Test
    fun `test get with non-existent key`() {
        val result = Singleton.get<TestObject>("nonExistentKey")
        assertNull(result)
    }

    @Test
    fun `test get with supplier`() {
        val testObject = TestObject()
        val result = Singleton.get("newKey") { testObject }
        assertEquals(testObject, result)
    }

    @Test
    fun `test get with null supplier`() {
        val result = Singleton.get<TestObject>("nullKey") { null }
        assertNull(result)
    }

    @Test
    fun `test put overwrites existing value`() {
        val firstObject = TestObject()
        val secondObject = TestObject()
        
        Singleton.put("sameKey", firstObject)
        Singleton.put("sameKey", secondObject)
        
        val result = Singleton.get<TestObject>("sameKey")
        assertEquals(secondObject, result)
    }
}

class TestObject 