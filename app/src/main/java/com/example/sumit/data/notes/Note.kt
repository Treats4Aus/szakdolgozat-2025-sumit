package com.example.sumit.data.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "firebase_id")
    val firebaseId: String? = null,

    @ColumnInfo(name = "owner")
    val owner: String? = null,

    @ColumnInfo(name = "created")
    val created: Date,

    @ColumnInfo(name = "last_modified")
    val lastModified: Date,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "summary")
    val summary: String
)
