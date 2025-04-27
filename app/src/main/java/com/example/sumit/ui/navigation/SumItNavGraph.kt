package com.example.sumit.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.example.sumit.ui.notes.EditNoteDestination
import com.example.sumit.ui.notes.EditNoteScreen
import com.example.sumit.ui.notes.ViewNoteDestination
import com.example.sumit.ui.notes.ViewNoteScreen
import com.example.sumit.ui.scan.PhotoProcessDestination
import com.example.sumit.ui.scan.PhotoProcessScreen
import com.example.sumit.ui.scan.PhotoSegmentDestination
import com.example.sumit.ui.scan.PhotoSegmentScreen
import com.example.sumit.ui.scan.PhotoSelectDestination
import com.example.sumit.ui.scan.PhotoSelectScreen

private const val SLIDE_DURATION = 400

@Composable
fun SumItNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(SLIDE_DURATION, easing = EaseOut)
            )
        },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(SLIDE_DURATION, easing = EaseOut)
            )
        }
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onNewScan = {
                    navController.navigate("${PhotoSelectDestination.route}/$it")
                },
                onViewNote = {
                    navController.navigate("${ViewNoteDestination.route}/$it")
                },
                onEditNote = {
                    navController.navigate("${EditNoteDestination.route}/$it")
                }
            )
        }

        composable(
            route = PhotoSelectDestination.routeWithArgs,
            arguments = listOf(navArgument(PhotoSelectDestination.selectModeArg) {
                type = NavType.BoolType
            })
        ) {
            PhotoSelectScreen(
                onCancel = { navController.navigateUp() },
                onNextStep = { navController.navigate(PhotoSegmentDestination.route) }
            )
        }

        composable(route = PhotoSegmentDestination.route) {
            PhotoSegmentScreen(
                onCancel = { navController.navigateUp() },
                onNextStep = { navController.navigate(PhotoProcessDestination.route) }
            )
        }

        composable(route = PhotoProcessDestination.route) {
            PhotoProcessScreen(
                onCancel = { navController.navigateUp() },
                onSavingDone = {
                    navController.popBackStack(
                        route = HomeDestination.route,
                        inclusive = false
                    )
                }
            )
        }

        composable(
            route = ViewNoteDestination.routeWithArgs,
            arguments = listOf(navArgument(ViewNoteDestination.noteIdArg) {
                type = NavType.IntType
            })
        ) {
            ViewNoteScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = EditNoteDestination.routeWithArgs,
            arguments = listOf(navArgument(EditNoteDestination.noteIdArg) {
                type = NavType.IntType
            })
        ) {
            EditNoteScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}
