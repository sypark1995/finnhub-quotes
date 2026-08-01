// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/EmptyState.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.Spacing

@Composable
fun EmptyState(
    title: String,
    description: String,
    ctaLabel: String?,
    onCtaClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.space10),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        @Suppress("DEPRECATION")
        Icon(
            imageVector = Icons.Outlined.TrendingUp,
            contentDescription = null,
            modifier = Modifier.padding(bottom = Spacing.space4),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.space2, bottom = Spacing.space5),
        )
        if (ctaLabel != null && onCtaClick != null) {
            CapsuleButton(
                text = ctaLabel,
                onClick = onCtaClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Preview
@Composable
private fun EmptyStatePreview() {
    FinnhubQuotesTheme {
        EmptyState(
            title = "아직 관심종목이 없어요",
            description = "종목을 검색해 추가해 보세요",
            ctaLabel = "종목 검색",
            onCtaClick = {},
        )
    }
}
