package com.example.sumit.ui.test

import android.graphics.Bitmap
import android.net.Uri
import androidx.work.WorkInfo
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.ui.scan.PhotoSegmentViewModel
import com.example.sumit.ui.scan.SegmentedPhoto
import com.example.sumit.utils.KEY_PHOTO_URI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class PhotoSegmentViewModelTest {
    private lateinit var viewModel: PhotoSegmentViewModel
    private val segmentationWorkDataMock = MutableSharedFlow<WorkInfo>()
    private val photosRepository = mockk<PhotosRepository>(relaxUnitFun = true)
    private val tempUriMock = mockk<Uri>()
    private val workerUUIDMock = mockk<UUID>()

    @Before
    fun setup() {
        coEvery { photosRepository.getTempPhotos() } returns listOf(tempUriMock)
        every { photosRepository.startSegmentation(any(), any()) } returns workerUUIDMock
        every { photosRepository.getSegmentationWorkData(any()) } returns segmentationWorkDataMock
        viewModel = PhotoSegmentViewModel(photosRepository)
    }

    @Test
    fun photoSegmentViewModel_OnInit_TempPhotosLoaded() {
        val currentUiState = viewModel.uiState.value
        val expectedPhotos = arrayOf(SegmentedPhoto(tempUriMock, true))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
        coVerify { photosRepository.getTempPhotos() }
        verify { photosRepository.startSegmentation(0, tempUriMock) }
        verify { photosRepository.getSegmentationWorkData(workerUUIDMock) }
    }

    @Test
    fun photoSegmentViewModel_SegmentationFinished_PhotoLoadingEnds() = runTest {
        val inProgressWorkInfoMock = mockk<WorkInfo>()
        every { inProgressWorkInfoMock.outputData.getString(KEY_PHOTO_URI) } returns null
        every { inProgressWorkInfoMock.state.isFinished } returns false

        segmentationWorkDataMock.emit(inProgressWorkInfoMock)

        var currentUiState = viewModel.uiState.value
        var expectedPhotos = arrayOf(SegmentedPhoto(tempUriMock, true))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())

        val segmentedUriMock = mockk<Uri>()
        every { segmentedUriMock.toString() } returns "segmented"

        val succeededWorkInfoMock = mockk<WorkInfo>()
        every { succeededWorkInfoMock.outputData.getString(KEY_PHOTO_URI) } returns segmentedUriMock.toString()
        every { succeededWorkInfoMock.state.isFinished } returns true

        segmentationWorkDataMock.emit(succeededWorkInfoMock)

        currentUiState = viewModel.uiState.value
        expectedPhotos = arrayOf(SegmentedPhoto(Uri.parse(segmentedUriMock.toString()), false))
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
    }

    @Test
    fun photoSegmentViewModel_SegmentationFinishedPhotoNotLoading_PhotoUriDoesNotUpdate() =
        runTest {
            val segmentedUriMock = mockk<Uri>()
            every { segmentedUriMock.toString() } returns "segmented"

            val succeededWorkInfoMock = mockk<WorkInfo>()
            every { succeededWorkInfoMock.outputData.getString(KEY_PHOTO_URI) } returns segmentedUriMock.toString()
            every { succeededWorkInfoMock.state.isFinished } returns true

            segmentationWorkDataMock.emit(succeededWorkInfoMock)

            val segmentedUriMock2 = mockk<Uri>()
            every { segmentedUriMock2.toString() } returns "segmented2"

            val succeededWorkInfoMock2 = mockk<WorkInfo>()
            every { succeededWorkInfoMock2.outputData.getString(KEY_PHOTO_URI) } returns segmentedUriMock2.toString()
            every { succeededWorkInfoMock2.state.isFinished } returns true

            segmentationWorkDataMock.emit(succeededWorkInfoMock2)

            val currentUiState = viewModel.uiState.value
            val expectedPhotos =
                arrayOf(SegmentedPhoto(Uri.parse(segmentedUriMock.toString()), false))
            assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
        }

    @Test
    fun photoSegmentViewModel_PhotoSelected_SelectedIndexSet() {
        var currentUiState = viewModel.uiState.value
        assertNull(currentUiState.selectedPhotoIndex)

        val index = 0
        viewModel.selectPhoto(index)

        currentUiState = viewModel.uiState.value
        assertEquals(index, currentUiState.selectedPhotoIndex)
    }

    @Test
    fun photoSegmentViewModel_ModalDismissed_SelectedIndexCleared() {
        val index = 1
        viewModel.selectPhoto(index)
        viewModel.clearSelectedPhoto()

        val currentUiState = viewModel.uiState.value
        assertNull(currentUiState.selectedPhotoIndex)
    }

    @Test
    fun photoSegmentViewModel_AdjustPhoto_AdjustedImageSet() {
        var currentUiState = viewModel.uiState.value
        assertNull(currentUiState.adjustedImage)
        assertFalse(currentUiState.isAdjustmentRunning)

        val index = 0
        val amount = 1

        val bitmapMock = mockk<Bitmap>()
        coEvery { photosRepository.adjustBitmap(tempUriMock, amount) } returns bitmapMock

        viewModel.selectPhoto(index)
        viewModel.adjustSelectedPhoto(amount)

        currentUiState = viewModel.uiState.value
        assertEquals(bitmapMock, currentUiState.adjustedImage)
        assertFalse(currentUiState.isAdjustmentRunning)
        coVerify { photosRepository.adjustBitmap(tempUriMock, amount) }
    }

    @Test
    fun photoSegmentViewModel_SaveAdjustedPhotoWhenNull_DoNothing() {
        val index = 0

        viewModel.selectPhoto(index)
        viewModel.saveAdjustedPhoto()

        val currentUiState = viewModel.uiState.value
        assertEquals(index, currentUiState.selectedPhotoIndex)
        coVerify(inverse = true) { photosRepository.overrideSegmentedPhoto(any(), any()) }
    }

    @Test
    fun photoSegmentViewModel_SaveAdjustedPhotoWhenNotNull_ListUpdated() {
        val index = 0
        val amount = 1

        val bitmapMock = mockk<Bitmap>()
        coEvery { photosRepository.adjustBitmap(tempUriMock, amount) } returns bitmapMock

        viewModel.selectPhoto(index)
        viewModel.adjustSelectedPhoto(amount)

        var currentUiState = viewModel.uiState.value
        assertEquals(index, currentUiState.selectedPhotoIndex)
        assertEquals(bitmapMock, currentUiState.adjustedImage)

        val adjustedUriMock = mockk<Uri>()
        coEvery {
            photosRepository.overrideSegmentedPhoto(
                index,
                bitmapMock
            )
        } returns adjustedUriMock

        viewModel.saveAdjustedPhoto()

        currentUiState = viewModel.uiState.value
        val expectedPhotos = arrayOf(SegmentedPhoto(adjustedUriMock, false))
        assertNull(currentUiState.selectedPhotoIndex)
        assertNull(currentUiState.adjustedImage)
        assertArrayEquals(expectedPhotos, currentUiState.photos.toTypedArray())
        coVerify { photosRepository.overrideSegmentedPhoto(index, bitmapMock) }
    }
}
