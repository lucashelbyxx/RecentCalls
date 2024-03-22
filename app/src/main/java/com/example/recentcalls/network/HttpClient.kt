package com.example.recentcalls.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Kotlin 协程来封装 OkHttp 的 GET 和 POST 方法
 */
object HttpClient {
    private val client = OkHttpClient()

    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: throw IOException("Empty response body")
        }
    }

    suspend fun post(url: String, json: String): String = withContext(Dispatchers.IO) {
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: throw IOException("Empty response body")
        }
    }
}

suspend fun main() {
    println(HttpClient.get("http://127.0.0.1:8080/contacts/conversation"))
    val json = """ [{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p5","updated_at": 0},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6","updated_at": 0},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p7","updated_at": 1}] """
        .trimIndent()
    println(HttpClient.post("http://127.0.0.1:8080/contacts/users", json))
}