package com.example.sumit.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sumit.SumItApplication
import com.example.sumit.ui.home.notes.MyNotesViewModel
import com.example.sumit.ui.scan.PhotoSelectViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MyNotesViewModel(
                notesRepository = sumItApplication().container.notesRepository
            )
        }

        initializer {
            PhotoSelectViewModel(
                this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.sumItApplication(): SumItApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SumItApplication)
