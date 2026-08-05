// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/StaggeredListItem.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun StaggeredListItem(
    index: Int,
    key: Any = index,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // Keyed on `key` (an item's stable identity), not `index`: if callers pass a
    // reorderable list, keying on `index` alone would restart this entrance
    // animation for every slot on every reorder, and rapid successive reorders
    // (e.g. many DB writes emitting in quick succession) can repeatedly cancel
    // and relaunch the delay before it ever completes, leaving `visible` stuck
    // false and the item invisible until an unrelated full recomposition.
    var visible by remember(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        delay(min(index, 10) * 30L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 4 },
        modifier = modifier,
    ) {
        content()
    }
}
