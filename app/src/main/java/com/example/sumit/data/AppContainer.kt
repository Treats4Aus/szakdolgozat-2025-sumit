package com.example.sumit.data

import android.content.Context
import com.example.sumit.data.notes.LocalNotesRepository
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.data.photos.WorkManagerPhotosRepository
import com.example.sumit.data.translations.LocalTranslationsRepository
import com.example.sumit.data.translations.TranslationsRepository
import com.example.sumit.data.users.FirebaseUserRepository
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.InferenceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AppContainer {
    val notesRepository: NotesRepository

    val photosRepository: PhotosRepository

    val userRepository: UserRepository

    val translationsRepository: TranslationsRepository

    val inferenceModel: InferenceModel
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        LocalNotesRepository(SumItDatabase.getDatabase(context).noteDao())
    }

    override val photosRepository: PhotosRepository by lazy {
        WorkManagerPhotosRepository(context)
    }

    override val userRepository: UserRepository by lazy {
        val auth = Firebase.auth
        FirebaseUserRepository(auth)
    }

    override val translationsRepository: TranslationsRepository by lazy {
        LocalTranslationsRepository(context)
    }

    override val inferenceModel: InferenceModel by lazy {
        InferenceModel.getInstance(context)
    }
}
