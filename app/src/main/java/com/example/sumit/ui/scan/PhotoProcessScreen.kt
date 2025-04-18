package com.example.sumit.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
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
    val processState by viewModel.processState

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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.progress_indicator_size))
                    .padding(bottom = dimensionResource(R.dimen.medium_padding))
            )

            Text(stringResource(processState.descriptionRes))
        }
    }
}
