package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesStream(): Flow<List<Note>>

    fun getNoteStream(id: Int): Flow<Note>
}
