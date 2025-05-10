package com.example.sumit.ui.home.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
        if (myNotesUiState.myNotes.isNotEmpty()) {
            items(myNotesUiState.myNotes) { note ->
                NoteCard(
                    title = note.title,
                    content = note.content,
                    lastModified = note.lastModified,
                    canModify = true,
                    onNoteClick = { onViewNote(note.id) },
                    onEditClick = { onEditNote(note.id) },
                    onDeleteClick = { viewModel.deleteNote(note) }
                )
            }
        } else {
            item {
                NoNotes(modifier = Modifier.padding(top = 24.dp))
            }
        }
    }
}

@Composable
fun NoNotes(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.large_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_notes),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.medium_padding)),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.use_scan_button),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
