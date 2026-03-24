package com.allways.hackaton.data.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SolanaRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val rpcUrl = "https://api.devnet.solana.com"

    suspend fun rewardUser(walletAddress: String, amountLamports: Long = 1_000_000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val body = mapOf(
                    "jsonrpc" to "2.0",
                    "id" to 1,
                    "method" to "requestAirdrop",
                    "params" to listOf(walletAddress, amountLamports)
                )
                val request = Request.Builder()
                    .url(rpcUrl)
                    .post(gson.toJson(body).toRequestBody("application/json".toMediaType()))
                    .build()
                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }
}