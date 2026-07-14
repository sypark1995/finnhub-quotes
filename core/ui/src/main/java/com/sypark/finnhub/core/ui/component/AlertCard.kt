// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/AlertCard.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.Spacing

@Composable
fun AlertCard(
    symbol: String,
    conditionText: String,
    isEnabled: Boolean,
    triggeredText: String?,
    onToggleEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = symbol, style = MaterialTheme.typography.titleMedium)
            Text(
                text = conditionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (triggeredText != null) {
                Text(
                    text = triggeredText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = isEnabled, onCheckedChange = onToggleEnabled)
    }
}

@Preview
@Composable
private fun AlertCardPreview() {
    FinnhubQuotesTheme {
        AlertCard(
            symbol = "AAPL",
            conditionText = "$210.00 이상 도달 시 알림",
            isEnabled = true,
            triggeredText = null,
            onToggleEnabled = {},
        )
    }
}
