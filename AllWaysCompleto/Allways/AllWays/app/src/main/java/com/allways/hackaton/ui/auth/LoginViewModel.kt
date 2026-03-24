package com.allways.hackaton.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val walletAddress: String? = null
)

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
        }
    }

    fun connectSolanaWallet() {
        _uiState.value = _uiState.value.copy(walletAddress = "DEMO_WALLET_ADDRESS")
    }

    fun continueAsGuest() {
        _uiState.value = _uiState.value.copy(isLoggedIn = false)
    }
}