package com.sypark.finnhub.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.domain.model.ThemeMode

@Composable
fun SettingsRoute(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        var menuExpanded by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text("테마") },
            trailingContent = { Text(themeLabel(state.themeMode)) },
            modifier = Modifier.clickable { menuExpanded = true },
        )
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(themeLabel(mode)) },
                    onClick = { onIntent(SettingsIntent.ThemeChanged(mode)); menuExpanded = false },
                )
            }
        }
        SettingsDivider()
        ListItem(headlineContent = { Text("API 상태") }, trailingContent = { Text(apiStatusLabel(state.apiStatus)) })
        ListItem(headlineContent = { Text("데이터 출처") }, trailingContent = { Text("Finnhub") })
        SettingsDivider()
        ListItem(headlineContent = { Text("버전") }, trailingContent = { Text(state.versionName) })
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline),
    )
}

private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "시스템 설정"
    ThemeMode.DARK -> "다크"
    ThemeMode.LIGHT -> "라이트"
}

private fun apiStatusLabel(status: ApiStatus): String = when (status) {
    ApiStatus.OK -> "정상"
    ApiStatus.DEGRADED -> "지연"
    ApiStatus.UNKNOWN -> "확인 중"
}
