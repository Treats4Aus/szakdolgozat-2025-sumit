package com.example.sumit.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.sumit.R
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.home.notes.MyNotesTab
import com.example.sumit.ui.home.profile.ProfileTab
import com.example.sumit.ui.home.recent.RecentNotesTab
import com.example.sumit.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

enum class HomeTab {
    Recent, Notes, Profile
}

private data class NavigationItemContent(
    val page: HomeTab,
    val icon: ImageVector,
    val text: String
)

@Composable
fun HomeScreen(
    onNewScan: (Boolean) -> Unit,
    onViewNote: (Int) -> Unit,
    onEditNote: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(HomeTab.Recent) }
    var expanded by remember { mutableStateOf(false) }

    val navigationItemContentList = listOf(
        NavigationItemContent(
            page = HomeTab.Recent,
            icon = Icons.Filled.Home,
            text = "Home"
        ),
        NavigationItemContent(
            page = HomeTab.Notes,
            icon = Icons.AutoMirrored.Filled.StickyNote2,
            text = "Notes"
        ),
        NavigationItemContent(
            page = HomeTab.Profile,
            icon = Icons.Filled.Person,
            text = "Profile"
        )
    )

    Box(modifier = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) { expanded = false }) {
        Scaffold(
            topBar = {
                SumItAppBar(
                    title = stringResource(HomeDestination.titleRes),
                    canNavigateBack = false
                )
            },
            bottomBar = {
                SumItBottomNavigationBar(
                    currentTab = currentTab,
                    onTabPressed = { currentTab = it },
                    navigationItemContentList = navigationItemContentList
                )
            },
            floatingActionButton = {
                NewScanFAB(
                    expanded = expanded,
                    onFABClick = { expanded = !expanded },
                    onNewScan = onNewScan
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    HomeTab.Recent -> RecentNotesTab(
                        onViewOwnedNote = onViewNote,
                        onViewSharedNote = { }
                    )

                    HomeTab.Notes -> MyNotesTab(
                        onViewNote = onViewNote,
                        onEditNote = onEditNote
                    )

                    HomeTab.Profile -> ProfileTab()
                }
            }
        }
    }
}

@Composable
private fun NewScanFAB(
    expanded: Boolean,
    onFABClick: () -> Unit,
    onNewScan: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_gap)),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_gap))) {
                NewScanOption(
                    icon = Icons.Default.CameraAlt,
                    text = stringResource(R.string.take_photo),
                    modifier = Modifier.clickable { onNewScan(true) }
                )

                NewScanOption(
                    icon = Icons.Default.Upload,
                    text = stringResource(R.string.select_from_gallery),
                    modifier = Modifier.clickable { onNewScan(false) }
                )
            }
        }

        FloatingActionButton(onClick = onFABClick) {
            Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "Scan notes")
        }
    }
}

@Composable
fun NewScanOption(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .width(dimensionResource(R.dimen.floating_card_width))
            .background(colorResource(R.color.transparent_tile))
            .padding(dimensionResource(R.dimen.medium_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.medium_padding))
        )
        Text(text)
    }
}

@Composable
private fun SumItBottomNavigationBar(
    currentTab: HomeTab,
    onTabPressed: ((HomeTab) -> Unit),
    navigationItemContentList: List<NavigationItemContent>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentTab == navItem.page,
                onClick = { onTabPressed(navItem.page) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.text
                    )
                },
                label = {
                    Text(
                        text = navItem.text,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}
