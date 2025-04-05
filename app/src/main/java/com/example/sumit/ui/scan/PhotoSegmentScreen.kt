package com.example.sumit.ui.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.sumit.R
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.navigation.NavigationDestination

object PhotoSegmentDestination : NavigationDestination {
    override val route = "photo_segment"
    override val titleRes = R.string.extract_text
}

@Composable
fun PhotoSegmentScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(PhotoSegmentDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onCancel
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Hello")
        }
    }
}
