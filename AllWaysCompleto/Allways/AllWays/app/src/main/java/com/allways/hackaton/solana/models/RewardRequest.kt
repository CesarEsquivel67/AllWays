package com.allways.hackaton.solana.models

data class RewardRequest(
    val recipientPublicKey: String,
    val amount: Long,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)