package com.allways.hackaton.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.allways.hackaton.data.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    init { loadPopularPlaces() }

    private fun loadPopularPlaces() = viewModelScope.launch {
        try {
            val snapshot = db.collection("places").limit(10).get().await()
            _places.value = snapshot.documents.map { doc ->
                Place(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    address = doc.getString("address") ?: "",
                    indications = doc.getString("indications") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun search(query: String) = viewModelScope.launch {
        try {
            if (query.isBlank()) { loadPopularPlaces(); return@launch }
            val snapshot = db.collection("places").get().await()
            _places.value = snapshot.documents.map { doc ->
                Place(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    address = doc.getString("address") ?: "",
                    indications = doc.getString("indications") ?: ""
                )
            }.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}