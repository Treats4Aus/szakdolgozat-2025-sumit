package com.example.sumit.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sumit.ui.home.HomeDestination
import com.example.sumit.ui.home.HomeScreen
import com.example.sumit.ui.scan.PhotoSelectDestination
import com.example.sumit.ui.scan.PhotoSelectScreen

@Composable
fun SumItNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(onNewScan = {
                navController.navigate("${PhotoSelectDestination.route}/$it")
            })
        }

        composable(
            route = PhotoSelectDestination.routeWithArgs,
            arguments = listOf(navArgument(PhotoSelectDestination.selectModeArg) {
                type = NavType.BoolType
            }),
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            PhotoSelectScreen(onCancel = { navController.navigateUp() })
        }
    }
}
