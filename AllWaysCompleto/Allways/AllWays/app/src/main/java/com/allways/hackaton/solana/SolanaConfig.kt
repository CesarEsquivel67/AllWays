package com.allways.hackaton.solana

object SolanaConfig {
    // Ambiente (cambiar a "mainnet-beta" cuando esté listo)
    const val NETWORK = "devnet"
    const val RPC_ENDPOINT = "https://api.devnet.solana.com"

    // URL de tu backend - ACTUALIZAR CON TU IP
    const val BACKEND_URL = "http://192.168.1.82:5000"

    // Token SPL - ACTUALIZAR CON TU MINT ADDRESS
    const val MINT_ADDRESS = "Tu_Mint_Address_Aqui"
    const val TOKEN_DECIMALS = 6

    // Recompensas por acción
    const val REWARD_FOR_ACCESSIBILITY_INFO = 10L
    const val REWARD_FOR_REPORT = 15L
    const val REWARD_FOR_VOTE = 5L
}