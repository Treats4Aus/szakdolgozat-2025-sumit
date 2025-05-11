package com.example.sumit.ui.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.common.EditNoteForm
import com.example.sumit.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object EditNoteDestination : NavigationDestination {
    override val route = "edit_note"
    override val titleRes = R.string.edit_note
    const val noteIdArg = "noteIdArg"
    val routeWithArgs = "${route}/{$noteIdArg}"
}

@Composable
fun EditNoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditNoteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val viewedNote by viewModel.viewedNote.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(EditNoteDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            EditNoteForm(
                note = viewedNote,
                onTitleEdit = viewModel::editTitle,
                onContentEdit = viewModel::editContent,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.medium_padding))
            )

            Box(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.saveNote()
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.save_note))
                }
            }
        }
    }
}
