package com.example.sumit.data.notes

import com.example.sumit.utils.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OfflineNotesRepository : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = flowOf(DataSource.notes)

    override fun getRecentNotesStream(): Flow<List<Note>> = flowOf(DataSource.notes.take(3))

    override fun getNoteStream(id: Int): Flow<Note> = flowOf(DataSource.notes[id])

    override suspend fun addNote(note: Note) {}

    override suspend fun updateNote(note: Note) {}

    override suspend fun deleteNote(note: Note) {}

    override suspend fun clearNotes() {}
}
