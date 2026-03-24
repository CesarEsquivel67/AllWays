package com.allways.hackaton.solana.models

data class RewardResponse(
    val success: Boolean,
    val transactionHash: String,
    val message: String,
    val amount: Long,
    val reason: String
)

data class RewardItem(
    val amount: Long,
    val reason: String,
    val date: String,
    val transactionHash: String,
    val status: String
)

data class RewardHistory(
    val rewards: List<RewardItem> = emptyList()
)

data class UserWallet(
    val userId: String,
    val publicKey: String,
    val balance: Long = 0L
)