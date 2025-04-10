package faustofan.app.framework.web.result

/**
 * Rust风格的Option类型
 * 用于表示可能存在或不存在的值
 * @param T 值的类型
 */
sealed class Option<out T> {
	/**
	 * 表示存在值的情况
	 * @param value 存在的值
	 */
	data class Some<out T>(val value: T) : Option<T>()
	
	/**
	 * 表示不存在值的情况
	 */
	data object None : Option<Nothing>()

	/**
	 * 获取值，如果为None则返回null
	 */
	fun getOrNull(): T? = when (this) {
		is Some -> value
		is None -> null
	}

	/**
	 * 如果为Some则执行给定的函数
	 */
	inline fun onSome(action: (T) -> Unit): Option<T> {
		if (this is Some) {
			action(value)
		}
		return this
	}

	/**
	 * 如果为None则执行给定的函数
	 */
	inline fun onNone(action: () -> Unit): Option<T> {
		if (this is None) {
			action()
		}
		return this
	}

	/**
	 * 映射Some值
	 */
	inline fun <R> map(transform: (T) -> R): Option<R> = when (this) {
		is Some -> Some(transform(value))
		is None -> None
	}

	/**
	 * 如果为Some则返回给定的值，否则返回None
	 */
	inline fun <R> flatMap(transform: (T) -> Option<R>): Option<R> = when (this) {
		is Some -> transform(value)
		is None -> None
	}

	/**
	 * 如果为None则返回给定的值
	 */
	fun orElse(other: Option<@UnsafeVariance T>): Option<T> = when (this) {
		is Some -> this
		is None -> other
	}

	/**
	 * 如果为None则使用给定的函数计算返回值
	 */
	inline fun orElseWith(otherFn: () -> Option<@UnsafeVariance T>): Option<T> = when (this) {
		is Some -> this
		is None -> otherFn()
	}

	/**
	 * 如果为None则返回给定的值
	 */
	fun getOrElse(default: @UnsafeVariance T): T = when (this) {
		is Some -> value
		is None -> default
	}
	
	/**
	 * 将Option转换为Result
	 */
	fun <E> toResult(error: E): Result<T, E> = when (this) {
		is Some -> Result.Ok(value)
		is None -> Result.Err(error)
	}
	
	/**
	 * 将Option转换为Result，使用给定的函数生成错误
	 */
	inline fun <E> toResult(errorFn: () -> E): Result<T, E> = when (this) {
		is Some -> Result.Ok(value)
		is None -> Result.Err(errorFn())
	}
	
	/**
	 * 将Option转换为List
	 */
	fun toList(): List<T> = when (this) {
		is Some -> listOf(value)
		is None -> emptyList()
	}
	
	/**
	 * 将Option转换为Sequence
	 */
	fun asSequence(): Sequence<T> = when (this) {
		is Some -> sequenceOf(value)
		is None -> emptySequence()
	}
	
	/**
	 * 将Option转换为字符串
	 */
	override fun toString(): String = when (this) {
		is Some -> "Some($value)"
		is None -> "None"
	}
	
	/**
	 * 获取Some中的值，如果为None则抛出异常
	 * 类似于Rust中的unwrap
	 */
	fun unwrap(): T = when (this) {
		is Some -> value
		is None -> throw NoSuchElementException("Option.None.unwrap()")
	}
	
	/**
	 * 获取Some中的值，如果为None则抛出带有自定义消息的异常
	 * 类似于Rust中的expect
	 */
	fun expect(message: String): T = when (this) {
		is Some -> value
		is None -> throw NoSuchElementException(message)
	}
	
	/**
	 * 获取Some中的值，如果为None则返回给定的默认值
	 * 类似于Rust中的unwrap_or
	 */
	fun unwrapOr(default: @UnsafeVariance T): T = when (this) {
		is Some -> value
		is None -> default
	}
	
