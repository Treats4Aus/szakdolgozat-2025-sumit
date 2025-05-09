package com.example.sumit.ui.notes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val friendList by viewModel.friendList.collectAsState()

    var showingSummary by remember { mutableStateOf(false) }
    var showingShareDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(ViewNoteDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBack,
                contextButton = {
                    IconButton(
                        onClick = { showingShareDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = "Share this note"
                        )
                    }
                }
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

            OptionSwitcher(
                firstOptionText = stringResource(R.string.note),
                secondOptionText = stringResource(R.string.summary),
                secondOptionSelected = showingSummary,
                onOptionSwitch = { showingSummary = it }
            )
        }
    }

    if (showingShareDialog) {
        ShareNoteDialog(
            friendList = friendList,
            onShareWithFriends = viewModel::shareWithFriends,
            onDismissRequest = { showingShareDialog = false }
        )
    }
}

@Composable
fun NoteContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val paragraphs = content.split("\n")

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.large_padding))
    ) {
        paragraphs.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
    }
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

@Composable
fun OptionSwitcher(
    firstOptionText: String,
    secondOptionText: String,
    secondOptionSelected: Boolean,
    onOptionSwitch: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val noteButtonContainerColor = animateColorAsState(
        if (secondOptionSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.primary
    )
    val noteButtonContentColor = animateColorAsState(
        if (secondOptionSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onPrimary
    )
    val summaryButtonContainerColor = animateColorAsState(
        if (secondOptionSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.primaryContainer
    )
    val summaryButtonContentColor = animateColorAsState(
        if (secondOptionSelected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onPrimaryContainer
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.medium_padding))
            .border(width = 2.dp, color = Color.Black, shape = CircleShape),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onOptionSwitch(false) },
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.small_padding)),
            colors = ButtonDefaults.buttonColors(
                containerColor = noteButtonContainerColor.value,
                contentColor = noteButtonContentColor.value
            )
        ) {
            Text(firstOptionText)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { onOptionSwitch(true) },
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.small_padding)),
            colors = ButtonDefaults.buttonColors(
                containerColor = summaryButtonContainerColor.value,
                contentColor = summaryButtonContentColor.value
            )
        ) {
            Text(secondOptionText)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewSwitcherPreview() {
    OptionSwitcher(
        firstOptionText = "First",
        secondOptionText = "Second",
        secondOptionSelected = false,
        onOptionSwitch = { }
    )
}
