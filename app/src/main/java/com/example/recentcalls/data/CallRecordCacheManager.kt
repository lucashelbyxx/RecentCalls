package com.example.recentcalls.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CallRecordCacheManager {

    private const val PREF_NAME = "CallRecordCache"
    private const val KEY_CALL_RECORD_LIST = "callRecordList"

    fun saveCallRecordList(context: Context, callRecordList: List<CallRecord>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(callRecordList)
        editor.putString(KEY_CALL_RECORD_LIST, json)
        editor.apply()
    }

    fun getCallRecordList(context: Context): List<CallRecord> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val json: String? = sharedPreferences.getString(KEY_CALL_RECORD_LIST, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<CallRecord>>() {}.type
            gson.fromJson(json, type)
        } else {
            ArrayList()
        }
    }
}