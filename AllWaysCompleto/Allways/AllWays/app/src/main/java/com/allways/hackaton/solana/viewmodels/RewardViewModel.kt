package com.allways.hackaton.solana.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allways.hackaton.solana.models.RewardResponse
import com.allways.hackaton.solana.services.SolanaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RewardUiState(
    val tokenBalance: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class RewardViewModel : ViewModel() {
    private val solanaService = SolanaService()

    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState

    fun submitAccessibilityInfo(
        recipientPublicKey: String,
        placeId: String,
        infoId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            solanaService.sendAccessibilityReward(recipientPublicKey, placeId, infoId)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "¡Ganaste ${response.amount} tokens! 🎉",
                        tokenBalance = _uiState.value.tokenBalance + response.amount
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun submitReport(
        recipientPublicKey: String,
        reportId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            solanaService.sendReportReward(recipientPublicKey, reportId)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "¡Ganaste ${response.amount} tokens! 🎉",
                        tokenBalance = _uiState.value.tokenBalance + response.amount
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error desconocido"
                    )
                }
        }
    }
}