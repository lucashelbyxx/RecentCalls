package com.example.recentcalls.data

data class RecentCall(
    val id: String,
    val last_call_time: Long
){
    override fun toString(): String {
        return "RecentCall(id=$id, last_call_time=$last_call_time)"
    }
}
