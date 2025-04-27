package com.example.sumit.ui.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.sumit.R
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
    modifier: Modifier = Modifier
) {
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
        Column(modifier = modifier.padding(innerPadding)) {
            Box(modifier = Modifier.weight(1f)) {
                Text("Hello")
            }

            Row {
                Button(onClick = { }) {
                    Text("Note")
                }

                Button(onClick = { }) {
                    Text("Summary")
                }
            }
        }
    }
}
