import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * LogUtil对象提供了一种获取Logger实例的方法
 * 这个对象的目的是统一日志记录方式，提供了一种简便的方法来获取Logger实例
 */
object LogUtil {
    /**
     * 根据方法签名获取Logger实例
     *
     * @param joinPoint 切入点对象，包含了方法签名等信息
     * @return 返回对应的Logger实例，用于日志记录
     *
     * 方法解释：
     * 1. 使用LogFactory的getLogger方法获取Logger实例
     * 2. 传入参数为方法签名对象，表示需要为哪个类获取Logger
     * 3. 如果签名对象不是MethodSignature类型，将抛出ClassCastException
     */
    fun getLog(joinPoint: ProceedingJoinPoint): Logger {
        return LoggerFactory.getLogger((joinPoint.signature as MethodSignature).declaringType)
    }
}