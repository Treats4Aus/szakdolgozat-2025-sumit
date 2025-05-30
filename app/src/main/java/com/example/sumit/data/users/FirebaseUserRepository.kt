package com.example.sumit.data.users

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.util.Date

private const val USER_COLLECTION_NAME = "users"
private const val FRIENDSHIP_COLLECTION_NAME = "friendships"

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseUserRepository(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val messaging: FirebaseMessaging
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

        val deviceToken = messaging.token.await()
        addDeviceToken(deviceToken)
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): String? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid
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

    override suspend fun checkFriendshipStatus(
        firebaseId: String,
        email: String
    ): FriendshipStatus? {
        val userCollection = store.collection(USER_COLLECTION_NAME)
        val emailFieldName = "email"

        val userQuery = userCollection.whereEqualTo(emailFieldName, email).limit(1L)
        val user = userQuery.get().await().toObjects<UserData>().firstOrNull()
            ?: throw Exception("User with specified email does not exist")

        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val requesterFieldName = "requesterId"
        val responderFieldName = "responderId"

        val friendshipQuery = friendshipCollection
            .where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo(requesterFieldName, firebaseId),
                        Filter.equalTo(responderFieldName, user.id)
                    ),
                    Filter.and(
                        Filter.equalTo(requesterFieldName, user.id),
                        Filter.equalTo(responderFieldName, firebaseId)
                    )
                )
            )
            .limit(1L)
        val friendship = friendshipQuery.get().await().toObjects<FriendshipData>().firstOrNull()

        return friendship?.status?.let { FriendshipStatus.valueOf(it) }
    }

    override fun getUserFriends(firebaseId: String): Flow<List<FriendData>> {
        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val requesterFieldName = "requesterId"
        val responderFieldName = "responderId"

        val userFriendshipsQuery = friendshipCollection.where(
            Filter.or(
                Filter.equalTo(requesterFieldName, firebaseId),
                Filter.equalTo(responderFieldName, firebaseId)
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

    override suspend fun sendFriendRequest(firebaseId: String, email: String) {
        val userCollection = store.collection(USER_COLLECTION_NAME)
        val emailFieldName = "email"

        val userQuery = userCollection.whereEqualTo(emailFieldName, email).limit(1L)
        val user = userQuery.get().await().toObjects(UserData::class.java).firstOrNull()
            ?: throw Exception("User with specified email does not exist")

        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val documentRef = friendshipCollection.document()

        val friendshipData = FriendshipData(
            id = documentRef.id,
            requesterId = firebaseId,
            responderId = user.id,
            requestTime = Date().time,
            status = FriendshipStatus.Pending.toString()
        )
        documentRef.set(friendshipData).await()
    }

    override suspend fun acceptFriendRequest(friendshipData: FriendshipData) {
        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val documentRef = friendshipCollection.document(friendshipData.id)
        documentRef.set(friendshipData.copy(status = FriendshipStatus.Accepted.toString())).await()
    }

    override suspend fun rejectFriendRequest(friendshipData: FriendshipData) {
        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val documentRef = friendshipCollection.document(friendshipData.id)
        documentRef.delete().await()
    }

    override suspend fun blockFriend(friendshipData: FriendshipData) {
        val friendshipCollection = store.collection(FRIENDSHIP_COLLECTION_NAME)
        val documentRef = friendshipCollection.document(friendshipData.id)
        documentRef.set(friendshipData.copy(status = FriendshipStatus.Blocked.toString())).await()
    }

    override suspend fun addDeviceToken(token: String) {
        val collection = store.collection(USER_COLLECTION_NAME)
        val currentUser = currentUser.first() ?: return

        val documentRef = collection.document(currentUser.uid)
        val user = documentRef.get().await().toObject<UserData>() ?: return

        val updatedDeviceTokens = user.deviceTokens.toMutableSet()
        updatedDeviceTokens.add(token)

        val updatedUser = user.copy(
            deviceTokens = updatedDeviceTokens.toList()
        )
        documentRef.set(updatedUser).await()
    }
}
