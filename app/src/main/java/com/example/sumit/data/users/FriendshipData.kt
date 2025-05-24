package com.example.sumit.data.users

/**
 * Represents a relation between two users. Describes the document stored in Firestore.
 */
data class FriendshipData(
    /**
     * Unique identifier that is also the id of the Firebase document.
     */
    val id: String = "",

    /**
     * The user id of the person that sent the request.
     */
    val requesterId: String = "",

    /**
     * The user id of the person that the request was sent to.
     */
    val responderId: String = "",

    /**
     * The date the request was sent represented as a Long value.
     */
    val requestTime: Long = 0L,

    /**
     * The current state of the relation between the two user.
     */
    val status: String = FriendshipStatus.Pending.toString()
)

/**
 * The state of the friendship.
 */
enum class FriendshipStatus {
    Pending,
    Accepted,
    Blocked
}
