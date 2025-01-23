package com.example.sumit.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R

@Preview(showBackground = true)
@Composable
fun DemoScreen(
    modifier: Modifier = Modifier,
    demoViewModel: DemoViewModel = viewModel()
) {
    val demoUiState by demoViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello",
            fontSize = 26.sp
        )

        Image(painter = painterResource(R.drawable.test), contentDescription = null)

        Button(onClick = demoViewModel::recognizeImage) {
            Text(text = "Start")
        }

        Text(text = demoUiState.result)
    }
}
