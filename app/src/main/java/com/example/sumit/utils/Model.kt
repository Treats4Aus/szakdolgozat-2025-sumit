package com.example.sumit.utils

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend

/**
 * Represent an LLM model available to do inference with.
 */
enum class Model(
    /**
     * The name of the model.
     */
    val modelName: String,

    /**
     * The preferred environment to be used for running the model.
     */
    val preferredBackend: Backend,

    /**
     * The temperature to be used by the model to generate responses.
     */
    val temperature: Float,

    /**
     * The topK value to be used by the model.
     */
    val topK: Int,

    /**
     * The topP value to be used by the model.
     */
    val topP: Float,
) {
    GEMMA3_GPU(
        modelName = MODEL_NAME,
        preferredBackend = Backend.GPU,
        temperature = 0.8f,
        topK = 64,
        topP = 0.95f
    ),
    GEMMA3_CPU(
        modelName = MODEL_NAME,
        preferredBackend = Backend.CPU,
        temperature = 0.3f,
        topK = 64,
        topP = 0.95f
    )
}
