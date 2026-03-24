// app/src/main/java/com/allways/hackaton/ui/addinfo/AddInfoViewModel.kt
package com.allways.hackaton.ui.addinfo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.allways.hackaton.data.model.AccessibilityInfo
import com.allways.hackaton.data.repository.StorageRepository
import com.allways.hackaton.solana.models.RewardResponse
import com.allways.hackaton.solana.services.SolanaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

// ============================================
// ESTADO DE RECOMPENSA SOLANA
// ============================================
sealed class RewardStatus {
    object IDLE : RewardStatus()
    object LOADING : RewardStatus()
    data class SUCCESS(val message: String, val amount: Long) : RewardStatus()
    data class ERROR(val message: String) : RewardStatus()
}

// ============================================
// ESTADO DE LA UI
// ============================================
data class AddInfoUiState(
    val answers: Map<String, Boolean> = emptyMap(),
    val comment: String = "",
    val photoUri: Uri? = null,
    val isLoading: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null,
    val rewardMessage: String? = null,
    val rewardAmount: Long = 0L,
    val rewardStatus: RewardStatus = RewardStatus.IDLE
)

// ============================================
// VIEW MODEL
// ============================================
class AddInfoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = StorageRepository()
    private val solanaService = SolanaService()

    private val _uiState = MutableStateFlow(AddInfoUiState())
    val uiState: StateFlow<AddInfoUiState> = _uiState

    fun setAnswer(key: String, value: Boolean) {
        val updated = _uiState.value.answers.toMutableMap()
        updated[key] = value
        _uiState.value = _uiState.value.copy(answers = updated)
    }

    fun setComment(text: String) {
        _uiState.value = _uiState.value.copy(comment = text)
    }

    fun setPhotoUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(photoUri = uri)
    }

    fun resetState() {
        _uiState.value = AddInfoUiState()
    }

    fun submit(placeId: String, context: Context) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            rewardMessage = null,
            rewardStatus = RewardStatus.LOADING
        )
        try {
            // 1. Subir foto a Firebase Storage (si existe)
            val photoUrl = _uiState.value.photoUri?.let {
                storage.uploadImage(it, context)
            } ?: ""

            val answers = _uiState.value.answers
            val infoId = UUID.randomUUID().toString()

            // 2. Crear objeto de información de accesibilidad
            val info = AccessibilityInfo(
                id = infoId,
                placeId = placeId,
                userId = auth.currentUser?.uid ?: "guest",
                hasBraille = answers["hasBraille"],
                hasCaneBumps = answers["hasCaneBumps"],
                hasAssistanceButton = answers["hasAssistanceButton"],
                hasWheelchairRamp = answers["hasWheelchairRamp"],
                hasAccessibleParking = answers["hasAccessibleParking"],
                hasAccessibleBathroom = answers["hasAccessibleBathroom"],
                hasAudioSignals = answers["hasAudioSignals"],
                comment = _uiState.value.comment,
                photoUrl = photoUrl
            )

            // 3. Guardar en Firestore
            db.collection("accessibility_info").document(info.id).set(info).await()

            // 4. Enviar recompensa Solana
            val userPublicKey = auth.currentUser?.uid ?: "guest"

            solanaService.sendAccessibilityReward(userPublicKey, placeId, infoId)
                .onSuccess { response: RewardResponse ->
                    println("✅ Recompensa enviada: ${response.transactionHash}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitted = true,
                        rewardMessage = "¡Ganaste ${response.amount} tokens Solana! 🎉",
                        rewardAmount = response.amount,
                        rewardStatus = RewardStatus.SUCCESS(
                            message = "¡Ganaste ${response.amount} tokens Solana! 🎉",
                            amount = response.amount
                        )
                    )
                }
                .onFailure { error: Throwable ->
                    // Información guardada pero error en Solana
                    println("⚠️ Error en Solana: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitted = true,
                        rewardMessage = "Información guardada correctamente, pero hubo un error al procesar los tokens: ${error.message}",
                        rewardStatus = RewardStatus.ERROR(
                            message = "Error al enviar tokens: ${error.message ?: "Error desconocido"}"
                        )
                    )
                }

        } catch (e: Exception) {
            println("❌ Error general: ${e.message}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                submitted = false,
                error = e.message ?: "Error desconocido al guardar información",
                rewardStatus = RewardStatus.ERROR(
                    message = e.message ?: "Error desconocido"
                )
            )
        }
    }
}