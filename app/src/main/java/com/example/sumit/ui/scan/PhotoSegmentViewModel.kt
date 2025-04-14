package com.example.sumit.ui.scan

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.KEY_PHOTO_URI
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PhotoSegmentViewModel"

class PhotoSegmentViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSegmentUiState())
    val uiState = _uiState.asStateFlow()

    private var currentAdjustment: Job? = null

    init {
        viewModelScope.launch {
            val photoUris = photosRepository.getTempPhotos()
            _uiState.update { currentState ->
                currentState.copy(
                    photos = photoUris.map { SegmentedPhoto(uri = it, isProcessing = true) }
                )
            }
            photoUris.forEachIndexed { index, photo ->
                launch {
                    startSegmentationForResult(index, photo)
                }
            }
        }
    }

    private suspend fun startSegmentationForResult(index: Int, photoUri: Uri) {
        val workerId = photosRepository.startSegmentation(index, photoUri)
        photosRepository.getSegmentationWorkData(workerId).collect { info ->
            val uri = info.outputData.getString(KEY_PHOTO_URI)

            if (info.state.isFinished && uri != null && _uiState.value.photos[index].isProcessing) {
                setSegmentedPhoto(index, Uri.parse(uri))
            }
        }
    }

    private fun setSegmentedPhoto(index: Int, segmentedPhotoUri: Uri) {
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
                selectedPhotoIndex = null,
                adjustedImage = null
            )
        }
    }

    fun adjustSelectedPhoto(amount: Int) {
        currentAdjustment?.cancel()
        currentAdjustment = viewModelScope.launch {
            setAdjustmentRunning(true)
            val result = photosRepository.adjustBitmap(
                _uiState.value.photos[_uiState.value.selectedPhotoIndex!!].uri,
                amount
            )
            _uiState.update { currentState ->
                currentState.copy(
                    adjustedImage = result
                )
            }
            setAdjustmentRunning(false)
        }
    }

    private fun setAdjustmentRunning(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isAdjustmentRunning = value
            )
        }
    }

    fun saveAdjustedPhoto() {
        val index = _uiState.value.selectedPhotoIndex!!

        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    set(
                        index,
                        currentState.photos[index].copy(isProcessing = true)
                    )
                }
            )
        }
        viewModelScope.launch {
            val adjustedPhotoUri = photosRepository.overrideSegmentedPhoto(
                index,
                _uiState.value.adjustedImage!!
            )
            setSegmentedPhoto(index, adjustedPhotoUri)
            clearSelectedPhoto()
        }
    }
}

data class PhotoSegmentUiState(
    val photos: List<SegmentedPhoto> = listOf(),
    val selectedPhotoIndex: Int? = null,
    val adjustedImage: Bitmap? = null,
    val isAdjustmentRunning: Boolean = false
)

data class SegmentedPhoto(
    val uri: Uri,
    val isProcessing: Boolean = false
)
