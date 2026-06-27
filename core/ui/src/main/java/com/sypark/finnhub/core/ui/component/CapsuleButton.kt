// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/CapsuleButton.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.ShapePill
import com.sypark.finnhub.core.ui.theme.Spacing

@Composable
fun CapsuleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(ShapePill)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space5, vertical = Spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.space2),
    ) {
        icon?.invoke()
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

@Preview
@Composable
private fun CapsuleButtonPreview() {
    FinnhubQuotesTheme { CapsuleButton(text = "종목 검색", onClick = {}) }
}
