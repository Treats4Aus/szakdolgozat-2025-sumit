package com.example.sumit.data.users

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUser: Flow<FirebaseUser?>

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun registerWithEmailAndPassword(email: String, password: String)

    suspend fun changePassword(email: String, currentPassword: String, newPassword: String)

    suspend fun createUserData(firebaseId: String, email: String, name: String, username: String)

    fun getUserData(firebaseId: String): Flow<UserData?>

    fun signOut()

    suspend fun validateEmail(email: String): Boolean

    fun getUserFriends(firebaseId: String): Flow<List<FriendData>>
}
