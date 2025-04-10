package faustofan.app.framework.web.result

/**
 * Rust风格的Result类型
 * 用于表示操作可能成功或失败
 * @param T 成功值的类型
 * @param E 错误值的类型
 */
sealed class Result<out T, out E> {
	/**
	 * 表示操作成功的情况
	 * @param value 成功的值
	 */
	data class Ok<out T>(val value: T) : Result<T, Nothing>()

	/**
	 * 表示操作失败的情况
	 * @param error 错误值
	 */
	data class Err<out E>(val error: E) : Result<Nothing, E>()

	/**
	 * 判断是否为Ok
	 */
	fun isOk(): Boolean = this is Ok

	/**
	 * 判断是否为Err
	 */
	fun isErr(): Boolean = this is Err

	/**
	 * 获取Ok值，如果为Err则返回null
	 */
	fun getOrNull(): T? = when (this) {
		is Ok -> value
		is Err -> null
	}

	/**
	 * 获取Err值，如果为Ok则返回null
	 */
	fun errorOrNull(): E? = when (this) {
		is Ok -> null
		is Err -> error
	}

	/**
	 * 如果为Ok则执行给定的函数
	 */
	inline fun onSuccess(action: (T) -> Unit): Result<T, E> {
		if (this is Ok) {
			action(value)
		}
		return this
	}

	/**
	 * 如果为Err则执行给定的函数
	 */
	inline fun onFailure(action: (E) -> Unit): Result<T, E> {
		if (this is Err) {
			action(error)
		}
		return this
	}

	/**
	 * 将Result转换为Option
	 */
	fun toOption(): Option<T> = when (this) {
		is Ok -> Option.Some(value)
		is Err -> Option.None
	}

