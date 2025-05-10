package com.example.sumit.data.notes

data class RemoteNote(
    val id: String = "",
    val owner: String = "",
    val created: Long = 0L,
    val lastModified: Long = 0L,
    val title: String = "",
    val content: String = "",
    val summary: String = "",
    val keywords: List<String> = emptyList(),
    val sharedWith: List<String> = emptyList()
)
