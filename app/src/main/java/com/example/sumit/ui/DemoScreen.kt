package com.example.sumit.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import java.io.File

@Preview(showBackground = true)
@Composable
fun DemoScreen(
    modifier: Modifier = Modifier,
    demoViewModel: DemoViewModel = viewModel()
) {
    val demoUiState by demoViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dataDir = File(context.filesDir, "tessdata")
    dataDir.mkdirs()
    val copiedFile = File(dataDir, "eng.traineddata")
    if (!copiedFile.exists()) {
        context.assets.open("eng.traineddata").use { input ->
            copiedFile.outputStream().use { output ->
                input.copyTo(output, 1024)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello",
            fontSize = 26.sp
        )

        Image(painter = painterResource(R.drawable.a35), contentDescription = null)

        Button(onClick = { demoViewModel.recognizeImage(
            context.filesDir.absolutePath,
            BitmapFactory.decodeResource(context.resources, R.drawable.a35)
        ) }) {
            Text(text = "Start")
        }

        Text(text = demoUiState.result)
    }
}
