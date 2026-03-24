package com.allways.hackaton.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.allways.hackaton.data.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _place = MutableStateFlow<Place?>(null)
    val place: StateFlow<Place?> = _place

    fun loadPlace(placeId: String) = viewModelScope.launch {
        try {
            val snapshot = db.collection("places").document(placeId).get().await()
            if (snapshot.exists()) {
                val place = Place(
                    id = snapshot.id,
                    name = snapshot.getString("name") ?: "",
                    description = snapshot.getString("description") ?: "",
                    address = snapshot.getString("address") ?: "",
                    indications = snapshot.getString("indications") ?: ""
                )
                _place.value = place
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}