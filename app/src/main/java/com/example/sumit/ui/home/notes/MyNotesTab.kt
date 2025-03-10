package com.example.sumit.ui.home.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.data.notes.Note
import com.example.sumit.ui.AppViewModelProvider

@Composable
fun MyNotesTab(
    modifier: Modifier = Modifier,
    viewModel: MyNotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val myNotesUiState by viewModel.myNotesUiState.collectAsState()

    LazyColumn(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(myNotesUiState.myNotes) { note ->
            NoteCard(note = note)
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(134, 184, 235))
        .padding(4.dp)
    ) {
        Text(note.id.toString())

        Spacer(Modifier.weight(1f))

        Text(note.title)
    }
}
