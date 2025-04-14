package com.example.sumit.ui.scan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "PhotoSegmentScreen"

object PhotoSegmentDestination : NavigationDestination {
    override val route = "photo_segment"
    override val titleRes = R.string.extract_text
}

@Composable
fun PhotoSegmentScreen(
    onCancel: () -> Unit,
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoSegmentViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(PhotoSegmentDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onCancel
            )
        }
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
                PhotoList(
                    photos = uiState.photos,
                    onSelect = viewModel::selectPhoto,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.small_padding))) {
                    Button(
                        onClick = onNextStep,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(stringResource(R.string.next))
                    }
                }
            }
        }

        if (uiState.selectedPhotoIndex != null) {
            ModalBottomSheet(
                onDismissRequest = viewModel::clearSelectedPhoto,
                sheetState = sheetState
            ) {
                var sliderPosition by remember { mutableFloatStateOf(2f) }

                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.use_slider),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Box(
                        modifier = Modifier
                            .padding(vertical = dimensionResource(R.dimen.medium_padding))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    uiState.adjustedImage
                                        ?: uiState.photos[uiState.selectedPhotoIndex!!].uri
                                )
                                .crossfade(400)
                                .build(),
                            contentDescription = stringResource(
                                R.string.photo_number,
                                uiState.selectedPhotoIndex!!
                            )
                        )

                        androidx.compose.animation.AnimatedVisibility(
                            visible = uiState.isAdjustmentRunning,
                            modifier = Modifier.matchParentSize(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            CircularLoadingScreenWithBackdrop()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.thinner_lines),
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            stringResource(R.string.thicker_lines),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        onValueChangeFinished = {
                            viewModel.adjustSelectedPhoto(sliderPosition.roundToInt())
                        },
                        steps = 3,
                        valueRange = 0f..4f,
                        thumb = {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.width(dimensionResource(R.dimen.slider_thumb_width)),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                viewModel.clearSelectedPhoto()
                            }
                        }
                    }) {
                        Text(stringResource(R.string.done))
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoList(
    photos: List<SegmentedPhoto>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.medium_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        itemsIndexed(photos) { index, photo ->
            SegmentedPhoto(
                photo = photo,
                index = index,
                onClick = onSelect,
                modifier = Modifier.clip(MaterialTheme.shapes.small)
            )
        }
    }
}

@Composable
fun SegmentedPhoto(
    photo: SegmentedPhoto,
    index: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = photo.uri,
            contentDescription = stringResource(R.string.photo_number, index),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(index) },
            contentScale = ContentScale.FillWidth
        )

        AnimatedVisibility(
            visible = photo.isProcessing,
            modifier = Modifier.matchParentSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularLoadingScreenWithBackdrop()
        }
    }
}
