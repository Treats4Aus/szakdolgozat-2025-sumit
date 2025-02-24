package com.example.sumit.ui.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScanScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "Scan")
        Button(onClick = onCancel) {
            Text(text = "Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanScreenPreview() {
    ScanScreen(onCancel = { })
}
