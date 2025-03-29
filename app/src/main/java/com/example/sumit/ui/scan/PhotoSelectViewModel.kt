package com.example.sumit.ui.scan

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhotoSelectViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSelectUiState())
    val uiState = _uiState.asStateFlow()

    private val useCamera: Boolean =
        checkNotNull(savedStateHandle[PhotoSelectDestination.selectModeArg])

    fun addPhoto(photo: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.plus(photo)
            )
        }
    }
}

data class PhotoSelectUiState(val photos: List<Bitmap> = listOf())
