// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/AppSnackbarHost.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sypark.finnhub.core.ui.theme.ShapePill
import com.sypark.finnhub.core.ui.theme.Spacing

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        Text(
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = Spacing.space4, vertical = Spacing.space2)
                .clip(ShapePill)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
        )
    }
}
