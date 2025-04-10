package com.example.sumit.ui.scan

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_SEGMENTED_BITMAP
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
                    val bitmap = it.outputData.keyValueMap[KEY_SEGMENTED_BITMAP] as Bitmap?

                    Log.d(TAG, "index: $index isNull: ${bitmap == null}")

                    if (it.state.isFinished && bitmap != null) {
                        addSegmentedBitmap(index, bitmap)
                    }
                }
            }
        }
    }

    private fun addSegmentedBitmap(index: Int, segmentedPhoto: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    set(
                        index,
                        SegmentedPhoto(segmentedPhoto, currentState.photos[index].original)
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
    val segmented: Bitmap? = null,
    val original: Uri
)
