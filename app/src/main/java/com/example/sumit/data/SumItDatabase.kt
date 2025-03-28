package com.example.sumit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NoteDao
import com.example.sumit.utils.Converters

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SumItDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var Instance: SumItDatabase? = null

        fun getDatabase(context: Context): SumItDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SumItDatabase::class.java, "sumit_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}