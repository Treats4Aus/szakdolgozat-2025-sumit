package com.example.sumit.ui.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoSegmentViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSegmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    photos = photosRepository.getTempPhotos()
                )
            }
        }
    }
}

data class PhotoSegmentUiState(
    val photos: List<Uri> = listOf()
)
