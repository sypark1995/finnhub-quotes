// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/AppBottomBar.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.ShapePill
import com.sypark.finnhub.core.ui.theme.Spacing

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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.space4, vertical = Spacing.space3)
            .clip(ShapePill)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = Spacing.space2),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        BottomNavTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(ShapePill)
                    .clickable(onClick = { onTabSelected(tab) })
                    .padding(horizontal = Spacing.space3, vertical = Spacing.space1),
            ) {
                Box {
                    val icon = when (tab) {
                        BottomNavTab.HOME -> Icons.Filled.Home
                        BottomNavTab.SEARCH -> Icons.Filled.Search
                        BottomNavTab.ALERTS -> Icons.Filled.Notifications
                        BottomNavTab.SETTINGS -> Icons.Filled.Settings
                    }
                    Icon(imageVector = icon, contentDescription = tab.label, tint = tint, modifier = Modifier.size(24.dp))
                    if (tab == BottomNavTab.ALERTS && alertBadgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(AppTheme.extended.loss),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = alertBadgeCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                Text(text = tab.label, style = MaterialTheme.typography.labelSmall, color = tint)
            }
        }
    }
}

@Preview
@Composable
private fun AppBottomBarPreview() {
    FinnhubQuotesTheme { AppBottomBar(selectedTab = BottomNavTab.HOME, alertBadgeCount = 2, onTabSelected = {}) }
}
