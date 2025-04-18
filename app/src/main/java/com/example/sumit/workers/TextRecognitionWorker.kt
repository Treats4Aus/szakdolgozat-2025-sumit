package com.example.sumit.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TextRecognitionWorker(ctx: Context, param: WorkerParameters) : CoroutineWorker(ctx, param) {
    override suspend fun doWork(): Result {
        TODO("AAA")
    }
}
