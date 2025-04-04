package com.example.sumit.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.photos.PhotosRepository
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

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
    }

    fun addPhoto(photoUri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.plus(photoUri)
            )
        }
    }

    fun addPhotoFromCamera() = addPhoto(_uiState.value.cameraPhotoUri)

    fun cropPhoto(index: Int, context: Context) {
        viewModelScope.launch {
            val result = imageCropper.crop(_uiState.value.photos[index], context)
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
                photos = currentState.photos.slice(0..<index).plus(photoUri)
                        + currentState.photos.slice((index + 1)..<(currentState.photos.size))
            )
        }
    }

    fun removePhoto(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                photos = currentState.photos.slice(0..<index)
                        + currentState.photos.slice((index + 1)..<(currentState.photos.size))
            )
        }
    }

    fun savePhotosToTemp() {
        photosRepository.movePhotosToTemp(_uiState.value.photos)
    }

    fun checkCameraPermission(
        context: Context,
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
    ) {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            launchCamera(context, cameraLauncher)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun launchCamera(context: Context, launcher: ManagedActivityResultLauncher<Uri, Boolean>) {
        _uiState.update { currentState ->
            currentState.copy(
                cameraPhotoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    context.createImageFile()
                )
            )
        }
        launcher.launch(_uiState.value.cameraPhotoUri)
    }

    fun disableDefaultLaunch() {
        _uiState.update { currentState ->
            currentState.copy(
                useCamera = null
            )
        }
    }
}

fun Context.createImageFile(): File {
    val name = String.format("temp-photo-%s", UUID.randomUUID().toString())
    val extension = ".png"
    val image = File.createTempFile(name, extension, cacheDir)
    return image
}

data class PhotoSelectUiState(
    val photos: List<Uri> = listOf(),
    val cameraPhotoUri: Uri = Uri.EMPTY,
    val useCamera: Boolean? = false
)
