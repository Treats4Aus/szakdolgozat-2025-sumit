package com.example.sumit.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PhotoSelectScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.White)) {
        Text(text = "Select photos")
        Button(onClick = onCancel) {
            Text(text = "Cancel")
        }
    }
}

@Preview
@Composable
private fun PhotoSelectScreenPreview() {
    PhotoSelectScreen(onCancel = { })
}
