package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

interface RemoteNotesRepository {
    fun getUserNotes(firebaseId: String): Flow<List<RemoteNote>>

    fun getUserSharedNotes(firebaseId: String): Flow<List<RemoteNote>>

    fun getNote(id: String): Flow<RemoteNote?>

    suspend fun uploadNote(firebaseId: String, localNote: Note): String

    suspend fun updateNote(remoteNote: RemoteNote)

    suspend fun deleteNote(id: String)

    fun startSync()

    fun cancelSync()
}
