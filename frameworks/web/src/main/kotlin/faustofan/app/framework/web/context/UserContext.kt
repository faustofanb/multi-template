package faustofan.app.framework.web.context

import com.alibaba.ttl.TransmittableThreadLocal


/**
 * UserContext对象用于在多线程环境下安全地存储和访问当前用户的上下文信息。
 * 它利用TransmittableThreadLocal来实现线程间的用户信息传递，确保用户信息在同一个线程内的一致性和隔离性。
 */
object UserContext {
    /**
     * 使用TransmittableThreadLocal来存储用户信息，保证线程安全。
     * 这样设计是为了在同一个线程中保持用户上下文的一致性，同时能够在线程间传递用户信息。
     */
    private val USER_THREAD_LOCAL = TransmittableThreadLocal<UserInfoDTO>()

    /**
     * 设置当前线程的用户信息。
     * 这个方法用于将用户信息绑定到当前线程，以便在后续的操作中可以方便地获取到该用户的信息。
     *
     * @param user 用户信息DTO，包含了用户的标识和相关属性。
     */
    fun setUser(user: UserInfoDTO) {
        USER_THREAD_LOCAL.set(user)
    }

    /**
     * 获取当前线程的用户ID。
     * 这个方法用于获取当前线程所绑定的用户的信息，特别是用户的唯一标识ID。
     *
     * @return 当前线程所绑定的用户的ID，如果用户未设置或已移除，则返回null。
     */
    fun getUserId(): String? {
        return USER_THREAD_LOCAL.get()?.userId
    }

    /**
     * 获取当前线程的用户名。
     * 这个方法用于获取当前线程所绑定的用户的信息，特别是用户的用户名。
     *
     * @return 当前线程所绑定的用户的用户名，如果用户未设置或已移除，则返回null。
     */
    fun getUsername(): String? {
        return USER_THREAD_LOCAL.get()?.username
    }

    /**
     * 获取当前线程的用户真实姓名。
     * 这个方法用于获取当前线程所绑定的用户的信息，特别是用户的真实姓名。
     *
     * @return 当前线程所绑定的用户的真名，如果用户未设置或已移除，则返回null。
     */
    fun getRealName(): String? {
        return USER_THREAD_LOCAL.get()?.realName
    }

    /**
     * 获取当前线程的用户令牌。
     * 这个方法用于获取当前线程所绑定的用户的信息，特别是用于身份验证的令牌。
     *
     * @return 当前线程所绑定的用户的令牌，如果用户未设置或已移除，则返回null。
     */
    fun getToken(): String? {
        return USER_THREAD_LOCAL.get()?.token
    }

    /**
     * 移除当前线程的用户信息。
     * 这个方法用于清除当前线程所绑定的用户信息，确保线程的安全性和隔离性。
     */
    fun removeUser() {
        USER_THREAD_LOCAL.remove()
    }
}



