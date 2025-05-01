package com.example.sumit.ui.home.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.common.NoteCard

@Composable
fun MyNotesTab(
    onViewNote: (Int) -> Unit,
    onEditNote: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyNotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val myNotesUiState by viewModel.myNotesUiState.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.medium_padding))
    ) {
        items(myNotesUiState.myNotes) { note ->
            NoteCard(
                note = note,
                canModify = true,
                onNoteClick = { onViewNote(note.id) },
                onEditClick = { onEditNote(note.id) },
                onDeleteClick = { viewModel.deleteNote(note) }
            )
        }
    }
}
