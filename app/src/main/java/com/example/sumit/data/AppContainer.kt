package com.example.sumit.data

import android.content.Context
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.notes.OfflineNotesRepository

interface AppContainer {
    val notesRepository: NotesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        OfflineNotesRepository()
    }
}
