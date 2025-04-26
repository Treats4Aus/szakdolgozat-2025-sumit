package com.example.sumit.ui.scan

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.sumit.ui.navigation.NavigationDestination

private const val TAG = "PhotoProcessScreen"

object PhotoProcessDestination : NavigationDestination {
    override val route = "photo_process"
    override val titleRes = R.string.processing_photos
}

@Composable
fun PhotoProcessScreen(
    onCancel: () -> Unit,
    onProcessingDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoProcessViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val processState by viewModel.processState.collectAsState()
    val processedNote by viewModel.processedNote.collectAsState()

    LaunchedEffect(processState) {
        if (processState == ProcessState.DONE) {
            onProcessingDone()
        }
    }

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
            if (processState == ProcessState.DONE && processedNote != null) {
                ProcessedNoteEditScreen(
                    processedNote = processedNote!!,
                    onTitleEdit = viewModel::updateTitle,
                    onContentEdit = viewModel::updateContent,
                    onSaveNote = { }
                )
            } else {
                ProcessingScreen(processState = processState)
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
        modifier = modifier,
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
        TextField(
            value = processedNote.title,
            onValueChange = onTitleEdit
        )

        TextField(
            value = processedNote.content,
            onValueChange = onContentEdit
        )

        Button(onClick = onSaveNote) {
            Text("Save note")
        }
    }
}
