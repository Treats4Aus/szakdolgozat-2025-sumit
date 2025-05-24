package com.example.sumit.data.users

/**
 * Represents a user with an active friendship.
 */
data class FriendData(
    /**
     * References the friendship data between the two users.
     */
    val friendshipData: FriendshipData,

    /**
     * The user data of the other user.
     */
    val userData: UserData
)
