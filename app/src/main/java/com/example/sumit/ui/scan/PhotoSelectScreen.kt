package com.example.sumit.ui.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.navigation.NavigationDestination
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }

    val cropState = viewModel.imageCropper.cropState

    val pickMediaLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            uris.forEach { uri ->
                Log.d(TAG, "Selected URI: $uri")
                viewModel.addPhoto(uri)
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { photoTaken ->
            if (photoTaken) {
                viewModel.addPhotoFromCamera()
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.launchCamera(context, cameraLauncher)
            }
        }

    LaunchedEffect(Unit) {
        if (uiState.useCamera != null) {
            if (uiState.useCamera == true) {
                viewModel.checkCameraPermission(context, permissionLauncher, cameraLauncher)
            } else {
                pickMediaLauncher
                    .launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            viewModel.disableDefaultLaunch()
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
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.medium_padding))
        ) {
            PhotoGrid(
                photos = uiState.photos,
                onDelete = { selectedPhotoIndex = it },
                modifier = Modifier.weight(1f)
            )

            AddPhotoButtons(
                onGalleryClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onCameraClick = {
                    viewModel.checkCameraPermission(context, permissionLauncher, cameraLauncher)
                },
                onContinueClick = viewModel::savePhotosToTemp
            )
        }

        if (cropState != null) {
            ImageCropperDialog(state = cropState)
        }

        if (selectedPhotoIndex != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedPhotoIndex = null },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
                    BottomSheetOption(
                        icon = Icons.Default.Crop,
                        text = stringResource(R.string.crop_image),
                        onClick = {
                            viewModel.cropPhoto(selectedPhotoIndex!!, context)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    selectedPhotoIndex = null
                                }
                            }
                        }
                    )

                    HorizontalDivider(thickness = 2.dp)

                    BottomSheetOption(
                        icon = Icons.Default.Delete,
                        text = stringResource(R.string.delete_image),
                        onClick = {
                            viewModel.removePhoto(selectedPhotoIndex!!)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    selectedPhotoIndex = null
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoGrid(
    photos: List<Uri>,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(R.dimen.photo_grid_min_width)),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.medium_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        itemsIndexed(photos) { index, photo ->
            NotePhoto(
                photo = photo,
                index = index,
                onClick = onDelete
            )
        }

        if (photos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                NoNotePhotos()
            }
        }
    }
}

@Composable
fun NotePhoto(
    photo: Uri,
    index: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = photo,
        contentDescription = stringResource(R.string.image_number, index),
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick(index) },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun NoNotePhotos(modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(320.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.no_photos_selected),
            modifier = modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun AddPhotoButtons(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = dimensionResource(R.dimen.small_padding))) {
        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))) {
            IconAndTextButton(
                icon = Icons.Default.ImageSearch,
                text = stringResource(R.string.open_gallery),
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            )

            IconAndTextButton(
                icon = Icons.Default.CameraAlt,
                text = stringResource(R.string.take_photo),
                onClick = onCameraClick,
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
fun IconAndTextButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.small_padding))
            )

            Text(text)
        }
    }
}

@Composable
fun BottomSheetOption(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.medium_padding),
                vertical = dimensionResource(R.dimen.large_padding)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.large_padding)),
            tint = Color.Black
        )

        Text(text, style = MaterialTheme.typography.displayMedium, color = Color.Black)
    }
}

@Preview
@Composable
private fun PhotoSelectScreenPreview() {
    PhotoSelectScreen(onCancel = { })
}
