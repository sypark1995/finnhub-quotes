// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/QuoteRowSkeleton.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.ShapeCard
import com.sypark.finnhub.core.ui.theme.ShapeExtraSmall
import com.sypark.finnhub.core.ui.theme.ShapeSmall
import com.sypark.finnhub.core.ui.theme.Spacing
import com.sypark.finnhub.core.ui.util.shimmerEffect

@Composable
fun QuoteRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.quoteRowHeight)
            .clip(ShapeCard)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = Spacing.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = MaterialTheme.colorScheme.outline, shape = ShapeSmall)
                .shimmerEffect(),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.space3),
            verticalArrangement = Arrangement.spacedBy(Spacing.space1),
        ) {
            Box(Modifier.width(64.dp).height(16.dp).background(MaterialTheme.colorScheme.outline, ShapeExtraSmall).shimmerEffect())
            Box(Modifier.width(100.dp).height(14.dp).background(MaterialTheme.colorScheme.outline, ShapeExtraSmall).shimmerEffect())
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(Spacing.space1)) {
            Box(Modifier.width(72.dp).height(18.dp).background(MaterialTheme.colorScheme.outline, ShapeExtraSmall).shimmerEffect())
            Box(Modifier.width(48.dp).height(14.dp).background(MaterialTheme.colorScheme.outline, ShapeExtraSmall).shimmerEffect())
        }
    }
}

@Preview
@Composable
private fun QuoteRowSkeletonPreview() {
    FinnhubQuotesTheme { QuoteRowSkeleton() }
}
