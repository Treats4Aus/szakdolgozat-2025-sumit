package com.example.sumit.data.users

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) : UserRepository {
    override val currentUser: FirebaseUser? = auth.currentUser

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ) {
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        auth.currentUser?.let {
            it.reauthenticate(credential).await()
            it.updatePassword(newPassword).await()
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
