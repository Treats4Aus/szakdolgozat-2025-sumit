package com.example.sumit.ui.test

import android.net.Uri
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkInfo
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.ui.scan.Photo
import com.example.sumit.ui.scan.PhotoSelectDestination
import com.example.sumit.ui.scan.PhotoSelectViewModel
import com.example.sumit.ui.scan.SavePhotosState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class PhotoSelectViewModelTest {
    private lateinit var viewModel: PhotoSelectViewModel
    private val movePhotosWorkInfoMock = MutableSharedFlow<List<WorkInfo>>()
    private val photosRepository = mockk<PhotosRepository>(relaxUnitFun = true)

    @Before
    fun setup() {
        val savedState = SavedStateHandle(mapOf(PhotoSelectDestination.selectModeArg to false))
        every { photosRepository.movePhotosWorkData } returns movePhotosWorkInfoMock
        viewModel = PhotoSelectViewModel(photosRepository, savedState)
    }

    @Test
    fun photoSelectViewModel_OnInit_UseCameraSetToFalse() {
        val currentUiState = viewModel.uiState.value
        assertEquals(false, currentUiState.useCamera)
    }

    @Test
    fun photoSelectViewModel_DisableDefaultLaunch_UseCameraSetToNull() {
        viewModel.disableDefaultLaunch()

        val currentUiState = viewModel.uiState.value
        assertNull(currentUiState.useCamera)
    }

    @Test
    fun photoSelectViewModel_AddPhoto_PhotoListUpdated() {
        mockkStatic(UUID::class)
        val uuidMock = mockk<UUID>()
        every { UUID.randomUUID() } returns uuidMock

        var currentUiState = viewModel.uiState.value
        assertTrue(currentUiState.photos.isEmpty())

        val testUri = Uri.parse("test")
        viewModel.addPhoto(testUri)

        currentUiState = viewModel.uiState.value
        val expectedPhotos = arrayOf(Photo(uuidMock, testUri))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
    }

    @Test
    fun photoSelectViewModel_AddPhotoThroughCamera_PhotoListUpdated() {
        mockkStatic(UUID::class)
        val uuidMock = mockk<UUID>()
        every { UUID.randomUUID() } returns uuidMock

        var currentUiState = viewModel.uiState.value
        assertTrue(currentUiState.photos.isEmpty())

        val testUri = Uri.parse("test")
        viewModel.updateCameraPhotoUri(testUri)
        viewModel.addPhotoFromCamera()

        currentUiState = viewModel.uiState.value
        val expectedPhotos = arrayOf(Photo(uuidMock, testUri))
        assertEquals(testUri, currentUiState.cameraPhotoUri)
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
    }

    @Test
    fun photoSelectViewModel_RearrangeGrid_PhotoListUpdated() {
        mockkStatic(UUID::class)
        val uuidMock = mockk<UUID>()
        every { UUID.randomUUID() } returns uuidMock

        val fromInfo = mockk<LazyGridItemInfo>()
        every { fromInfo.index } returns 0
        val toInfo = mockk<LazyGridItemInfo>()
        every { toInfo.index } returns 1

        val uriFirst = Uri.parse("first")
        val uriSecond = Uri.parse("second")
        viewModel.addPhoto(uriFirst)
        viewModel.addPhoto(uriSecond)

        var currentUiState = viewModel.uiState.value
        var expectedPhotos = arrayOf(Photo(uuidMock, uriFirst), Photo(uuidMock, uriSecond))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())

        viewModel.movePhoto(fromInfo, toInfo)

        currentUiState = viewModel.uiState.value
        expectedPhotos = expectedPhotos.reversedArray()
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
    }

    @Test
    fun photoSelectViewModel_RemovePhoto_PhotoListUpdated() {
        mockkStatic(UUID::class)
        val uuidMock = mockk<UUID>()
        every { UUID.randomUUID() } returns uuidMock

        val uriFirst = Uri.parse("first")
        val uriSecond = Uri.parse("second")
        viewModel.addPhoto(uriFirst)
        viewModel.addPhoto(uriSecond)

        var currentUiState = viewModel.uiState.value
        var expectedPhotos = arrayOf(Photo(uuidMock, uriFirst), Photo(uuidMock, uriSecond))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())

        viewModel.removePhoto(0)

        currentUiState = viewModel.uiState.value
        expectedPhotos = arrayOf(Photo(uuidMock, uriSecond))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
    }

    @Test
    fun photoSelectViewModel_PhotoSelected_SelectedIndexSet() {
        var currentUiState = viewModel.uiState.value
        assertNull(currentUiState.selectedPhotoIndex)

        val index = 0
        viewModel.selectPhoto(index)

        currentUiState = viewModel.uiState.value
        assertEquals(index, currentUiState.selectedPhotoIndex)
    }

    @Test
    fun photoSelectViewModel_PhotoUnselected_SelectedIndexCleared() {
        val index = 1
        viewModel.selectPhoto(index)
        viewModel.clearSelectedPhoto()

        val currentUiState = viewModel.uiState.value
        assertNull(currentUiState.selectedPhotoIndex)
    }

    @Test
    fun photoSelectViewModel_SavingStarted_LoadingStateSet() {
        checkSavePhotosState(SavePhotosState.Default)

        viewModel.savePhotosToTemp()

        checkSavePhotosState(SavePhotosState.Loading)
        verify { photosRepository.movePhotosToTemp(emptyList()) }
    }

    @Test
    fun photoSelectViewModel_SavingFinishedNotLoading_CompleteStateNotSet() = runTest {
        val succeededWorkInfoMock = mockk<WorkInfo>()
        every { succeededWorkInfoMock.state } returns WorkInfo.State.SUCCEEDED
        val runningWorkInfoMock = mockk<WorkInfo>()
        every { runningWorkInfoMock.state } returns WorkInfo.State.RUNNING

        val workerInfoMock = MutableSharedFlow<List<WorkInfo>>()
        every { photosRepository.movePhotosWorkData } returns workerInfoMock

        checkSavePhotosState(SavePhotosState.Default)

        workerInfoMock.emit(listOf(succeededWorkInfoMock, runningWorkInfoMock))

        checkSavePhotosState(SavePhotosState.Default)

        workerInfoMock.emit(listOf(succeededWorkInfoMock, succeededWorkInfoMock))

        checkSavePhotosState(SavePhotosState.Default)
    }

    @Test
    fun photoSelectViewModel_SavingFinishedLoading_CompleteStateSet() = runTest {
        val succeededWorkInfoMock = mockk<WorkInfo>()
        every { succeededWorkInfoMock.state } returns WorkInfo.State.SUCCEEDED
        val runningWorkInfoMock = mockk<WorkInfo>()
        every { runningWorkInfoMock.state } returns WorkInfo.State.RUNNING

        checkSavePhotosState(SavePhotosState.Default)

        viewModel.savePhotosToTemp()
        movePhotosWorkInfoMock.emit(listOf(succeededWorkInfoMock, runningWorkInfoMock))

        checkSavePhotosState(SavePhotosState.Loading)

        movePhotosWorkInfoMock.emit(listOf(succeededWorkInfoMock, succeededWorkInfoMock))

        checkSavePhotosState(SavePhotosState.Complete)
    }

    @Test
    fun photoSelectViewModel_CancelSaving_DefaultStateSet() {
        viewModel.savePhotosToTemp()

        checkSavePhotosState(SavePhotosState.Loading)

        viewModel.resetSavePhotoState()

        checkSavePhotosState(SavePhotosState.Default)
    }

    private fun checkSavePhotosState(expectedState: SavePhotosState) {
        val currentUiState = viewModel.uiState.value
        assertEquals(expectedState, currentUiState.savePhotosState)
    }
}