package com.sypark.finnhub.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ShapeExtraSmall = RoundedCornerShape(4.dp)
val ShapeSmall = RoundedCornerShape(8.dp)
val ShapeMedium = RoundedCornerShape(12.dp)
val ShapeLarge = RoundedCornerShape(16.dp)
val ShapeExtraLarge = RoundedCornerShape(28.dp)

// Custom UI redesign (2026-07-06) — soft rounded cards + capsule pills,
// replacing Material3's default component shapes.
val ShapeCard = RoundedCornerShape(20.dp)
val ShapePill = RoundedCornerShape(percent = 50)

val AppShapes = Shapes(
    extraSmall = ShapeExtraSmall,
    small = ShapeSmall,
    medium = ShapeMedium,
    large = ShapeLarge,
    extraLarge = ShapeExtraLarge,
)

// Elevation tokens (ui-design.md §2.4) — Dp, applied via Modifier.shadow or
// Card's `elevation` param at call sites, not bundled into a single object
// because M3 components take elevation as a direct Dp argument.
object Elevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
}
