// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/AppBottomBar.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme

enum class BottomNavTab(val label: String) {
    HOME("홈"),
    SEARCH("검색"),
    ALERTS("알림"),
    SETTINGS("설정"),
}

@Composable
fun AppBottomBar(
    selectedTab: BottomNavTab,
    alertBadgeCount: Int,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        BottomNavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = {
                    val icon = when (tab) {
                        BottomNavTab.HOME -> Icons.Filled.Home
                        BottomNavTab.SEARCH -> Icons.Filled.Search
                        BottomNavTab.ALERTS -> Icons.Filled.Notifications
                        BottomNavTab.SETTINGS -> Icons.Filled.Settings
                    }
                    if (tab == BottomNavTab.ALERTS && alertBadgeCount > 0) {
                        BadgedBox(badge = { Badge { Text(alertBadgeCount.toString()) } }) {
                            Icon(imageVector = icon, contentDescription = tab.label, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Icon(imageVector = icon, contentDescription = tab.label, modifier = Modifier.size(24.dp))
                    }
                },
                label = { Text(tab.label) },
            )
        }
    }
}

@Preview
@Composable
private fun AppBottomBarPreview() {
    FinnhubQuotesTheme { AppBottomBar(selectedTab = BottomNavTab.HOME, alertBadgeCount = 2, onTabSelected = {}) }
}
