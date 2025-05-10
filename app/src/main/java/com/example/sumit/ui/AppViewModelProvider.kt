package com.example.sumit.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sumit.SumItApplication
import com.example.sumit.ui.home.notes.MyNotesViewModel
import com.example.sumit.ui.home.profile.ProfileViewModel
import com.example.sumit.ui.home.profile.RegistrationViewModel
import com.example.sumit.ui.home.recent.RecentNotesViewModel
import com.example.sumit.ui.notes.EditNoteViewModel
import com.example.sumit.ui.notes.ViewNoteViewModel
import com.example.sumit.ui.scan.PhotoProcessViewModel
import com.example.sumit.ui.scan.PhotoSegmentViewModel
import com.example.sumit.ui.scan.PhotoSelectViewModel
import com.example.sumit.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            RecentNotesViewModel(
                notesRepository = sumItApplication().container.notesRepository
            )
        }

        initializer {
            MyNotesViewModel(
                notesRepository = sumItApplication().container.notesRepository
            )
        }

        initializer {
            ProfileViewModel(
                userRepository = sumItApplication().container.userRepository,
                translationsRepository = sumItApplication().container.translationsRepository
            )
        }

        initializer {
            RegistrationViewModel(
                userRepository = sumItApplication().container.userRepository,
                translationsRepository = sumItApplication().container.translationsRepository
            )
        }

        initializer {
            SettingsViewModel(
                preferencesRepository = sumItApplication().container.preferencesRepository,
                remoteNotesRepository = sumItApplication().container.remoteNotesRepository
            )
        }

        initializer {
            PhotoSelectViewModel(
                photosRepository = sumItApplication().container.photosRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }

        initializer {
            PhotoSegmentViewModel(
                photosRepository = sumItApplication().container.photosRepository
            )
        }

        initializer {
            PhotoProcessViewModel(
                photosRepository = sumItApplication().container.photosRepository,
                notesRepository = sumItApplication().container.notesRepository
            )
        }

        initializer {
            ViewNoteViewModel(
                notesRepository = sumItApplication().container.notesRepository,
                remoteNotesRepository = sumItApplication().container.remoteNotesRepository,
                userRepository = sumItApplication().container.userRepository,
                translationsRepository = sumItApplication().container.translationsRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }

        initializer {
            EditNoteViewModel(
                notesRepository = sumItApplication().container.notesRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.sumItApplication(): SumItApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SumItApplication)
