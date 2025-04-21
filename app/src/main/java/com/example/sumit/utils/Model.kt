package com.example.sumit.utils

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend

enum class Model(
    val modelName: String,
    val preferredBackend: Backend,
    val temperature: Float,
    val topK: Int,
    val topP: Float,
) {
    GEMMA3_GPU(
        modelName = MODEL_NAME,
        preferredBackend = Backend.GPU,
        temperature = 0.8f,
        topK = 64,
        topP = 0.95f
    )
}
