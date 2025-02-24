package com.example.sumit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sumit.ui.home.HomeScreen
import com.example.sumit.ui.scan.ScanScreen

enum class SumItScreen {
    Home,
    Scan
}

@Composable
fun SumItApp(
    navController: NavHostController = rememberNavController()
) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SumItScreen.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = SumItScreen.Home.name) {
                HomeScreen(onNewScan = { navController.navigate(SumItScreen.Scan.name) })
            }
            composable(route = SumItScreen.Scan.name) {
                ScanScreen(onCancel = { navController.navigateUp() })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SumItAppPreview() {
    SumItApp()
}
