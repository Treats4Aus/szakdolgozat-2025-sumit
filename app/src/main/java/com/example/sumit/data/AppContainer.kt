package com.example.sumit.data

import android.content.Context
import com.example.sumit.data.notes.LocalNotesRepository
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.data.photos.WorkManagerPhotosRepository

interface AppContainer {
    val notesRepository: NotesRepository

    val photosRepository: PhotosRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        LocalNotesRepository(SumItDatabase.getDatabase(context).noteDao())
    }

    override val photosRepository: PhotosRepository by lazy {
        WorkManagerPhotosRepository(context)
    }
}
