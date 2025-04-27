package com.example.sumit.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.sumit.R
import com.example.sumit.data.notes.Note

@Composable
fun EditNoteScreen(
    processedNote: Note,
    onTitleEdit: (String) -> Unit,
    onContentEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        OutlinedTextField(
            value = processedNote.title,
            onValueChange = onTitleEdit,
            label = { Text(stringResource(R.string.title)) }
        )

        OutlinedTextField(
            value = processedNote.content,
            onValueChange = onContentEdit,
            label = { Text(stringResource(R.string.content)) }
        )
    }
}
