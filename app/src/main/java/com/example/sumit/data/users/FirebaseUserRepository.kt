package com.example.sumit.data.users

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

private const val USER_COLLECTION_NAME = "users"
private const val FRIENDSHIP_COLLECTION_NAME = "friendships"

@OptIn(ExperimentalCoroutinesApi::class)
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

    override fun getUserData(firebaseId: String): Flow<UserData?> {
        val documentRef = store.collection(USER_COLLECTION_NAME).document(firebaseId)
        return documentRef.dataObjects<UserData>()
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun validateEmail(email: String): Boolean {
        val collection = store.collection(USER_COLLECTION_NAME)
        val emailFieldName = "email"

        val query = collection.whereEqualTo(emailFieldName, email).count()
        val result = query.get(AggregateSource.SERVER).await()

        return result.count == 1L
    }

    override fun getUserFriends(firebaseId: String): Flow<List<FriendData>> {
        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val requesterFieldName = "requesterId"
        val responderFieldName = "responderId"
        val statusFieldName = "status"

        val userFriendshipsQuery = friendshipCollection.where(
            Filter.and(
                Filter.or(
                    Filter.equalTo(requesterFieldName, firebaseId),
                    Filter.equalTo(responderFieldName, firebaseId)
                ),
                Filter.equalTo(statusFieldName, FriendshipStatus.Accepted.toString())
            )
        )
        val userFriendships = userFriendshipsQuery.dataObjects<FriendshipData>()

        return userFriendships.flatMapLatest { friendships ->
            val friendFlows = friendships.map { friendship ->
                val friendUserId = if (friendship.requesterId == firebaseId) {
                    friendship.responderId
                } else {
                    friendship.requesterId
                }
                getUserData(friendUserId).map { userData ->
                    userData?.let { FriendData(friendship, it) }
                }
            }

            combine(friendFlows) { friends ->
                friends.mapNotNull { it }.toList()
            }
        }
    }
}
