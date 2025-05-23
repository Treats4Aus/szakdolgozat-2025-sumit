package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

/**
 * Handles operations related to locally stored notes.
 */
interface NotesRepository {
    /**
     * Collects locally stored notes and exposes them as a flow.
     * @return Flow of every note
     */
    fun getAllNotesStream(): Flow<List<Note>>

    /**
     * Collects recently created notes and exposes them as a flow.
     * @return Flow of recent notes
     */
    fun getRecentNotesStream(): Flow<List<Note>>

    /**
     * Queries the note with the provided id and exposes it as a flow.
     * @param id The unique identifier of the requested note
     * @return Flow of the note with the provided id
     */
    fun getNoteStream(id: Int): Flow<Note>

    /**
     * Locally stores a note.
     * @param note The note to add
     */
    suspend fun addNote(note: Note)

    /**
     * Updates the note with matching id.
     * @param note The note to overwrite the current record with
     */
    suspend fun updateNote(note: Note)

    /**
     * Deletes a note.
     * @param note The note to delete
     */
    suspend fun deleteNote(note: Note)

    /**
     * Deletes every locally stored note.
     */
    suspend fun clearNotes()
}
