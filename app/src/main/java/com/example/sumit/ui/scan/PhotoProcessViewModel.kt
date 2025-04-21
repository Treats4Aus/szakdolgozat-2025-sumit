package com.example.sumit.ui.scan

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.sumit.R
import com.example.sumit.data.photos.PhotosRepository
import com.example.sumit.utils.TAG_DOWNLOAD_WORKER
import com.example.sumit.utils.TAG_RECOGNITION_WORKER
import com.example.sumit.utils.TAG_REFINING_WORKER
import com.example.sumit.utils.TAG_STRUCTURING_WORKER
import com.example.sumit.utils.TAG_SUMMARY_WORKER
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class PhotoProcessViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    @OptIn(FlowPreview::class)
    val processState: StateFlow<ProcessState> = photosRepository.processingWorkData.map { infos ->
        val runningWork = infos.find { it.state == WorkInfo.State.RUNNING }
        if (runningWork == null) {
            return@map if (infos.all { it.state == WorkInfo.State.SUCCEEDED })
                ProcessState.DONE
            else
                ProcessState.INITIALIZING
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
            SharingStarted.WhileSubscribed(5_000L),
            ProcessState.INITIALIZING
        )
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
