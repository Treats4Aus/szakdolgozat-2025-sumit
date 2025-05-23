package com.example.sumit.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.navigation.NavigationDestination
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

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
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoSelectViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                val isUriValid = try {
                    context.contentResolver.openInputStream(uiState.cameraPhotoUri).use {
                        it != null && it.available() > 0
                    }
                } catch (ex: Exception) {
                    false
                }
                if (isUriValid) {
                    viewModel.addPhotoFromCamera()
                } else {
                    Log.d(TAG, "Invalid uri: ${uiState.cameraPhotoUri}")
                }
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchCamera(viewModel, context, cameraLauncher)
            }
        }

    LaunchedEffect(uiState.savePhotosState) {
        if (uiState.savePhotosState == SavePhotosState.Complete) {
            viewModel.resetSavePhotoState()
            onNextStep()
        }

        if (uiState.useCamera != null) {
            if (uiState.useCamera == true) {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    launchCamera(viewModel, context, cameraLauncher)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
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
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.medium_padding))
            ) {
                PhotoGrid(
                    photos = uiState.photos,
                    onSelect = viewModel::selectPhoto,
                    onMove = viewModel::movePhoto,
                    modifier = Modifier.weight(1f)
                )

                AddPhotoButtons(
                    canContinue = uiState.photos.isNotEmpty(),
                    onGalleryClick = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    onCameraClick = {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(viewModel, context, cameraLauncher)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onContinueClick = {
                        viewModel.savePhotosToTemp()
                    }
                )
            }

            AnimatedVisibility(
                visible = uiState.savePhotosState == SavePhotosState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularLoadingScreenWithBackdrop(
                    indicatorSize = dimensionResource(R.dimen.progress_indicator_size)
                )
            }
        }

        if (cropState != null) {
            ImageCropperDialog(state = cropState)
        }

        if (uiState.selectedPhotoIndex != null) {
            ModalBottomSheet(
                onDismissRequest = viewModel::clearSelectedPhoto,
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = uiState.photos[uiState.selectedPhotoIndex!!].uri,
                        contentDescription = stringResource(
                            R.string.photo_number,
                            uiState.selectedPhotoIndex!!
                        )
                    )

                    BottomSheetOption(
                        icon = Icons.Default.Crop,
                        text = stringResource(R.string.crop_image),
                        onClick = {
                            viewModel.cropPhoto(uiState.selectedPhotoIndex!!, context)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    viewModel.clearSelectedPhoto()
                                }
                            }
                        }
                    )

                    HorizontalDivider(thickness = 2.dp)

                    BottomSheetOption(
                        icon = Icons.Default.Delete,
                        text = stringResource(R.string.delete_image),
                        onClick = {
                            viewModel.removePhoto(uiState.selectedPhotoIndex!!)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    viewModel.clearSelectedPhoto()
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
    photos: List<Photo>,
    onSelect: (Int) -> Unit,
    onMove: (LazyGridItemInfo, LazyGridItemInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val lazyGridState = rememberLazyGridState()
    val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        onMove(from, to)

        ViewCompat.performHapticFeedback(
            view,
            HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(R.dimen.photo_grid_min_width)),
        modifier = modifier,
        state = lazyGridState,
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.medium_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        itemsIndexed(photos, key = { _, photo -> photo.id }) { index, photo ->
            ReorderableItem(
                state = reorderableLazyGridState,
                key = photo.id
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                NotePhoto(
                    photo = photo.uri,
                    index = index,
                    onClick = onSelect,
                    modifier = Modifier
                        .shadow(elevation)
                        .longPressDraggableHandle(
                            onDragStarted = {
                                ViewCompat.performHapticFeedback(
                                    view,
                                    HapticFeedbackConstantsCompat.GESTURE_START
                                )
                            },
                            onDragStopped = {
                                ViewCompat.performHapticFeedback(
                                    view,
                                    HapticFeedbackConstantsCompat.GESTURE_END
                                )
                            }
                        )
                )
            }
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
        contentDescription = stringResource(R.string.photo_number, index),
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
    canContinue: Boolean,
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
            enabled = canContinue,
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

private fun launchCamera(
    viewModel: PhotoSelectViewModel,
    context: Context,
    launcher: ManagedActivityResultLauncher<Uri, Boolean>
) {
    val cameraPhotoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        context.createImageFile()
    )
    viewModel.updateCameraPhotoUri(cameraPhotoUri)
    launcher.launch(cameraPhotoUri)
}
