package faustofan.app.framework.idempotent.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class SpELUtilTest {

    // 定义测试用例的辅助类和方法
    class TestClass {
        fun testMethod(id: String, value: Int, date: LocalDate): String {
            return "$id-$value-$date"
        }
        
        fun methodWithComplex(user: User, amount: BigDecimal, tags: List<String>): String {
            return "${user.name}-$amount-${tags.joinToString(",")}"
        }
    }
    
    data class User(val id: Long, val name: String, val age: Int)
    
    @Test
    fun `test parseKey with simple string`() {
        // 准备测试数据
        val spEl = "simpleKey123"
        val method = TestClass::class.java.getDeclaredMethod(
            "testMethod",
            String::class.java,
            Int::class.java,
            LocalDate::class.java
        )
        val args: Array<Any?> = arrayOf("test123", 42, LocalDate.of(2023, 1, 1))
        
        // 执行测试
        val result = SpELUtil.parseKey(spEl, method, args)
        
        // 验证结果 - 不包含#或T(的字符串应原样返回
        assertEquals("simpleKey123", result)
    }
    
    @Test
    fun `test parseKey with SpEL hash expression`() {
        // 准备测试数据
        val spEl = "#id + '-' + #value"
        val method = TestClass::class.java.getDeclaredMethod("testMethod", String::class.java, Int::class.java, LocalDate::class.java)
        val args: Array<Any?> = arrayOf("test123", 42, LocalDate.of(2023, 1, 1))
        
        // 执行测试
        val result = SpELUtil.parseKey(spEl, method, args)
        
        // 验证结果 - 应解析为参数组合
        assertEquals("test123-42", result)
    }
    
    @Test
    fun `test parseKey with SpEL T expression`() {
        // 准备测试数据
        val spEl = "T(java.lang.Math).max(#value, 50)"
        val method = TestClass::class.java.getDeclaredMethod("testMethod", String::class.java, Int::class.java, LocalDate::class.java)
        val args: Array<Any?> = arrayOf("test123", 42, LocalDate.of(2023, 1, 1))
        
        // 执行测试
        val result = SpELUtil.parseKey(spEl, method, args)
        
        // 验证结果 - 应返回50（Math.max(42, 50)）
        assertEquals(50, result)
    }
    
    @Test
    fun `test parse with complex object parameters`() {
        // 准备测试数据
        val user = User(1L, "Zhang San", 30)
        val amount = BigDecimal("1234.56")
        val tags = listOf("VIP", "NEW", "ACTIVE")
        
        val spEl = "#user.name + '-' + #amount + '-' + #tags[0]"
        val method = TestClass::class.java.getDeclaredMethod(
            "methodWithComplex", 
            User::class.java, 
            BigDecimal::class.java, 
            List::class.java
        )
        val args: Array<Any?> = arrayOf(user, amount, tags)
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果
        assertEquals("Zhang San-1234.56-VIP", result)
    }
    
    @Test
    fun `test parse with date formatting`() {
        // 准备测试数据
        val spEl = "#date.toString()"
        val method = TestClass::class.java.getDeclaredMethod("testMethod", String::class.java, Int::class.java, LocalDate::class.java)
        val date = LocalDate.of(2023, 1, 1)
        val args: Array<Any?> = arrayOf("test123", 42, date)
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果
        assertEquals(date.toString(), result)
    }
    
    @Test
    fun `test parse with string manipulation`() {
        // 准备测试数据
        val spEl = "#id.substring(0, 4).toUpperCase()"
        val method = TestClass::class.java.getDeclaredMethod("testMethod", String::class.java, Int::class.java, LocalDate::class.java)
        val args: Array<Any?> = arrayOf("test123", 42, LocalDate.of(2023, 1, 1))
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果 - 应返回TEST
        assertEquals("TEST", result)
    }
    
    @Test
    fun `test parse with arithmetic operations`() {
        // 准备测试数据
        val spEl = "#value * 2 + 10"
        val method = TestClass::class.java.getDeclaredMethod("testMethod", String::class.java, Int::class.java, LocalDate::class.java)
        val args: Array<Any?> = arrayOf("test123", 42, LocalDate.of(2023, 1, 1))
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果 - 应返回94 (42*2+10)
        assertEquals(94, result)
    }
    
    @Test
    fun `test parse with multiple method calls`() {
        // 准备测试数据
        val spEl = "T(java.lang.String).format('User %s with ID %d', #user.name, #user.id)"
        val method = TestClass::class.java.getDeclaredMethod(
            "methodWithComplex", 
            User::class.java, 
            BigDecimal::class.java, 
            List::class.java
        )
        val user = User(1001L, "Li Si", 25)
        val args: Array<Any?> = arrayOf(user, BigDecimal("500"), listOf("STANDARD"))
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果
        assertEquals("User Li Si with ID 1001", result)
    }
    
    @Test
    fun `test parse with logical operations`() {
        // 准备测试数据
        val spEl = "#user.age > 18 and #amount > 1000"
        val method = TestClass::class.java.getDeclaredMethod(
            "methodWithComplex", 
            User::class.java, 
            BigDecimal::class.java, 
            List::class.java
        )
        val user = User(1L, "Wang Wu", 30)
        val args: Array<Any?> = arrayOf(user, BigDecimal("2000"), listOf("PREMIUM"))
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果 - 应返回true
        assertEquals(true, result)
    }
    
    @Test
    fun `test parse with null parameters`() {
        // 准备测试数据
        val spEl = "#user?.name ?: 'Unknown'"
        val method = TestClass::class.java.getDeclaredMethod(
            "methodWithComplex", 
            User::class.java, 
            BigDecimal::class.java, 
            List::class.java
        )
        val args = arrayOf(null, BigDecimal("0"), emptyList<String>())
        
        // 执行测试
        val result = SpELUtil.parse(spEl, method, args)
        
        // 验证结果 - 应返回'Unknown'
        assertEquals("Unknown", result)
    }
}