package com.example.sumit.ui.test

import androidx.work.WorkInfo
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.ui.scan.PhotoProcessViewModel
import com.example.sumit.ui.scan.ProcessState
import com.example.sumit.utils.TAG_DOWNLOAD_WORKER
import com.example.sumit.utils.TAG_RECOGNITION_WORKER
import com.example.sumit.utils.TAG_REFINING_WORKER
import com.example.sumit.utils.TAG_STRUCTURING_WORKER
import com.example.sumit.utils.TAG_SUMMARY_WORKER
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PhotoProcessViewModelTest {
    private lateinit var viewModel: PhotoProcessViewModel
    private val processingWorkDataMock = MutableSharedFlow<List<WorkInfo>>()
    private val photosRepository = mockk<PhotosRepository>(relaxUnitFun = true)
    private val notesRepository = mockk<NotesRepository>(relaxUnitFun = true)
    private val debounceTime = 1_000L

    @Before
    fun setup() {
        every { photosRepository.processingWorkData } returns processingWorkDataMock
        viewModel = PhotoProcessViewModel(photosRepository, notesRepository)
    }

    @Test
    fun photoProcessViewModel_OnInit_StartsProcessing() = runTest {
        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        val currentProcessedNote = viewModel.processedNote.value
        assertNull(currentProcessedNote)

        val currentIsSaving = viewModel.isSaving.value
        assertFalse(currentIsSaving)

        coVerify { photosRepository.startProcessing() }
    }

    @Test
    fun photoProcessViewModel_DownloadingModelsRunning_CorrectProcessingState() = runTest {
        val downloadWorkInfoMock = createWorkInfoMock(WorkInfo.State.RUNNING, TAG_DOWNLOAD_WORKER)
        val recognitionWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.BLOCKED, TAG_RECOGNITION_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(listOf(downloadWorkInfoMock, recognitionWorkInfoMock))

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.DOWNLOADING, currentProcessState)
    }

    @Test
    fun photoProcessViewModel_RecognitionRunning_CorrectProcessingState() = runTest {
        val downloadWorkInfoMock = createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_DOWNLOAD_WORKER)
        val recognitionWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.RUNNING, TAG_RECOGNITION_WORKER)
        val refiningWorkInfoMock = createWorkInfoMock(WorkInfo.State.BLOCKED, TAG_REFINING_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(
            listOf(
                downloadWorkInfoMock,
                recognitionWorkInfoMock,
                refiningWorkInfoMock
            )
        )

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.RECOGNIZING, currentProcessState)
    }

    @Test
    fun photoProcessViewModel_RefiningRunning_CorrectProcessingState() = runTest {
        val refiningWorkInfoMock = createWorkInfoMock(WorkInfo.State.RUNNING, TAG_REFINING_WORKER)
        val structuringWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.BLOCKED, TAG_STRUCTURING_WORKER)
        val summaryWorkInfoMock = createWorkInfoMock(WorkInfo.State.ENQUEUED, TAG_SUMMARY_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(
            listOf(
                structuringWorkInfoMock,
                refiningWorkInfoMock,
                summaryWorkInfoMock
            )
        )

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.REFINING, currentProcessState)
    }

    @Test
    fun photoProcessViewModel_StructuringRunning_CorrectProcessingState() = runTest {
        val downloadWorkInfoMock = createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_DOWNLOAD_WORKER)
        val recognitionWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_RECOGNITION_WORKER)
        val refiningWorkInfoMock = createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_REFINING_WORKER)
        val structuringWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.RUNNING, TAG_STRUCTURING_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(
            listOf(
                structuringWorkInfoMock,
                refiningWorkInfoMock,
                downloadWorkInfoMock,
                recognitionWorkInfoMock
            )
        )

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.CREATING, currentProcessState)
    }

    @Test
    fun photoProcessViewModel_SummarizingRunning_CorrectProcessingState() = runTest {
        val summaryWorkInfoMock = createWorkInfoMock(WorkInfo.State.RUNNING, TAG_SUMMARY_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(listOf(summaryWorkInfoMock))

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.GENERATING, currentProcessState)
    }

    @Test
    fun photoProcessViewModel_ProcessingCancelled_CorrectProcessingState() = runTest {
        val downloadWorkInfoMock = createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_DOWNLOAD_WORKER)
        val recognitionWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.SUCCEEDED, TAG_RECOGNITION_WORKER)
        val refiningWorkInfoMock = createWorkInfoMock(WorkInfo.State.CANCELLED, TAG_REFINING_WORKER)
        val structuringWorkInfoMock =
            createWorkInfoMock(WorkInfo.State.CANCELLED, TAG_STRUCTURING_WORKER)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.processState.collect()
        }

        var currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)

        processingWorkDataMock.emit(
            listOf(
                structuringWorkInfoMock,
                refiningWorkInfoMock,
                downloadWorkInfoMock,
                recognitionWorkInfoMock
            )
        )

        advanceTimeBy(debounceTime)

        currentProcessState = viewModel.processState.value
        assertEquals(ProcessState.INITIALIZING, currentProcessState)
    }

    private fun createWorkInfoMock(state: WorkInfo.State, tag: String): WorkInfo {
        val workInfoMock = mockk<WorkInfo>()
        every { workInfoMock.state } returns state
        every { workInfoMock.tags } returns setOf(tag)
        return workInfoMock
    }
}
