package com.allways.hackaton.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.allways.hackaton.data.model.AccessibilityInfo
import com.allways.hackaton.data.model.Review
import com.allways.hackaton.data.repository.SolanaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val solana = SolanaRepository()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _averages = MutableStateFlow<Map<String, Float>>(emptyMap())
    val averages: StateFlow<Map<String, Float>> = _averages

    fun load(placeId: String) = viewModelScope.launch {
        try {
            val snapshot = db.collection("accessibility_info")
                .whereEqualTo("placeId", placeId)
                .get()
                .await()

            val infoList = snapshot.documents.map { doc ->
                AccessibilityInfo(
                    id = doc.id,
                    placeId = doc.getString("placeId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    hasBraille = doc.getBoolean("hasBraille"),
                    hasCaneBumps = doc.getBoolean("hasCaneBumps"),
                    hasAssistanceButton = doc.getBoolean("hasAssistanceButton"),
                    hasWheelchairRamp = doc.getBoolean("hasWheelchairRamp"),
                    hasAccessibleParking = doc.getBoolean("hasAccessibleParking"),
                    hasAccessibleBathroom = doc.getBoolean("hasAccessibleBathroom"),
                    hasAudioSignals = doc.getBoolean("hasAudioSignals"),
                    comment = doc.getString("comment") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: "",
                    upvotes = doc.getLong("upvotes")?.toInt() ?: 0
                )
            }

            _reviews.value = infoList.map { info ->
                android.util.Log.d("Reviews", "info id: ${info.id}, photoUrl: '${info.photoUrl}', comment: '${info.comment}'")
                Review(
                    infoId = info.id,
                    userId = info.userId,
                    userName = if (info.userId == "guest") "Guest" else "User ${info.userId.take(6)}",
                    comment = info.comment,
                    photoUrl = info.photoUrl,
                    upvotes = info.upvotes,
                    userHasUpvoted = false
                )
            }

            val keys = listOf(
                "hasBraille", "hasCaneBumps", "hasAssistanceButton",
                "hasWheelchairRamp", "hasAccessibleParking",
                "hasAccessibleBathroom", "hasAudioSignals"
            )

            val avgMap = mutableMapOf<String, Float>()
            keys.forEach { key ->
                val values = infoList.mapNotNull { info ->
                    when (key) {
                        "hasBraille" -> info.hasBraille
                        "hasCaneBumps" -> info.hasCaneBumps
                        "hasAssistanceButton" -> info.hasAssistanceButton
                        "hasWheelchairRamp" -> info.hasWheelchairRamp
                        "hasAccessibleParking" -> info.hasAccessibleParking
                        "hasAccessibleBathroom" -> info.hasAccessibleBathroom
                        "hasAudioSignals" -> info.hasAudioSignals
                        else -> null
                    }
                }
                avgMap[key] = if (values.isEmpty()) 0f
                else values.count { it }.toFloat() / values.size
            }
            _averages.value = avgMap

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun upvote(infoId: String) = viewModelScope.launch {
        try {
            val ref = db.collection("accessibility_info").document(infoId)
            val snapshot = ref.get().await()
            val current = snapshot.getLong("upvotes")?.toInt() ?: 0
            ref.update("upvotes", current + 1).await()

            // Reward the author with Solana tokens
            val authorId = snapshot.getString("userId") ?: return@launch
            val userSnapshot = db.collection("users").document(authorId).get().await()
            val walletAddress = userSnapshot.getString("walletAddress")
            if (!walletAddress.isNullOrBlank()) {
                solana.rewardUser(walletAddress)
            }

            // Update local state
            _reviews.value = _reviews.value.map { review ->
                if (review.infoId == infoId) review.copy(upvotes = current + 1, userHasUpvoted = true)
                else review
            }
        } catch (e: Exception) {
            // Silently fail for now
        }
    }
}