	/**
	 * 映射Ok值
	 */
	inline fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
		is Ok -> Ok(transform(value))
		is Err -> Err(error)
	}

	/**
	 * 映射Err值
	 */
	inline fun <F> mapError(transform: (E) -> F): Result<T, F> = when (this) {
		is Ok -> Ok(value)
		is Err -> Err(transform(error))
	}

	/**
	 * 如果为Ok则返回给定的值，否则返回Err
	 */
	inline fun <R> flatMap(transform: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> = when (this) {
		is Ok -> transform(value)
		is Err -> Err(error)
	}

	/**
	 * 如果为Err则返回给定的值，否则返回Ok
	 */
	fun orElse(other: Result<@UnsafeVariance T, @UnsafeVariance E>): Result<T, E> = when (this) {
		is Ok -> this
		is Err -> other
	}

	/**
	 * 如果为Err则使用给定的函数计算返回值，否则返回Ok
	 */
	inline fun orElseWith(otherFn: (E) -> Result<@UnsafeVariance T, @UnsafeVariance E>): Result<T, E> = when (this) {
		is Ok -> this
		is Err -> otherFn(error)
	}

	/**
	 * 如果为Err则使用给定的函数计算返回值，否则返回Ok
	 * 与orElseWith类似，但允许转换错误类型
	 */
	inline fun orElseWithTransform(transform: (E) -> Result<@UnsafeVariance T, @UnsafeVariance E>): Result<T, E> = when (this) {
		is Ok -> Ok(value)
		is Err -> transform(error)
	}

	/**
	 * 如果为Ok则返回给定的值，否则返回Err
	 */
	fun getOrElse(default: @UnsafeVariance T): T = when (this) {
		is Ok -> value
		is Err -> default
	}

	/**
	 * 如果为Err则返回给定的值，否则返回Ok
	 */
	fun errorOrElse(default: @UnsafeVariance E): E = when (this) {
		is Ok -> default
		is Err -> error
	}

	/**
	 * 将Result转换为字符串
	 */
	override fun toString(): String = when (this) {
		is Ok -> "Ok($value)"
		is Err -> "Err($error)"
	}

	/**
	 * 获取Ok中的值，如果为Err则抛出异常
	 * 类似于Rust中的unwrap
	 */
	fun unwrap(): T = when (this) {
		is Ok -> value
		is Err -> throw IllegalStateException("Result.Err.unwrap(): $error")
	}

	/**
	 * 获取Ok中的值，如果为Err则抛出带有自定义消息的异常
	 * 类似于Rust中的expect
	 */
	fun expect(message: String): T = when (this) {
		is Ok -> value
		is Err -> throw IllegalStateException("$message: $error")
	}

	/**
	 * 获取Ok中的值，如果为Err则返回给定的默认值
	 * 类似于Rust中的unwrap_or
	 */
	fun unwrapOr(default: @UnsafeVariance T): T = when (this) {
		is Ok -> value
		is Err -> default
	}

	/**
	 * 获取Ok中的值，如果为Err则使用给定的函数计算默认值
	 * 类似于Rust中的unwrap_or_else
	 */
	inline fun unwrapOrElse(defaultFn: () -> @UnsafeVariance T): T = when (this) {
		is Ok -> value
		is Err -> defaultFn()
	}

	/**
	 * 获取Err中的值，如果为Ok则抛出异常
	 * 类似于Rust中的unwrap_err
	 */
	fun unwrapErr(): E = when (this) {
		is Ok -> throw IllegalStateException("Result.Ok.unwrapErr(): $value")
		is Err -> error
	}

	/**
	 * 获取Err中的值，如果为Ok则抛出带有自定义消息的异常
	 * 类似于Rust中的expect_err
	 */
	fun expectErr(message: String): E = when (this) {
		is Ok -> throw IllegalStateException("$message: $value")
		is Err -> error
	}

	/**
	 * 获取Err中的值，如果为Ok则返回给定的默认值
	 * 类似于Rust中的unwrap_err_or
	 */
	fun unwrapErrOr(default: @UnsafeVariance E): E = when (this) {
		is Ok -> default
		is Err -> error
	}

	/**
	 * 获取Err中的值，如果为Ok则使用给定的函数计算默认值
	 * 类似于Rust中的unwrap_err_or_else
	 */
	inline fun unwrapErrOrElse(defaultFn: () -> @UnsafeVariance E): E = when (this) {
		is Ok -> defaultFn()
		is Err -> error
	}

	/**
	 * 如果为Ok且满足给定的条件则返回true，否则返回false
	 * 类似于Rust中的is_ok_and
	 */
	inline fun isOkAnd(predicate: (T) -> Boolean): Boolean = when (this) {
		is Ok -> predicate(value)
		is Err -> false
	}

	/**
	 * 如果为Err且满足给定的条件则返回true，否则返回false
	 * 类似于Rust中的is_err_and
	 */
	inline fun isErrAnd(predicate: (E) -> Boolean): Boolean = when (this) {
		is Ok -> false
		is Err -> predicate(error)
	}

	/**
	 * 如果为Ok则返回Some(value)，否则返回None
	 * 类似于Rust中的ok
	 */
	fun ok(): Option<T> = when (this) {
		is Ok -> Option.Some(value)
		is Err -> Option.None
	}

	/**
	 * 如果为Err则返回Some(error)，否则返回None
	 * 类似于Rust中的err
	 */
	fun err(): Option<E> = when (this) {
		is Ok -> Option.None
		is Err -> Option.Some(error)
	}

	/**
	 * 如果为Ok则返回Ok(transform(value))，否则返回Err(error)
	 * 类似于Rust中的map_or
	 */
	inline fun <R> mapOr(default: R, transform: (T) -> R): R = when (this) {
		is Ok -> transform(value)
		is Err -> default
	}

	/**
	 * 如果为Ok则返回Ok(transform(value))，否则返回Err(error)
	 * 类似于Rust中的map_or_else
	 */
	inline fun <R> mapOrElse(defaultFn: () -> R, transform: (T) -> R): R = when (this) {
		is Ok -> transform(value)
		is Err -> defaultFn()
	}

	/**
	 * 如果为Ok则返回Ok(transform(value))，否则返回Err(transform(error))
	 * 类似于Rust中的map_or_else
	 */
	inline fun <R, F> mapOrElse(defaultFn: (E) -> F, transform: (T) -> R): Result<R, F> = when (this) {
		is Ok -> Ok(transform(value))
		is Err -> Err(defaultFn(error))
	}

	/**
	 * 如果为Ok则返回Ok(transform(value))，否则返回Err(error)
	 * 类似于Rust中的and_then
	 */
	inline fun <R> andThen(transform: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> = when (this) {
		is Ok -> transform(value)
		is Err -> Err(error)
	}

	/**
	 * 如果为Ok则返回Ok(value)，否则返回other
	 * 类似于Rust中的or
	 */
	fun or(other: Result<@UnsafeVariance T, @UnsafeVariance E>): Result<T, E> = when (this) {
		is Ok -> this
		is Err -> other
	}

	/**
	 * 如果为Ok则返回Ok(value)，否则返回other
	 * 类似于Rust中的and
	 */
	fun <U> and(other: Result<U, @UnsafeVariance E>): Result<U, E> = when (this) {
		is Ok -> other
		is Err -> Err(error)
	}

	companion object {
		/**
		 * 创建一个成功的Result
		 */
		fun <T> ok(value: T): Result<T, Nothing> = Ok(value)

		/**
		 * 创建一个失败的Result
		 */
		fun <E> err(error: E): Result<Nothing, E> = Err(error)

		/**
		 * 将可能抛出异常的操作包装为Result
		 */
		fun <T> runCatching(block: () -> T): Result<T, Throwable> = try {
			ok(block())
		} catch (e: Throwable) {
			err(e)
		}

		/**
		 * 将多个Result组合成一个Result
		 */
		fun <T> combine(vararg results: Result<T, *>): Result<List<T>, Any> {
			val values = mutableListOf<T>()
			for (result in results) {
				when (result) {
					is Ok -> values.add(result.value)
					is Err -> return Err(result.error!!)
				}
			}
			return Ok(values)
		}

		/**
		 * 将多个Result组合成一个Result，使用给定的函数组合值
		 */
		fun <T, R, E> combine(vararg results: Result<T, E>, transform: (List<T>) -> R): Result<R, E> {
			val values = mutableListOf<T>()
			for (result in results) {
				when (result) {
					is Ok -> values.add(result.value)
					is Err -> return Err(result.error)
				}
			}
			return Ok(transform(values))
		}
	}
}

