package com.example.sumit.ui.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.sumit.R
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
    modifier: Modifier = Modifier
) {
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
        Column(modifier = modifier.padding(innerPadding)) {
            Text("Woof")
        }
    }
}
