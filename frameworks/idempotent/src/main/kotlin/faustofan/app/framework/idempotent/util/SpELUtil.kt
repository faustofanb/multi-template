package faustofan.app.framework.idempotent.util

import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.Method

object SpELUtil {
	/**
	 * 校验并返回实际使用的 spEL 表达式
	 *
	 * @param spEl spEL 表达式
	 * @return 实际使用的 spEL 表达式
	 */
	fun parseKey(spEl: String, method: Method, contextObj: Array<Any?>): Any? {
		return if (spEl.contains("#") || spEl.contains("T(")) {
			parse(spEl, method, contextObj)
		} else {
			spEl
		}
	}

	/**
	 * 解析给定的Spring表达式语言（SpEL）字符串，并在指定的上下文中执行它。
	 *
	 * @param spEl 要解析的Spring表达式语言（SpEL）字符串。
	 * @param method 与方法相关的参数名称将被用于设置上下文变量。
	 * @param contextObj 与方法参数对应的值数组，这些值将被设置为上下文变量。
	 * @return 解析并执行SpEL表达式后的结果，如果表达式没有返回值，则返回null。
	 */
	fun parse(spEl: String, method: Method, contextObj: Array<Any?>): Any? {
	    // 使用SpelExpressionParser解析SpEL表达式
	    val exp = SpelExpressionParser().parseExpression(spEl)

	    // 获取方法的参数名称，如果无法获取则返回空数组
	    val params = DefaultParameterNameDiscoverer().getParameterNames(method)
	        ?: arrayOf()

	    // 创建标准评估上下文
	    val context = StandardEvaluationContext()

	    // 如果方法有参数，则将参数名称和对应的值设置为上下文变量
	    if (params.isNotEmpty()) {
	        params.forEachIndexed { index, _ ->
	            context.setVariable(params[index], contextObj[index])
	        }
	    }

	    // 在上下文中执行SpEL表达式并返回结果
	    return exp.getValue(context)
	}
}

