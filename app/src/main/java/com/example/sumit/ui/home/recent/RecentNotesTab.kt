package com.example.sumit.ui.home.recent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun RecentNotesTab(
    onViewOwnedNote: (Int) -> Unit,
    onViewSharedNote: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentNotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val recentNotes by viewModel.recentNotes.collectAsState()

    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.medium_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        Text(
            text = stringResource(R.string.recent_notes),
            style = MaterialTheme.typography.displayMedium
        )

        if (recentNotes.isNotEmpty()) {
            recentNotes.forEach {
                NoteCard(
                    note = it,
                    canModify = false,
                    onNoteClick = { onViewOwnedNote(it.id) }
                )
            }
        } else {
            NoRecentNotes()
        }

        Text(
            text = stringResource(R.string.shared_with_you),
            style = MaterialTheme.typography.displayMedium
        )

        NoSharedNotes(isSignedIn = false)
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
