package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ArenaDarkColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = ElectricPurpleContainer,
    onPrimaryContainer = ElectricPurpleBright,
    secondary = ElectricBlue,
    onSecondary = Color(0xFF04141F),
    secondaryContainer = ElectricBlueContainer,
    onSecondaryContainer = ElectricBlueBright,
    tertiary = TrophyGold,
    onTertiary = Color(0xFF1F1400),
    tertiaryContainer = StatusAmberContainer,
    onTertiaryContainer = TrophyGold,
    background = ArenaBg,
    onBackground = TextPrimaryDark,
    surface = ArenaSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ArenaSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerLowest = ArenaBg,
    surfaceContainerLow = ArenaSurface,
    surfaceContainer = ArenaSurfaceElevated,
    surfaceContainerHigh = ArenaSurfaceHighest,
    surfaceContainerHighest = Color(0xFF2B3554),
    outline = ArenaOutline,
    outlineVariant = ArenaOutlineSubtle,
    error = StatusCrimson,
    onError = Color.White,
    errorContainer = StatusCrimsonContainer,
    onErrorContainer = Color(0xFFFCA5A5)
)

private val ArenaLightColorScheme = lightColorScheme(
    primary = ElectricPurpleLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = ElectricBlueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = ArenaBgLight,
    onBackground = TextPrimaryLight,
    surface = ArenaSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = ArenaSurfaceElevatedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = ArenaOutlineLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

val BattleArenaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BattleArenaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ArenaDarkColorScheme else ArenaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BattleArenaTypography,
        shapes = BattleArenaShapes,
        content = content
    )
}
