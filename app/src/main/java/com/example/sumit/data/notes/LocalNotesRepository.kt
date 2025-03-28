package com.example.sumit.data.notes

import kotlinx.coroutines.flow.Flow

class LocalNotesRepository(private val noteDao: NoteDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun getNoteStream(id: Int): Flow<Note> = noteDao.getNote(id)
}