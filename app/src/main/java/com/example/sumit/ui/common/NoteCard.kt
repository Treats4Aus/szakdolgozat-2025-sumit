package com.example.sumit.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.sumit.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    title: String,
    content: String,
    extraInfo: String,
    canModify: Boolean,
    onNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = { },
    onDeleteClick: () -> Unit = { }
) {
    var expanded by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onNoteClick,
                    onLongClick = {
                        if (canModify) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            expanded = true
                        }
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimensionResource(R.dimen.card_elevation)
            )
        ) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
                Text(
                    text = title,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.medium_padding)),
                    style = MaterialTheme.typography.displayMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row {
                    Text(
                        text = content.replace("\n", " "),
                        modifier = Modifier
                            .weight(3f)
                            .padding(end = dimensionResource(R.dimen.medium_padding)),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = extraInfo,
                        modifier = Modifier.weight(2f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit_note)) },
                onClick = onEditClick
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete_note)) },
                onClick = onDeleteClick
            )
        }
    }
}

@Preview
@Composable
private fun NoteCardPreview() {
    NoteCard(
        title = "Preview",
        content = "This is a preview",
        extraInfo = "Last modified: 2025. május 10.",
        canModify = true,
        onNoteClick = { },
        onEditClick = { },
        onDeleteClick = { }
    )
}
