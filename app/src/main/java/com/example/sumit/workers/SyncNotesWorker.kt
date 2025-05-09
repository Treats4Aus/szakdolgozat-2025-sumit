package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sumit.SumItApplication
import com.example.sumit.data.notes.Note
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.first
import java.util.Date

private const val TAG = "SyncNotesWorker"

class SyncNotesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as SumItApplication
        val localRepository = app.container.notesRepository
        val remoteRepository = app.container.remoteNotesRepository
        val userRepository = app.container.userRepository

        val userId = userRepository.currentUser.first()?.uid ?: return Result.failure()
        val localNotes = localRepository.getAllNotesStream().first()
        val remoteNotes = remoteRepository.getUserNotes(userId).first()

        for (note in localNotes) {
            if (note.firebaseId == null) {
                try {
                    remoteRepository.uploadNote(userId, note)
                    Log.d(TAG, "Local note with id ${note.id} successfully uploaded")
                } catch (e: FirebaseException) {
                    Log.e(TAG, "Local note with id ${note.id} failed to upload", e)
                }
            } else {
                remoteNotes
                    .find {
                        it.id == note.firebaseId &&
                                it.lastModified < note.lastModified.time
                    }
                    ?.let {
                        try {
                            val updatedNote = it.copy(
                                lastModified = note.lastModified.time,
                                title = note.title,
                                content = note.content,
                                summary = note.summary,
                                keywords = emptyList()
                            )
                            remoteRepository.updateNote(updatedNote)
                            Log.d(TAG, "Remote note with id ${it.id} successfully updated")
                        } catch (e: FirebaseException) {
                            Log.e(TAG, "Remote note with id ${it.id} failed to update", e)
                        }
                    }
            }
        }

        for (note in remoteNotes) {
            if (localNotes.none { it.firebaseId == note.id }) {
                val downloadedNote = Note(
                    firebaseId = note.id,
                    created = Date(note.created),
                    lastModified = Date(note.lastModified),
                    title = note.title,
                    content = note.content,
                    summary = note.summary
                )
                localRepository.addNote(downloadedNote)
                Log.d(TAG, "Local note with id ${note.id} successfully downloaded")
            } else {
                localNotes
                    .find {
                        it.firebaseId == note.id &&
                                it.lastModified.time < note.lastModified
                    }
                    ?.let {
                        val updatedNote = it.copy(
                            lastModified = Date(note.lastModified),
                            title = note.title,
                            content = note.content,
                            summary = note.summary
                        )
                        localRepository.updateNote(updatedNote)
                        Log.d(TAG, "Local note with id ${it.id} successfully updated")
                    }
            }
        }
        return Result.success()
    }
}
