package com.allways.hackaton.data.model

data class AccessibilityInfo(
    val id: String = "",
    val placeId: String = "",
    val userId: String = "",
    val hasBraille: Boolean? = null,
    val hasCaneBumps: Boolean? = null,
    val hasAssistanceButton: Boolean? = null,
    val hasWheelchairRamp: Boolean? = null,
    val hasAccessibleParking: Boolean? = null,
    val hasAccessibleBathroom: Boolean? = null,
    val hasAudioSignals: Boolean? = null,
    val comment: String = "",
    val photoUrl: String = "",
    val upvotes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)