package com.example.sumit.data.notes

/**
 * Represents a note stored in Firestore.
 */
data class RemoteNote(
    /**
     * Unique identifier of the note. Also the id of the Firestore document.
     */
    val id: String = "",

    /**
     * An identifier that points to the owner of note.
     */
    val owner: String = "",

    /**
     * The time of creation represented as a Long value.
     */
    val created: Long = 0L,

    /**
     * The time the note was modified last represented as a Long value. Used to track which version
     * is the most up-to-date during syncing.
     */
    val lastModified: Long = 0L,

    /**
     * Title of the note.
     */
    val title: String = "",

    /**
     * The content of the note, divided into paragraphs with line breaks.
     */
    val content: String = "",

    /**
     * The summary of the note as raw text.
     */
    val summary: String = "",

    /**
     * List of keywords that belong to the note.
     */
    val keywords: List<String> = emptyList(),

    /**
     * List of user ids that can access this note through sharing.
     */
    val sharedWith: List<String> = emptyList()
)
