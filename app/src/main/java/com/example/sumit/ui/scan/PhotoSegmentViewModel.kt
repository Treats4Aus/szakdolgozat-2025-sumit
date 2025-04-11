package com.example.sumit.ui.scan

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.KEY_PHOTO_INDEX
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
                    photos = photoUris.map { SegmentedPhoto(original = it) }
                )
            }
            photosRepository.startSegmentation(photoUris)
            photosRepository.segmentPhotosWorkData.collect { infos ->
                infos.forEach {
                    val index = it.outputData.getInt(KEY_PHOTO_INDEX, 0)
                    val uri = it.outputData.getString(KEY_PHOTO_URI)

                    Log.d(TAG, "index: $index isNull: ${uri == null}")

                    if (it.state.isFinished && uri != null) {
                        addSegmentedBitmap(index, Uri.parse(uri))
                    }
                }
            }
        }
    }

    private fun addSegmentedBitmap(index: Int, segmentedPhotoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    set(
                        index,
                        SegmentedPhoto(segmentedPhotoUri, currentState.photos[index].original)
                    )
                }
            )
        }
    }
}

data class PhotoSegmentUiState(
    val photos: List<SegmentedPhoto> = listOf()
)

data class SegmentedPhoto(
    val segmented: Uri? = null,
    val original: Uri
)
