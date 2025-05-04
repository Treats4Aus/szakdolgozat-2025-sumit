package com.example.sumit.data.users

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

private const val USER_COLLECTION_NAME = "users"

class FirebaseUserRepository(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) : UserRepository {
    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { state ->
            trySend(state.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.onStart { emit(auth.currentUser) }

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

    override suspend fun createUserData(
        firebaseId: String,
        email: String,
        name: String,
        username: String
    ) {
        val collection = store.collection(USER_COLLECTION_NAME)
        val userData = UserData(
            id = firebaseId,
            email = email,
            name = name,
            username = username
        )
        collection.document(firebaseId).set(userData).await()
    }

    override fun getUserData(firebaseId: String): Flow<UserData?> = callbackFlow {
        val documentRef = store.collection(USER_COLLECTION_NAME).document(firebaseId)
        val snapshotListener = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject<UserData>())
            } else {
                trySend(null)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    override fun signOut() {
        auth.signOut()
    }
}
