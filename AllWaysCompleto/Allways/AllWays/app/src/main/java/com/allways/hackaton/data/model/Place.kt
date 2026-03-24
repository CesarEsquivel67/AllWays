package com.allways.hackaton.data.model

data class Place(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val indications: String = "",
    val averageRatings: Map<String, Float> = emptyMap()
)