	/**
	 * 获取Some中的值，如果为None则使用给定的函数计算默认值
	 * 类似于Rust中的unwrap_or_else
	 */
	inline fun unwrapOrElse(defaultFn: () -> @UnsafeVariance T): T = when (this) {
		is Some -> value
		is None -> defaultFn()
	}
	
	/**
	 * 获取Some中的值，如果为None则返回null
	 * 类似于Rust中的unwrap_or_default
	 */
	fun unwrapOrNull(): T? = when (this) {
		is Some -> value
		is None -> null
	}
	
	/**
	 * 如果为Some则返回true，否则返回false
	 * 类似于Rust中的is_some
	 */
	fun isSome(): Boolean = this is Some
	
	/**
	 * 如果为None则返回true，否则返回false
	 * 类似于Rust中的is_none
	 */
	fun isNone(): Boolean = this is None
	
	/**
	 * 如果为Some且满足给定的条件则返回true，否则返回false
	 * 类似于Rust中的is_some_and
	 */
	inline fun isSomeAnd(predicate: (T) -> Boolean): Boolean = when (this) {
		is Some -> predicate(value)
		is None -> false
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的filter
	 */
	inline fun filter(predicate: (T) -> Boolean): Option<T> = when (this) {
		is Some -> if (predicate(value)) this else None
		is None -> None
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的map_or
	 */
	inline fun <R> mapOr(default: R, transform: (T) -> R): R = when (this) {
		is Some -> transform(value)
		is None -> default
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的map_or_else
	 */
	inline fun <R> mapOrElse(defaultFn: () -> R, transform: (T) -> R): R = when (this) {
		is Some -> transform(value)
		is None -> defaultFn()
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的and_then
	 */
	inline fun <R> andThen(transform: (T) -> Option<R>): Option<R> = when (this) {
		is Some -> transform(value)
		is None -> None
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的xor
	 */
	fun xor(other: Option<@UnsafeVariance T>): Option<T> = when {
		this is Some && other is None -> this
		this is None && other is Some -> other
		else -> None
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的zip
	 */
	fun <U> zip(other: Option<U>): Option<Pair<T, U>> = when {
		this is Some && other is Some -> Some(Pair(value, other.value))
		else -> None
	}
	
	/**
	 * 如果为Some则返回Some(transform(value))，否则返回None
	 * 类似于Rust中的zip_with
	 */
	inline fun <U, R> zipWith(other: Option<U>, transform: (T, U) -> R): Option<R> = when {
		this is Some && other is Some -> Some(transform(value, other.value))
		else -> None
	}
	
	companion object {
		/**
		 * 创建一个Some
		 */
		fun <T> some(value: T): Option<T> = Some(value)
		
		/**
		 * 创建一个None
		 */
		fun <T> none(): Option<T> = None
		
		/**
		 * 将可能为null的值转换为Option
		 */
		fun <T> fromNullable(value: T?): Option<T> = if (value != null) Some(value) else None
		
		/**
		 * 将集合转换为Option，如果集合为空则返回None
		 */
		fun <T> fromCollection(collection: Collection<T>): Option<List<T>> =
			if (collection.isEmpty()) None else Some(collection.toList())
			
		/**
		 * 将多个Option组合成一个Option
		 */
		fun <T> combine(vararg options: Option<T>): Option<List<T>> {
			val values = mutableListOf<T>()
			for (option in options) {
				when (option) {
					is Some -> values.add(option.value)
					is None -> return None
				}
			}
			return Some(values)
		}
		
		/**
		 * 将多个Option组合成一个Option，使用给定的函数组合值
		 */
		fun <T, R> combine(vararg options: Option<T>, transform: (List<T>) -> R): Option<R> {
			val values = mutableListOf<T>()
			for (option in options) {
				when (option) {
					is Some -> values.add(option.value)
					is None -> return None
				}
			}
			return Some(transform(values))
		}
	}
} 