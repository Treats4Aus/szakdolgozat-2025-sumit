package com.example.sumit.ui.home.recent

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.common.NoteCard
import com.example.sumit.utils.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RecentNotesTab(
    onViewOwnedNote: (Int) -> Unit,
    onViewSharedNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentNotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val user by viewModel.currentUser.collectAsState()
    val recentNotes by viewModel.recentNotes.collectAsState()
    val sharedNotes by viewModel.sharedNotes.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.medium_padding))
    ) {
        item {
            Text(
                text = stringResource(R.string.recent_notes),
                style = MaterialTheme.typography.displayMedium
            )
        }

        if (recentNotes.isNotEmpty()) {
            items(recentNotes, { it.id }) { note ->
                val lastModifiedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                    .format(note.lastModified)

                NoteCard(
                    title = note.title,
                    content = note.content,
                    extraInfo = stringResource(R.string.last_modified, lastModifiedDate),
                    canModify = false,
                    onNoteClick = { onViewOwnedNote(note.id) }
                )
            }
        } else {
            item {
                NoRecentNotes()
            }
        }

        item {
            Text(
                text = stringResource(R.string.shared_with_you),
                style = MaterialTheme.typography.displayMedium
            )
        }

        if (sharedNotes.isNotEmpty()) {
            items(sharedNotes, { it.id }) { note ->
                NoteCard(
                    title = note.title,
                    content = note.content,
                    extraInfo = stringResource(R.string.shared_by, note.owner),
                    canModify = false,
                    onNoteClick = { onViewSharedNote(note.id) }
                )
            }
        } else {
            item {
                NoSharedNotes(isSignedIn = user != null)
            }
        }
    }
}

@Composable
fun NoRecentNotes(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.large_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_recent_notes),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.medium_padding)),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.use_scan_button),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NoSharedNotes(
    isSignedIn: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.large_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_shared_notes),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.medium_padding)),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(
                if (isSignedIn)
                    R.string.add_friends
                else
                    R.string.sign_in_or_create_account
            ),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
