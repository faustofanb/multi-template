package faustofan.app.framework.web.util

import java.net.NetworkInterface
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

/**
 * 雪花算法ID生成器
 * 用于生成分布式环境下的唯一ID
 * 
 * 结构：
 * - 1位符号位，始终为0
 * - 41位时间戳，精确到毫秒
 * - 10位工作机器ID（5位数据中心ID + 5位机器ID）
 * - 12位序列号
 */
class SnowflakeIdGenerator(
    private val datacenterId: Long = getDatacenterId(),
    private val workerId: Long = getWorkerId()
) {
    companion object {
        // 开始时间戳（2023-01-01 00:00:00）
        private const val START_TIMESTAMP = 1672531200000L
        
        // 各部分占用的位数
        private const val SEQUENCE_BITS = 12L
        private const val WORKER_ID_BITS = 5L
        private const val DATACENTER_ID_BITS = 5L
        
        // 各部分的最大值
        private const val MAX_SEQUENCE = -1L xor (-1L shl SEQUENCE_BITS.toInt())
        private const val MAX_WORKER_ID = -1L xor (-1L shl WORKER_ID_BITS.toInt())
        private const val MAX_DATACENTER_ID = -1L xor (-1L shl DATACENTER_ID_BITS.toInt())
        
        // 各部分向左的位移
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
        private const val TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS
        
        // 单例实例
        private val INSTANCE = SnowflakeIdGenerator()
        
        /**
         * 获取单例实例
         */
        fun getInstance(): SnowflakeIdGenerator = INSTANCE
        
        /**
         * 获取数据中心ID
         * 基于MAC地址生成
         */
        private fun getDatacenterId(): Long {
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                var macAddress: String? = null
                
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    val mac = networkInterface.hardwareAddress
                    if (mac != null && mac.isNotEmpty()) {
                        macAddress = mac.joinToString("") { String.format("%02X", it) }
                        break
                    }
                }
                
                if (macAddress == null) {
                    return 1L
                }
                
                // 使用MAC地址的后6位作为数据中心ID
                val macHash = macAddress.hashCode()
                return ((macHash.toLong() and 0xFFFF) shr 10) and MAX_DATACENTER_ID
            } catch (e: Exception) {
                return 1L
            }
        }
        
        /**
         * 获取工作机器ID
         * 基于进程ID生成
         */
        private fun getWorkerId(): Long {
            val processId = ProcessHandle.current().pid()
            return (processId and MAX_WORKER_ID)
        }
    }
    
    // 序列号
    private val sequence = AtomicLong(0L)
    
    // 上次生成ID的时间戳
    private var lastTimestamp = -1L
    
    /**
     * 生成下一个ID
     */
    @Synchronized
    fun nextId(): Long {
        var timestamp = System.currentTimeMillis()
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            throw RuntimeException("系统时钟回退，拒绝生成ID")
        }
        
        // 如果是同一时间生成的，则进行序列号递增
        if (timestamp == lastTimestamp) {
            val currentSequence = sequence.incrementAndGet()
            // 序列号超出最大值，等待下一毫秒
            if (currentSequence > MAX_SEQUENCE) {
                timestamp = tilNextMillis(lastTimestamp)
                sequence.set(0)
            }
        } else {
            // 时间戳变化，序列号重置
            sequence.set(0)
        }
        
        lastTimestamp = timestamp
        
        // 组合ID
        return ((timestamp - START_TIMESTAMP) shl TIMESTAMP_SHIFT.toInt()) or
                (datacenterId shl DATACENTER_ID_SHIFT.toInt()) or
                (workerId shl WORKER_ID_SHIFT.toInt()) or
                sequence.get()
    }
    
    /**
         * 等待下一个毫秒
         */
    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
    
    /**
     * 解析ID
     */
    fun parseId(id: Long): Map<String, Any> {
        val timestamp = (id shr TIMESTAMP_SHIFT.toInt()) + START_TIMESTAMP
        val datacenterId = (id shr DATACENTER_ID_SHIFT.toInt()) and MAX_DATACENTER_ID
        val workerId = (id shr WORKER_ID_SHIFT.toInt()) and MAX_WORKER_ID
        val sequence = id and MAX_SEQUENCE
        
        val dateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC)
        val dateTimeStr = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        
        return mapOf(
            "timestamp" to timestamp,
            "dateTime" to dateTimeStr,
            "datacenterId" to datacenterId,
            "workerId" to workerId,
            "sequence" to sequence
        )
    }
} 