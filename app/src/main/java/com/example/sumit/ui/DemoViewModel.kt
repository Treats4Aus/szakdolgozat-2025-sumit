package com.example.sumit.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DemoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState: StateFlow<DemoUiState> = _uiState.asStateFlow()

    fun recognizeImage(dataPath: String, image: Bitmap) {
        val tess = TessBaseAPI()
        if (!tess.init(dataPath, "eng")) {
            tess.recycle()
            return
        }
        tess.setImage(image)
        val text = tess.utF8Text
        tess.recycle()

        _uiState.update { currentState ->
            currentState.copy(result = text)
        }
    }
}
