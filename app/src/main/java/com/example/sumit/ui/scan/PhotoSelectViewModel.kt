package com.example.sumit.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.OUTPUT_PATH
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

private const val TAG = "PhotoSelectViewModel"

class PhotoSelectViewModel(
    private val photosRepository: PhotosRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoSelectUiState())
    val uiState = _uiState.asStateFlow()

    val imageCropper = ImageCropper()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                useCamera = checkNotNull(savedStateHandle[PhotoSelectDestination.selectModeArg])
            )
        }

        viewModelScope.launch {
            photosRepository.movePhotosWorkData.collect { infos ->
                if (infos.all { it.state == WorkInfo.State.SUCCEEDED }
                    && _uiState.value.savePhotosState == SavePhotosState.Loading) {
                    setPhotoState(SavePhotosState.Complete)
                }
            }
        }
    }

    fun addPhoto(photoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.plus(Photo(uri = photoUri))
            )
        }
    }

    fun addPhotoFromCamera() = addPhoto(_uiState.value.cameraPhotoUri)

    fun cropPhoto(index: Int, context: Context) {
        viewModelScope.launch {
            val result = imageCropper.crop(_uiState.value.photos[index].uri, context)
            if (result is CropResult.Success) {
                val croppedPhotoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    context.createImageFile()
                )
                val resolver = context.contentResolver
                val stream = resolver.openOutputStream(croppedPhotoUri)
                if (stream != null) {
                    result.bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 0, stream)
                }
                stream?.close()
                updatePhoto(index, croppedPhotoUri)
            }
        }
    }

    private fun updatePhoto(index: Int, photoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    set(index, Photo(currentState.photos[index].id, photoUri))
                }
            )
        }
    }

    fun removePhoto(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    removeAt(index)
                },
                selectedPhotoIndex = null
            )
        }
    }

    fun movePhoto(from: LazyGridItemInfo, to: LazyGridItemInfo) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            )
        }
    }

    fun savePhotosToTemp() {
        setPhotoState(SavePhotosState.Loading)
        photosRepository.movePhotosToTemp(_uiState.value.photos.map { it.uri })
    }

    fun resetSavePhotoState() = setPhotoState(SavePhotosState.Default)

    private fun setPhotoState(state: SavePhotosState) {
        _uiState.update { currentState ->
            currentState.copy(
                savePhotosState = state
            )
        }
    }

    fun updateCameraPhotoUri(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraPhotoUri = uri
            )
        }
    }

    fun disableDefaultLaunch() {
        _uiState.update { currentState ->
            currentState.copy(
                useCamera = null
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

fun Context.createImageFile(): File {
    val name = String.format("temp-photo-%s", UUID.randomUUID().toString())
    val extension = ".png"
    val outputDir = File(cacheDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val image = File.createTempFile(name, extension, outputDir)
    return image
}

data class PhotoSelectUiState(
    val photos: List<Photo> = listOf(),
    val cameraPhotoUri: Uri = Uri.EMPTY,
    val useCamera: Boolean? = false,
    val selectedPhotoIndex: Int? = null,
    val savePhotosState: SavePhotosState = SavePhotosState.Default
)

data class Photo(
    val id: UUID = UUID.randomUUID(),
    val uri: Uri
)

enum class SavePhotosState {
    Default,
    Loading,
    Complete
}
