package com.example.sumit.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DemoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState: StateFlow<DemoUiState> = _uiState.asStateFlow()

    fun recognizeImage(image: Bitmap) {
        val segmented = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(segmented)
        val bitmapPaint = Paint()
        val matrixGreyscale = ColorMatrix()
        matrixGreyscale.setSaturation(0f)
        bitmapPaint.colorFilter = ColorMatrixColorFilter(matrixGreyscale)
        c.drawBitmap(image, 0f, 0f, bitmapPaint)
        bitmapPaint.colorFilter = ColorMatrixColorFilter(createThresholdMatrix(70))
        c.drawBitmap(segmented, 0f, 0f, bitmapPaint)
        bitmapPaint.colorFilter = null

        _uiState.update { currentState ->
            currentState.copy(segmented = segmented)
        }

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val input = InputImage.fromBitmap(segmented, 0)
        val result = recognizer.process(input)
            .addOnSuccessListener { visionText ->
                _uiState.update { currentState ->
                    currentState.copy(result = visionText.text)
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { currentState ->
                    currentState.copy(result = "Hiba: ${e.message}")
                }
            }
    }

    private fun createThresholdMatrix(threshold: Int): ColorMatrix {
        val matrix = ColorMatrix(floatArrayOf(
            85f, 85f, 85f, 0f, -255f * threshold,
            85f, 85f, 85f, 0f, -255f * threshold,
            85f, 85f, 85f, 0f, -255f * threshold,
            0f, 0f, 0f, 1f, 0f
            )
        )
        return matrix
    }
}
