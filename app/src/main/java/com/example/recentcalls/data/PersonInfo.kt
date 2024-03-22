package com.example.recentcalls.data

data class PersonInfo(
    val id: String,
    val name: String,
    val gender: Int,
    val phone_number: String,
    val updated_at: Int
) {
    override fun toString(): String {
        return "PersonInfo(id=$id, name=$name,gender=$gender, phone_number=$phone_number, updated_at=$updated_at)"
    }
}
