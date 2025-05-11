package com.example.sumit.utils

import com.example.sumit.data.notes.Note
import java.util.Date

object DataSource {
    val notes = listOf(
        Note(
            id = 0,
            created = Date(),
            lastModified = Date(),
            title = "Physics",
            content = "aaa",
            summary = "a"
        ),
        Note(
            id = 1,
            created = Date(),
            lastModified = Date(),
            title = "Literature",
            content = "aaa",
            summary = "a"
        ),
        Note(
            id = 2,
            created = Date(),
            lastModified = Date(),
            title = "Maths",
            content = "aaa",
            summary = "a"
        ),
        Note(
            id = 3,
            created = Date(),
            lastModified = Date(),
            title = "Art",
            content = "aaa",
            summary = "a"
        ),
        Note(
            id = 4,
            created = Date(),
            lastModified = Date(),
            title = "Music",
            content = "aaa",
            summary = "a"
        )
    )
}