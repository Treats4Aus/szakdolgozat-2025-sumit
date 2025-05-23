package com.example.sumit.data.users

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUser: Flow<FirebaseUser?>

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun registerWithEmailAndPassword(email: String, password: String): String?

    suspend fun changePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun createUserData(firebaseId: String, email: String, name: String, username: String)

    fun getUserData(firebaseId: String): Flow<UserData?>

    fun signOut()

    suspend fun validateEmail(email: String): Boolean

    suspend fun checkFriendshipStatus(firebaseId: String, email: String): FriendshipStatus?

    fun getUserFriends(firebaseId: String): Flow<List<FriendData>>

    suspend fun sendFriendRequest(firebaseId: String, email: String)

    suspend fun acceptFriendRequest(friendshipData: FriendshipData)

    suspend fun rejectFriendRequest(friendshipData: FriendshipData)

    suspend fun blockFriend(friendshipData: FriendshipData)

    suspend fun addDeviceToken(token: String)
}
