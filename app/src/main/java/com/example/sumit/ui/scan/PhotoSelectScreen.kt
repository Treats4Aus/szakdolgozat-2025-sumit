package com.example.sumit.ui.scan

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.navigation.NavigationDestination

private const val TAG = "PhotoSelectScreen"

object PhotoSelectDestination : NavigationDestination {
    override val route = "photo_select"
    override val titleRes = R.string.select_photos
    const val selectModeArg = "selectModeArg"
    val routeWithArgs = "$route/{$selectModeArg}"
}

@Composable
fun PhotoSelectScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoSelectViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d(TAG, "Selected URI: $uri")
                viewModel.addPhoto(uri)
            }
        }

    LaunchedEffect(Unit) {
        if (!uiState.useCamera) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(PhotoSelectDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onCancel
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(uiState.photos) { photo ->
                    AsyncImage(model = photo, contentDescription = null)
                }
            }

            AddPhotoButtons(
                onGalleryClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onCameraClick = { },
                onContinueClick = viewModel::savePhotosToTemp
            )
        }
    }
}

@Composable
fun AddPhotoButtons(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Button(onClick = onGalleryClick) {
            Row {
                Icon(
                    Icons.Default.ImageSearch,
                    contentDescription = stringResource(R.string.select_from_gallery)
                )

                Text(stringResource(R.string.select_from_gallery))
            }
        }

        Button(onClick = onCameraClick) {
            Row {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = stringResource(R.string.take_photo)
                )

                Text(stringResource(R.string.take_photo))
            }
        }

        Button(onClick = onContinueClick) {
            Text("Next")
        }
    }
}

@Preview
@Composable
private fun PhotoSelectScreenPreview() {
    PhotoSelectScreen(onCancel = { })
}
