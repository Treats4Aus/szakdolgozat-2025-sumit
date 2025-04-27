package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesStream(): Flow<List<Note>>

    fun getRecentNotesStream(): Flow<List<Note>>

    fun getNoteStream(id: Int): Flow<Note>

    suspend fun addNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)
}
