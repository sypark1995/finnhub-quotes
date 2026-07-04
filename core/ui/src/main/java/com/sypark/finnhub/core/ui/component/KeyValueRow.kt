// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/KeyValueRow.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import androidx.compose.ui.tooling.preview.Preview
import com.sypark.finnhub.core.ui.theme.PriceTypographySmall
import com.sypark.finnhub.core.ui.theme.Spacing

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.space3),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = PriceTypographySmall, color = MaterialTheme.colorScheme.onBackground)
        }
        if (showDivider) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Preview
@Composable
private fun KeyValueRowPreview() {
    FinnhubQuotesTheme { KeyValueRow(label = "시가총액", value = "$3.01T") }
}
