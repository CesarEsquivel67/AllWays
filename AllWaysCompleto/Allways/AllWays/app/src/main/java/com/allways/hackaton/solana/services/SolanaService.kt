package com.allways.hackaton.solana.services

import com.allways.hackaton.solana.SolanaConfig
import com.allways.hackaton.solana.api.RetrofitClient
import com.allways.hackaton.solana.api.SolanaApiService
import com.allways.hackaton.solana.models.RewardRequest
import com.allways.hackaton.solana.models.RewardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SolanaService(private val apiService: SolanaApiService = RetrofitClient.apiService) {

    suspend fun sendAccessibilityReward(
        recipientPublicKey: String,
        placeId: String,
        infoId: String
    ): Result<RewardResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RewardRequest(
                recipientPublicKey = recipientPublicKey,
                amount = SolanaConfig.REWARD_FOR_ACCESSIBILITY_INFO,
                reason = "accessibility_info",
                metadata = mapOf(
                    "placeId" to placeId,
                    "infoId" to infoId
                )
            )
            val response = apiService.sendReward(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun sendReportReward(
        recipientPublicKey: String,
        reportId: String
    ): Result<RewardResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RewardRequest(
                recipientPublicKey = recipientPublicKey,
                amount = SolanaConfig.REWARD_FOR_REPORT,
                reason = "report",
                metadata = mapOf("reportId" to reportId)
            )
            val response = apiService.sendReward(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun sendVoteReward(
        recipientPublicKey: String,
        proposalId: String
    ): Result<RewardResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RewardRequest(
                recipientPublicKey = recipientPublicKey,
                amount = SolanaConfig.REWARD_FOR_VOTE,
                reason = "vote",
                metadata = mapOf("proposalId" to proposalId)
            )
            val response = apiService.sendReward(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}