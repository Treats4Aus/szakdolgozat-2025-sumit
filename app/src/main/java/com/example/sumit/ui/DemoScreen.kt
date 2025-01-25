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
import androidx.compose.ui.graphics.asImageBitmap
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

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello",
            fontSize = 26.sp
        )

        if (demoUiState.segmented == null) {
            Image(painter = painterResource(R.drawable.demo), contentDescription = "Original image")
        } else {
            Image(bitmap = demoUiState.segmented!!.asImageBitmap(), contentDescription = "Segmented image")
        }

        Button(onClick = { demoViewModel.recognizeImage(BitmapFactory.decodeResource(context.resources, R.drawable.demo)) }) {
            Text(text = "Start")
        }

        Text(text = demoUiState.result)
    }
}
