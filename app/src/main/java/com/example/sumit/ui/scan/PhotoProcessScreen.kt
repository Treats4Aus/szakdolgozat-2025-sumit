package com.example.sumit.ui.scan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.data.notes.Note
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.common.EditNoteForm
import com.example.sumit.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

private const val TAG = "PhotoProcessScreen"

object PhotoProcessDestination : NavigationDestination {
    override val route = "photo_process"
    override val titleRes = R.string.processing_photos
}

@Composable
fun PhotoProcessScreen(
    onCancel: () -> Unit,
    onSavingDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoProcessViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val processState by viewModel.processState.collectAsState()
    val processedNote by viewModel.processedNote.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(PhotoProcessDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onCancel
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = processState == ProcessState.DONE && processedNote != null,
                exit = fadeOut()
            ) {
                ProcessedNoteEditScreen(
                    processedNote = processedNote!!,
                    onTitleEdit = viewModel::updateTitle,
                    onContentEdit = viewModel::updateContent,
                    onSaveNote = {
                        scope.launch {
                            viewModel.saveNote()
                            onSavingDone()
                        }
                    }
                )
            }

            AnimatedVisibility(
                visible = processState != ProcessState.DONE || processedNote == null,
                enter = fadeIn()
            ) {
                ProcessingScreen(processState = processState)
            }

            AnimatedVisibility(visible = isSaving, enter = fadeIn(), exit = fadeOut()) {
                CircularLoadingScreenWithBackdrop()
            }
        }
    }
}

@Composable
fun ProcessingScreen(
    processState: ProcessState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(dimensionResource(R.dimen.progress_indicator_size))
        )

        AnimatedContent(
            targetState = processState,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.large_padding)),
            transitionSpec = {
                slideInVertically { it } togetherWith slideOutVertically { -it }
            },
            label = "Animated process state"
        ) { state ->
            Text(
                text = stringResource(state.descriptionRes),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProcessedNoteEditScreen(
    processedNote: Note,
    onTitleEdit: (String) -> Unit,
    onContentEdit: (String) -> Unit,
    onSaveNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EditNoteForm(
            note = processedNote,
            onTitleEdit = onTitleEdit,
            onContentEdit = onContentEdit,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.medium_padding))
        )

        Box(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
            Button(
                onClick = onSaveNote,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(R.string.save_note))
            }
        }
    }
}
