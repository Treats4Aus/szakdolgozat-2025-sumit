package com.example.sumit.data.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(private val auth: FirebaseAuth) : UserRepository {
    override val currentUser: FirebaseUser? = auth.currentUser

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}
