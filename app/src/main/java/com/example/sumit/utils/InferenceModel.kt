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

/**
 * Handles interaction between the application and locally stored LLMs.
 */
class InferenceModel private constructor(context: Context) {
    /**
     * The connection to the LLM model.
     */
    private lateinit var llmInference: LlmInference

    /**
     * A session created within the connection.
     */
    private lateinit var llmInferenceSession: LlmInferenceSession

    init {
        if (!modelExists(context)) {
            throw IllegalArgumentException("Model not found")
        }

        createEngine(context)
        createSession()
    }

    /**
     * Closes the connection to the LLM. No requests can be made after this call.
     */
    fun close() {
        llmInferenceSession.close()
        llmInference.close()
    }

    /**
     * Recreates the session for the connection.
     */
    fun resetSession() {
        llmInferenceSession.close()
        createSession()
    }

    /**
     * Creates the connection to the LLM model.
     * @param context The application context
     */
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

    /**
     * Creates a new session.
     */
    private fun createSession() {
        val sessionOptions = LlmInferenceSessionOptions.builder()
            .setTemperature(model.temperature)
            .setTopK(model.topK)
            .setTopP(model.topP)
            .build()

        try {
            llmInferenceSession =
                LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating session: ${e.message}", e)
            throw ModelSessionCreateFailException()
        }
    }

    /**
     * Gives the loaded LLM a prompt to generate a response to. Afterwards it recreates the session.
     * @param prompt The prompt to give for the LLM
     * @return The generated response
     */
    suspend fun generateOneTimeResponse(prompt: String): String {
        llmInferenceSession.addQueryChunk(prompt)
        val response = llmInferenceSession.generateResponseAsync().await()

        resetSession()
        return response
    }

    /**
     * Gives the loaded LLM a prompt to generate a response to in the current session.
     * @param prompt The prompt to give for the LLM
     * @return The generated response
     */
    suspend fun generateResponse(prompt: String): String {
        llmInferenceSession.addQueryChunk(prompt)
        return llmInferenceSession.generateResponseAsync().await()
    }

    companion object {
        /**
         * The model to use for inference.
         */
        private val model = Model.GEMMA3_CPU

        @Volatile
        private var Instance: InferenceModel? = null

        /**
         * Returns the singleton instance of the service.
         * @param context The application context
         */
        fun getInstance(context: Context): InferenceModel {
            return Instance ?: synchronized(this) {
                InferenceModel(context).also { Instance = it }
            }
        }

        /**
         * Gets the path for the selected model.
         * @param context The application context
         * @return The path of the model
         */
        fun modelPath(context: Context): String {
            val modelsDir = File(context.filesDir, MODELS_PATH)
            val modelFile = File(modelsDir, model.modelName)
            return if (modelFile.exists()) {
                modelFile.absolutePath
            } else {
                ""
            }
        }

        /**
         * Checks if the selected model is available on the device.
         * @param context The application context
         * @return `true` if the model is found at its location, `false` otherwise
         */
        fun modelExists(context: Context): Boolean {
            return File(modelPath(context)).exists()
        }
    }
}