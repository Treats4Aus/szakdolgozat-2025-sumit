package com.example.sumit.data.notes

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sumit.utils.SYNC_NOTES_WORK_NAME
import com.example.sumit.workers.SyncNotesWorker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

private const val TAG = "FirebaseNotesRepository"
private const val NOTE_COLLECTION_NAME = "notes"

class FirebaseNotesRepository(
    context: Context,
    private val store: FirebaseFirestore
) : RemoteNotesRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun getUserNotes(firebaseId: String): Flow<List<RemoteNote>> {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        val ownerFieldName = "owner"

        val userNotesQuery = noteCollection.whereEqualTo(ownerFieldName, firebaseId)
        return userNotesQuery.dataObjects<RemoteNote>()
    }

    override fun getUserSharedNotes(firebaseId: String): Flow<List<RemoteNote>> {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        val sharedWithFieldName = "sharedWith"

        val userSharedNotesQuery =
            noteCollection.whereArrayContains(sharedWithFieldName, firebaseId)
        return userSharedNotesQuery.dataObjects<RemoteNote>()
    }

    override fun getNote(id: String): Flow<RemoteNote?> {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        val documentRef = noteCollection.document(id)
        return documentRef.dataObjects<RemoteNote>()
    }

    override suspend fun uploadNote(firebaseId: String, localNote: Note): String {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        val documentRef = noteCollection.document()

        val remoteNote = RemoteNote(
            id = documentRef.id,
            owner = firebaseId,
            created = localNote.created.time,
            lastModified = localNote.lastModified.time,
            title = localNote.title,
            content = localNote.content,
            summary = localNote.summary,
            keywords = emptyList()
        )
        documentRef.set(remoteNote).await()
        return documentRef.id
    }

    override suspend fun updateNote(remoteNote: RemoteNote) {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        noteCollection.document(remoteNote.id).set(remoteNote).await()
    }

    override suspend fun deleteNote(id: String) {
        val noteCollection = store.collection(NOTE_COLLECTION_NAME)
        noteCollection.document(id).delete().await()
    }

    override fun startSync() {
        Log.d(TAG, "Starting sync work")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncNotesWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_NOTES_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            syncWorkRequest
        )
    }

    override fun cancelSync() {
        Log.d(TAG, "Cancelling sync work")

        workManager.cancelUniqueWork(SYNC_NOTES_WORK_NAME)
    }
}