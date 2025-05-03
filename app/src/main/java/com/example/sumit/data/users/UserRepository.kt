package com.example.sumit.data.users

import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    val currentUser: FirebaseUser?

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun registerWithEmailAndPassword(email: String, password: String)

    fun signOut()
}
