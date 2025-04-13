package com.example.sumit.ui.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.KEY_PHOTO_URI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PhotoSegmentViewModel"

class PhotoSegmentViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSegmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val photoUris = photosRepository.getTempPhotos()
            _uiState.update { currentState ->
                currentState.copy(
                    photos = photoUris.map { SegmentedPhoto(uri = it, isProcessing = true) }
                )
            }
            photoUris.forEachIndexed { index, photo ->
                val workerId = photosRepository.startSegmentation(index, photo)
                launch {
                    photosRepository.getSegmentationWorkData(workerId).collect { info ->
                        val uri = info.outputData.getString(KEY_PHOTO_URI)

                        if (info.state.isFinished && uri != null && _uiState.value.photos[index].isProcessing) {
                            addSegmentedBitmap(index, Uri.parse(uri))
                        }
                    }
                }
            }
        }
    }

    private fun addSegmentedBitmap(index: Int, segmentedPhotoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    set(index, SegmentedPhoto(uri = segmentedPhotoUri, isProcessing = false))
                }
            )
        }
    }

    fun selectPhoto(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPhotoIndex = index
            )
        }
    }

    fun clearSelectedPhoto() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPhotoIndex = null
            )
        }
    }
}

data class PhotoSegmentUiState(
    val photos: List<SegmentedPhoto> = listOf(),
    val selectedPhotoIndex: Int? = null
)

data class SegmentedPhoto(
    val uri: Uri,
    val isProcessing: Boolean = false
)
