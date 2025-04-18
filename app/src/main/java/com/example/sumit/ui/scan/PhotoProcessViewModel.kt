package com.example.sumit.ui.scan

import androidx.annotation.StringRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sumit.R
import com.example.sumit.data.photos.PhotosRepository

class PhotoProcessViewModel(private val photosRepository: PhotosRepository) : ViewModel() {
    private val _processState = mutableStateOf(ProcessState.INITIALIZING)
    val processState: State<ProcessState> = _processState

    init {
        photosRepository.startProcessing()
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
