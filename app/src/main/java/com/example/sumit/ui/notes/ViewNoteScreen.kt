package com.example.sumit.ui.notes

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.navigation.NavigationDestination

object ViewNoteDestination : NavigationDestination {
    override val route = "view_note"
    override val titleRes = R.string.view_note
    const val noteIdArg = "noteIdArg"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@Composable
fun ViewNoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewNoteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val viewedNote by viewModel.viewedNoteUiState.collectAsState()

    var showingSummary by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(ViewNoteDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.medium_padding))
        ) {
            Text(
                text = viewedNote.title,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.medium_padding)),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.weight(1f)) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showingSummary,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    NoteContent(content = viewedNote.content)
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = showingSummary,
                    enter = fadeIn() + slideInHorizontally { it / 2 },
                    exit = fadeOut() + slideOutHorizontally { it / 2 }
                ) {
                    NoteSummary(summary = viewedNote.summary)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.medium_padding)),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showingSummary = false }) {
                    Text("Note")
                }

                Button(onClick = { showingSummary = true }) {
                    Text("Summary")
                }
            }
        }
    }
}

@Composable
fun NoteContent(
    content: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = content.repeat(3),
        modifier = modifier.verticalScroll(rememberScrollState()),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun NoteSummary(
    summary: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = summary,
        modifier = modifier.verticalScroll(rememberScrollState()),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Justify
    )
}
