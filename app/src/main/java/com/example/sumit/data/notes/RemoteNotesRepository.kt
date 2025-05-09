package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

interface RemoteNotesRepository {
    fun getUserNotes(firebaseId: String): Flow<List<RemoteNote>>

    suspend fun uploadNote(firebaseId: String, localNote: Note)

    suspend fun updateNote(remoteNote: RemoteNote)

    fun startSync()

    fun cancelSync()
}
