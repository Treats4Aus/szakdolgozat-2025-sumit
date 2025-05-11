package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

class LocalNotesRepository(private val noteDao: NoteDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun getRecentNotesStream(): Flow<List<Note>> = noteDao.getRecentNotes()

    override fun getNoteStream(id: Int): Flow<Note> = noteDao.getNote(id)

    override suspend fun addNote(note: Note) = noteDao.insert(note)

    override suspend fun updateNote(note: Note) = noteDao.update(note)

    override suspend fun deleteNote(note: Note) = noteDao.delete(note)

    override suspend fun clearNotes() = noteDao.deleteAll()
}