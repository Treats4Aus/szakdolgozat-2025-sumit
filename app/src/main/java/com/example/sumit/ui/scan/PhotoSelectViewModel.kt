package com.example.sumit.ui.scan

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.sumit.data.photos.PhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhotoSelectViewModel(
    private val photosRepository: PhotosRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSelectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                useCamera = checkNotNull(savedStateHandle[PhotoSelectDestination.selectModeArg])
            )
        }
    }

    fun addPhoto(photoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.plus(photoUri)
            )
        }
    }

    fun savePhotosToTemp() {
        photosRepository.movePhotosToTemp(_uiState.value.photos)
    }
}

data class PhotoSelectUiState(
    val photos: List<Uri> = listOf(),
    val useCamera: Boolean = false
)
