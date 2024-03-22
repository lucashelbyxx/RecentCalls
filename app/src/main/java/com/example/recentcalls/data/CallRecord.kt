package com.example.recentcalls.data

data class CallRecord(
    val id: String,
    val avatar: Int, // Resource ID for the avatar
    val name: String,
    val phoneNumber: String,
    var last_call_time: Long,
    var callTime: String
){
    override fun toString(): String {
        return "User(id=$id,name='$name', phoneNumber=$phoneNumber, callTime=$callTime, last_call_time=$last_call_time, avatar=$avatar)"
    }
}
