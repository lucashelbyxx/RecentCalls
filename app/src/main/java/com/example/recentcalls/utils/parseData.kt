package com.example.recentcalls.utils

import com.example.recentcalls.JsonContants.PersonInfo_1
import com.example.recentcalls.JsonContants.RecentCall_1
import com.example.recentcalls.utils.ParseData.parsePersonInfo
import com.example.recentcalls.utils.ParseData.parseRecentCall
import com.example.recentcalls.data.PersonInfo
import com.example.recentcalls.data.RecentCall
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object ParseData {
    private val gson = Gson()

    fun parseRecentCall(jsonString: String): List<RecentCall> {
        val itemType = object : TypeToken<List<RecentCall>>() {}.type

        return gson.fromJson(jsonString, itemType)
    }

    fun parsePersonInfo(jsonString: String): List<PersonInfo> {
        val itemType = object : TypeToken<List<PersonInfo>>() {}.type

        return gson.fromJson(jsonString, itemType)
    }
}

fun main() {
    for (item in parseRecentCall(RecentCall_1)) {
        println("ID: ${item.id}, last_call_time: ${item.last_call_time}")
    }
    for (item in parsePersonInfo(PersonInfo_1)) {
        println("ID: ${item.id}, Name: ${item.name}, Gender: ${item.gender}, phone_number: ${item.phone_number}, updated_at: ${item.updated_at}")
    }
}