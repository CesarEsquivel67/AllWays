package com.allways.hackaton.data.model

data class Review(
    val infoId: String = "",
    val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val photoUrl: String = "",
    val upvotes: Int = 0,
    val userHasUpvoted: Boolean = false
)