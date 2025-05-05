package com.example.sumit.data.users

data class FriendshipData(
    val requesterId: String,
    val responderId: String,
    val requestTime: Long,
    val status: String
)

enum class FriendshipStatus {
    Pending,
    Accepted,
    Rejected,
    Blocked
}
