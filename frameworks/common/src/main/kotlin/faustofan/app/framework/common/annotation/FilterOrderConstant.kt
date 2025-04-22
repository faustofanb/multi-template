package faustofan.app.framework.common.annotation

object FilterOrderConstant {
	/**
	 * 用户传输过滤器的优先级。
	 *
	 * 此常量定义了用户传输过滤器在处理链中的顺序。传输过滤器用于对用户数据进行预处理或后处理，
	 * 例如加密、压缩或验证数据。USER_TRANSMIT_FILTER_ORDER的值决定了它将在所有其他过滤器之前
	 * 还是之后执行。较低的数字表示更高的优先级，因此100表示这个过滤器将在默认情况下具有中等优先级。
	 *
	 * 通过调整这个值，可以在不修改过滤器实现的情况下，改变过滤器的执行顺序，从而影响数据处理的方式。
	 *
	 * 注意：这个值被声明为const，意味着它在编译时是已知的，不能在运行时更改。这也意味着
	 * 它只能被分配为一个常量表达式的结果，且一旦赋值后不能修改。
	 */
	const val USER_TRANSMIT_FILTER_ORDER = 100
}