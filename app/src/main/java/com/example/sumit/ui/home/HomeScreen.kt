package com.example.sumit.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

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
    onNewScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(HomeTab.Recent) }

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

    Scaffold(bottomBar = {
        SumItBottomNavigationBar(
            currentTab = currentTab,
            onTabPressed = { currentTab = it },
            navigationItemContentList = navigationItemContentList
        )
    },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewScan) {
                Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "Scan notes")
            }
        }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Text(text = "Home")
            when (currentTab) {
                HomeTab.Recent -> RecentNotesTab()
                HomeTab.Notes -> MyNotesTab()
                HomeTab.Profile -> ProfileTab()
            }
        }
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
                    Text(navItem.text)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(onNewScan = { })
}
