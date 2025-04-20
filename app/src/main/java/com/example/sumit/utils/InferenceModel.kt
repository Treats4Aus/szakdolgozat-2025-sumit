package com.example.sumit.utils

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import kotlinx.coroutines.guava.await
import java.io.File

const val MAX_TOKENS = 1024
const val TAG = "InferenceModel"

class ModelLoadFailException :
    Exception("Failed to load model, please try again")

class ModelSessionCreateFailException :
    Exception("Failed to create model session, please try again")

class InferenceModel private constructor(context: Context) {
    private lateinit var llmInference: LlmInference

    init {
        if (!modelExists(context)) {
            throw IllegalArgumentException("Model not found")
        }

        createEngine(context)
    }

    fun close() {
        llmInference.close()
    }

    private fun createEngine(context: Context) {
        val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath(context))
            .setMaxTokens(MAX_TOKENS)
            .setPreferredBackend(model.preferredBackend)
            .build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading model: ${e.message}", e)
            throw ModelLoadFailException()
        }
    }

    private fun createSession(): LlmInferenceSession {
        val sessionOptions = LlmInferenceSessionOptions.builder()
            .setTemperature(model.temperature)
            .setTopK(model.topK)
            .setTopP(model.topP)
            .build()

        return try {
            LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating session: ${e.message}", e)
            throw ModelSessionCreateFailException()
        }
    }

    suspend fun generateOneTimeResponse(prompt: String): String {
        val llmInferenceSession = createSession()

        llmInferenceSession.addQueryChunk(prompt)
        val response = llmInferenceSession.generateResponseAsync().await()

        llmInferenceSession.close()
        return response
    }

    companion object {
        private val model = Model.GEMMA3_GPU

        @Volatile
        private var Instance: InferenceModel? = null

        fun getInstance(context: Context): InferenceModel {
            return Instance ?: synchronized(this) {
                InferenceModel(context).also { Instance = it }
            }
        }

        fun modelPath(context: Context): String {
            val modelsDir = File(context.filesDir, MODELS_PATH)
            val modelFile = File(modelsDir, model.modelName)
            return if (modelFile.exists()) {
                modelFile.absolutePath
            } else {
                ""
            }
        }

        fun modelExists(context: Context): Boolean {
            return File(modelPath(context)).exists()
        }
    }
}