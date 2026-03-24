package com.allways.hackaton.solana.api

import com.allways.hackaton.solana.models.RewardHistory
import com.allways.hackaton.solana.models.RewardItem
import com.allways.hackaton.solana.models.RewardRequest
import com.allways.hackaton.solana.models.RewardResponse
import com.allways.hackaton.solana.models.UserWallet
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SolanaApiService {

    @POST("/api/rewards/send")
    suspend fun sendReward(@Body request: RewardRequest): RewardResponse

    @GET("/api/rewards/history/{userId}")
    suspend fun getRewardHistory(@Path("userId") userId: String): RewardHistory

    @GET("/api/wallet/{userId}")
    suspend fun getUserWallet(@Path("userId") userId: String): UserWallet
}