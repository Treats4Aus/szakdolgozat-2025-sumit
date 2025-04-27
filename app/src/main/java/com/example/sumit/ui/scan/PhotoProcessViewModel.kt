package com.example.sumit.ui.scan

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.sumit.R
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.KEY_NOTE_TEXT
import com.example.sumit.utils.KEY_SUMMARY_TEXT
import com.example.sumit.utils.TAG_DOWNLOAD_WORKER
import com.example.sumit.utils.TAG_RECOGNITION_WORKER
import com.example.sumit.utils.TAG_REFINING_WORKER
import com.example.sumit.utils.TAG_STRUCTURING_WORKER
import com.example.sumit.utils.TAG_SUMMARY_WORKER
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Date

class PhotoProcessViewModel(
    private val photosRepository: PhotosRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    @OptIn(FlowPreview::class)
    val processState: StateFlow<ProcessState> = photosRepository.processingWorkData.map { infos ->
        val runningWork = infos.find { it.state == WorkInfo.State.RUNNING }
        if (runningWork == null) {
            infos.find { it.tags.contains(TAG_SUMMARY_WORKER) }?.let {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    val text = it.outputData.getString(KEY_NOTE_TEXT) ?: ""
                    val summary = it.outputData.getString(KEY_SUMMARY_TEXT) ?: ""

                    val title = text.split("\n").firstOrNull() ?: ""
                    val content = text.split("\n").drop(1).joinToString("\n")

                    _processedNote.value = Note(
                        created = Date(),
                        lastModified = Date(),
                        title = title,
                        content = content,
                        summary = summary
                    )

                    return@map ProcessState.DONE
                }
            }
            return@map ProcessState.INITIALIZING
        }

        val workerTags = runningWork.tags
        when {
            workerTags.contains(TAG_DOWNLOAD_WORKER) -> ProcessState.DOWNLOADING
            workerTags.contains(TAG_RECOGNITION_WORKER) -> ProcessState.RECOGNIZING
            workerTags.contains(TAG_REFINING_WORKER) -> ProcessState.REFINING
            workerTags.contains(TAG_STRUCTURING_WORKER) -> ProcessState.CREATING
            workerTags.contains(TAG_SUMMARY_WORKER) -> ProcessState.GENERATING
            else -> ProcessState.INITIALIZING
        }
    }
        .onStart {
            photosRepository.startProcessing()
        }
        .debounce(1_000L)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            ProcessState.INITIALIZING
        )

    private val _processedNote = MutableStateFlow<Note?>(null)
    val processedNote = _processedNote.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    fun updateTitle(newTitle: String) {
        _processedNote.update { currentState ->
            currentState?.copy(
                title = newTitle
            )
        }
    }

    fun updateContent(newContent: String) {
        _processedNote.update { currentState ->
            currentState?.copy(
                content = newContent
            )
        }
    }

    suspend fun saveNote() {
        if (_processedNote.value != null) {
            _isSaving.value = true
            delay(2_000L)
            notesRepository.addNote(_processedNote.value!!)
            _isSaving.value = false
        }
    }
}

enum class ProcessState(@StringRes val descriptionRes: Int) {
    INITIALIZING(R.string.initializing),
    DOWNLOADING(R.string.downloading_models),
    RECOGNIZING(R.string.recognizing_text),
    REFINING(R.string.refining_text),
    CREATING(R.string.creating_note),
    GENERATING(R.string.generating_keywords),
    DONE(R.string.done)
}
