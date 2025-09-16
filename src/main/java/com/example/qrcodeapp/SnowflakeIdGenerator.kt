package com.example.qrcodeapp

import java.util.concurrent.atomic.AtomicLong

/**
 * 雪花算法ID生成器
 * 生成64位长整型ID，包含时间戳、机器ID、序列号等信息
 */
class SnowflakeIdGenerator {
    
    // 开始时间戳 (2024-01-01 00:00:00)
    private val startTimestamp = 1704067200000L
    
    // 机器ID位数
    private val machineIdBits = 5L
    
    // 序列号位数
    private val sequenceBits = 12L
    
    // 机器ID最大值
    private val maxMachineId = -1L shl (machineIdBits.toInt())
    private val maxSequence = -1L shl (sequenceBits.toInt())
    
    // 机器ID左移位数
    private val machineIdShift = sequenceBits
    
    // 时间戳左移位数
    private val timestampLeftShift = sequenceBits + machineIdBits
    
    // 机器ID (这里使用设备ID的hash值，实际项目中可以配置)
    private val machineId = getDeviceMachineId()
    
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
        
        // 如果当前时间小于上次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw RuntimeException("Clock moved backwards. Refusing to generate id")
        }
        
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            // 序列号自增
            val currentSequence = sequence.incrementAndGet()
            // 毫秒内序列溢出
            if (currentSequence > maxSequence) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp)
                sequence.set(0)
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence.set(0)
        }
        
        // 上次生成ID的时间戳
        lastTimestamp = timestamp
        
        // 移位并通过或运算拼到一起组成64位的ID
        return (timestamp - startTimestamp) shl timestampLeftShift.toInt() or
                (machineId shl machineIdShift.toInt()) or
                sequence.get()
    }
    
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
    
    /**
     * 获取设备机器ID
     * 这里使用设备的一些特征来生成一个相对稳定的机器ID
     */
    private fun getDeviceMachineId(): Long {
        // 使用设备的一些特征来生成机器ID
        val deviceInfo = android.os.Build.MODEL + android.os.Build.MANUFACTURER
        return (deviceInfo.hashCode() and 0x1F).toLong() // 取低5位作为机器ID
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SnowflakeIdGenerator? = null
        
        fun getInstance(): SnowflakeIdGenerator {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SnowflakeIdGenerator().also { INSTANCE = it }
            }
        }
    }
}
