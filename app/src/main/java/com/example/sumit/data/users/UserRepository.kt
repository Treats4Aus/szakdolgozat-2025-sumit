package com.example.sumit.data.users

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Handles users in the application.
 */
interface UserRepository {
    /**
     * The currently logged in user, or `null` if no one is logged in.
     */
    val currentUser: Flow<FirebaseUser?>

    /**
     * Signs a user in by providing an email and password with a valid registration.
     * @param email The email address of the user
     * @param password The password for the account
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String)

    /**
     * Creates a new account with the provided email and password.
     * @param email The email address of the user
     * @param password The password entered by the user
     * @return The uid of the newly created user, or `null` if the operation was unsuccessful
     */
    suspend fun registerWithEmailAndPassword(email: String, password: String): String?

    /**
     * Re-authenticates the user and changes the account password.
     * @param email The email address of the user
     * @param currentPassword The current password for the account
     * @param newPassword The new password entered by the user
     */
    suspend fun changePassword(email: String, currentPassword: String, newPassword: String)

    /**
     * Creates a new user record in the remote database.
     * @param firebaseId The uid of the user
     * @param email The email address of the user
     * @param name The name of the user
     * @param username The username of the user
     */
    suspend fun createUserData(firebaseId: String, email: String, name: String, username: String)

    /**
     * Gets the user data for a selected account.
     * @param firebaseId The uid of the requested user
     * @return Flow of user data, or `null` if no account was found
     */
    fun getUserData(firebaseId: String): Flow<UserData?>

    /**
     * Signs out the current user and destroys the session.
     */
    fun signOut()

    /**
     * Checks if a user with an email address exists in the remote database.
     * @param email The email address to find
     * @return `true` if a user with the email address exists, `false` otherwise
     */
    suspend fun validateEmail(email: String): Boolean

    /**
     * Checks that status of a relation between two users.
     * @param firebaseId The uid of the current user
     * @param email The email address of the user to query
     * @return The status of the relation between the users, or `null` if there is none
     */
    suspend fun checkFriendshipStatus(firebaseId: String, email: String): FriendshipStatus?

    /**
     * Gets every user that has an accepted friend status.
     * @param firebaseId The uid of the current user
     * @return Flow of friendships that contain the user's data as well
     */
    fun getUserFriends(firebaseId: String): Flow<List<FriendData>>

    /**
     * Sends a new friend request.
     * @param firebaseId The uid of the current user
     * @param email The email address of the user to send the request to
     */
    suspend fun sendFriendRequest(firebaseId: String, email: String)

    /**
     * Accept a pending friend request.
     * @param friendshipData The friendship to modify
     */
    suspend fun acceptFriendRequest(friendshipData: FriendshipData)

    /**
     * Reject a pending friend request.
     * @param friendshipData The friendship to modify
     */
    suspend fun rejectFriendRequest(friendshipData: FriendshipData)

    /**
     * Block a user.
     * @param friendshipData The friendship to modify
     */
    suspend fun blockFriend(friendshipData: FriendshipData)

    /**
     * Assign a new FCM token to the current user.
     * @param token The new token to add
     */
    suspend fun addDeviceToken(token: String)
}
