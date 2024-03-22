package com.example.recentcalls.utils

import com.example.recentcalls.R
import com.example.recentcalls.data.CallRecord
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

object Utils {

    fun timeStamp2DateStr(timeStamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(timeStamp * 1000) // 将秒转换为毫秒
        return sdf.format(date)
    }

    fun generateRandomCallRecords(count: Int): List<CallRecord> {
        val names = listOf("Alice", "Bob", "Charlie", "David", "Eve") // 示例名字列表
        val phoneNumberPrefix = "123456" // 假设的电话号码前缀
        val idPrefix = "a1b2c3d4-e5f6-g7h8-i9j0-"

        val random = java.util.Random()
        return (1..count).map {
            val id = "$idPrefix${generateRandomString(12)}"
            val avatar = R.drawable.ic_launcher_background
            val name = names[random.nextInt(names.size)] // 从名字列表中随机选择一个
            val phoneNumber = "$phoneNumberPrefix${random.nextInt(10000)}" // 生成随机电话号码
            val lastCallTime = Instant.now().epochSecond  // 生成一个当前秒时间戳
            val callTime = timeStamp2DateStr(lastCallTime) // 将时间戳转换为字符串格式

            CallRecord(id, avatar, name, phoneNumber, lastCallTime, callTime)
        }
    }

    fun generateRandomString(length: Int): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length).map { chars.random() }.joinToString("")
    }
}

fun main() {
    val callRecords = Utils.generateRandomCallRecords(5)
    callRecords.forEach { println(it) }
}