package faustofan.app.framework.idempotent.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

class IdempotentContextTest {

    @AfterEach
    fun cleanup() {
        // 每个测试后清理上下文，防止测试间互相影响
        IdempotentContext.clean()
    }

    @Test
    fun `test get returns empty map when no context exists`() {
        // 初始状态应该是空映射
        val context = IdempotentContext.get()
        
        assertNotNull(context)
        assertTrue(context.isEmpty())
    }

    @Test
    fun `test put and get basic functionality`() {
        // 测试基本的存取功能
        IdempotentContext.put("testKey", "testValue")
        
        val value = IdempotentContext.getKey("testKey")
        
        assertEquals("testValue", value)
    }

    @Test
    fun `test put overwrites existing value`() {
        // 测试重复键的值会被覆盖
        IdempotentContext.put("testKey", "initialValue")
        IdempotentContext.put("testKey", "updatedValue")
        
        val value = IdempotentContext.getKey("testKey")
        
        assertEquals("updatedValue", value)
    }

    @Test
    fun `test getString returns null for non-existent key`() {
        val value = IdempotentContext.getString("nonExistentKey")
        
        assertNull(value)
    }

    @Test
    fun `test getString converts values to string`() {
        // 测试各种类型的值被正确转换为字符串
        IdempotentContext.put("intKey", 123)
        IdempotentContext.put("boolKey", true)
        IdempotentContext.put("decimalKey", BigDecimal("123.45"))
        
        assertEquals("123", IdempotentContext.getString("intKey"))
        assertEquals("true", IdempotentContext.getString("boolKey"))
        assertEquals("123.45", IdempotentContext.getString("decimalKey"))
    }

    @Test
    fun `test putContext merges maps`() {
        // 先设置一些初始值
        IdempotentContext.put("key1", "value1")
        
        // 使用putContext合并新值
        val newContext = mutableMapOf<String?, Any?>(
            "key2" to "value2",
            "key3" to "value3"
        )
        IdempotentContext.putContext(newContext)
        
        // 验证所有值都存在
        assertEquals("value1", IdempotentContext.getKey("key1"))
        assertEquals("value2", IdempotentContext.getKey("key2"))
        assertEquals("value3", IdempotentContext.getKey("key3"))
    }

    @Test
    fun `test putContext overwrites existing values`() {
        // 设置初始值
        IdempotentContext.put("key1", "oldValue")
        
        // 使用putContext覆盖已有值
        val newContext = mutableMapOf<String?, Any?>("key1" to "newValue")
        IdempotentContext.putContext(newContext)
        
        assertEquals("newValue", IdempotentContext.getKey("key1"))
    }

    @Test
    fun `test clean removes all context data`() {
        // 添加一些数据
        IdempotentContext.put("key1", "value1")
        IdempotentContext.put("key2", "value2")
        
        // 确认数据已添加
        assertFalse(IdempotentContext.get().isEmpty())
        
        // 清理数据
        IdempotentContext.clean()
        
        // 验证已清空
        assertTrue(IdempotentContext.get().isEmpty())
    }

    @Test
    fun `test null keys and values are supported`() {
        // 测试空键
        IdempotentContext.put(null, "nullKeyValue")
        assertEquals("nullKeyValue", IdempotentContext.getKey(null))
        
        // 测试空值
        IdempotentContext.put("nullValueKey", null)
        assertNull(IdempotentContext.getKey("nullValueKey"))
    }

    @Test
    fun `test thread isolation`() {
        // 测试不同线程之间的上下文是隔离的
        val mainThreadDone = AtomicBoolean(false)
        val childThreadDone = CountDownLatch(1)
        
        // 在主线程中设置一个值
        IdempotentContext.put("threadKey", "mainThread")
        
        // 在子线程中检查并设置自己的值
        val thread = Thread {
            try {
                // 验证子线程无法看到主线程的值
                assertNull(IdempotentContext.getKey("threadKey"))
                
                // 在子线程设置同名键的不同值
                IdempotentContext.put("threadKey", "childThread")
                assertEquals("childThread", IdempotentContext.getKey("threadKey"))
                
                // 等待主线程完成检查
                while (!mainThreadDone.get()) {
                    Thread.sleep(10)
                }
                
                // 确认子线程的值没有被主线程修改
                assertEquals("childThread", IdempotentContext.getKey("threadKey"))
            } finally {
                // 通知主线程子线程已完成
                childThreadDone.countDown()
            }
        }
        thread.start()
        
        // 确认主线程的值没有被子线程修改
        assertEquals("mainThread", IdempotentContext.getKey("threadKey"))
        
        // 通知子线程，主线程检查完成
        mainThreadDone.set(true)
        
        // 等待子线程完成
        childThreadDone.await()
        
        // 再次确认主线程的值没有变化
        assertEquals("mainThread", IdempotentContext.getKey("threadKey"))
    }

    @Test
    fun `test complex objects can be stored and retrieved`() {
        // 测试存储和检索复杂对象
        val complexObject = ComplexObject("test", 123, listOf("item1", "item2"))
        
        IdempotentContext.put("complexKey", complexObject)
        
        val retrieved = IdempotentContext.getKey("complexKey") as ComplexObject
        
        assertEquals("test", retrieved.name)
        assertEquals(123, retrieved.id)
        assertEquals(2, retrieved.items.size)
    }

    // 测试用复杂对象
    data class ComplexObject(val name: String, val id: Int, val items: List<String>)
}