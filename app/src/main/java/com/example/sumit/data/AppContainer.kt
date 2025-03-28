package com.example.sumit.data

import android.content.Context
import com.example.sumit.data.notes.LocalNotesRepository
import com.example.sumit.data.notes.NotesRepository

interface AppContainer {
    val notesRepository: NotesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        LocalNotesRepository(SumItDatabase.getDatabase(context).noteDao())
    }
}
