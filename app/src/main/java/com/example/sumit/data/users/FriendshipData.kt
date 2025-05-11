package com.example.sumit.data.users

data class FriendshipData(
    val id: String = "",
    val requesterId: String = "",
    val responderId: String = "",
    val requestTime: Long = 0L,
    val status: String = FriendshipStatus.Pending.toString()
)

enum class FriendshipStatus {
    Pending,
    Accepted,
    Blocked
}
