package com.example.sumit.data.users

/**
 * Represents a user in Firestore.
 */
data class UserData(
    /**
     * Unique identifier that is also the id of the Firestore document.
     */
    val id: String = "",

    /**
     * The email address of the user provided at registration. Unique to the user.
     */
    val email: String = "",

    /**
     * The full name of the user provided at registration.
     */
    val name: String = "",

    /**
     * The username provided at registration.
     */
    val username: String = "",

    /**
     * List of FCM tokens belonging to the user's devices.
     */
    val deviceTokens: List<String> = emptyList()
)
