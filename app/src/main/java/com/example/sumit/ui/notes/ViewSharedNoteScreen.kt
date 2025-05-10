package com.example.sumit.ui.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.navigation.NavigationDestination

object ViewSharedNoteDestination : NavigationDestination {
    override val route = "view_shared_note"
    override val titleRes = R.string.view_shared_note
    const val noteIdArg = "noteIdArg"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@Composable
fun ViewSharedNoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewSharedNoteViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            viewedNote?.let { note ->
                Column(
                    modifier = modifier.padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    Text(
                        text = note.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(R.dimen.large_padding)),
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !showingSummary,
                            enter = fadeIn() + slideInHorizontally(),
                            exit = fadeOut() + slideOutHorizontally()
                        ) {
                            NoteContent(content = note.content)
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = showingSummary,
                            enter = fadeIn() + slideInHorizontally { it / 2 },
                            exit = fadeOut() + slideOutHorizontally { it / 2 }
                        ) {
                            NoteSummary(summary = note.summary)
                        }
                    }

                    OptionSwitcher(
                        firstOptionText = stringResource(R.string.note),
                        secondOptionText = stringResource(R.string.summary),
                        secondOptionSelected = showingSummary,
                        onOptionSwitch = { showingSummary = it }
                    )
                }
            }

            AnimatedVisibility(
                visible = viewedNote == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularLoadingScreenWithBackdrop()
            }
        }
    }
}
