package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

/**
 * Handles interaction with a remote storage regarding notes.
 */
interface RemoteNotesRepository {
    /**
     * Gets every note for the selected user from remote storage.
     * @param firebaseId The uid of the user
     * @return Flow of every note the user owns
     */
    fun getUserNotes(firebaseId: String): Flow<List<RemoteNote>>

    /**
     * Gets every note that is shared with the user.
     * @param firebaseId The uid of the user
     * @return Flow of every shared note
     */
    fun getUserSharedNotes(firebaseId: String): Flow<List<RemoteNote>>

    /**
     * Gets a selected note from remote storage.
     * @param id The unique identifier of the note
     * @return The flow of the corresponding note, emits `null` if no note was found
     */
    fun getNote(id: String): Flow<RemoteNote?>

    /**
     * Uploads a note to remote storage.
     * @param firebaseId The uid of the user
     * @param localNote The locally stored note
     * @return The unique identifier of the newly created note
     */
    suspend fun uploadNote(firebaseId: String, localNote: Note): String

    /**
     * Updates a remotely stored note.
     * @param remoteNote The note to overwrite the current record with
     */
    suspend fun updateNote(remoteNote: RemoteNote)

    /**
     * Deletes a remotely stored note.
     * @param id The unique identifier of the note to be deleted
     */
    suspend fun deleteNote(id: String)

    /**
     * Start the periodic background work of uploading and downloading notes between local and
     * remote storage.
     */
    fun startSync()

    /**
     * Cancel the periodic background work of synchronizing notes.
     */
    fun cancelSync()
}
