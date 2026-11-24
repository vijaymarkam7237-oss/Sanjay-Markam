package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ArenaBg
import com.example.ui.theme.ArenaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TrophyGold

/**
 * Custom esports geometric stadium & spotlight backdrop drawn cleanly via Compose Canvas.
 * Uses DrawScope size so it scales cleanly across phones, foldables, and tablets.
 */
@Composable
fun EsportsBannerCanvas(
    style: String,
    modifier: Modifier = Modifier,
    contentDescriptionText: String = "Battle Arena decorative esports banner"
) {
    val (startColor, midColor, accentColor) = when (style.uppercase()) {
        "STORM" -> Triple(Color(0xFF140D2E), Color(0xFF281154), ElectricBlue)
        "NEON" -> Triple(Color(0xFF0A192F), Color(0xFF112C4E), ElectricPurple)
        "SOLAR" -> Triple(Color(0xFF231126), Color(0xFF3E183D), TrophyGold)
        else -> Triple(Color(0xFF0E1326), Color(0xFF26124A), ElectricBlue)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = contentDescriptionText }
    ) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(startColor, midColor, ArenaBg),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        // Atmospheric radial spotlight glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor.copy(alpha = 0.32f), Color.Transparent),
                center = Offset(w * 0.82f, h * 0.25f),
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.82f, h * 0.25f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ElectricPurple.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(w * 0.18f, h * 0.85f),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(w * 0.18f, h * 0.85f)
        )

        // Tactical diagonal cyber grid lines
        val step = w / 7f
        for (i in -2..8) {
            val x = i * step
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(x, 0f),
                end = Offset(x - h * 0.45f, h),
                strokeWidth = 1.5f
            )
        }

        // Geometric shield silhouette on right side
        val cx = w * 0.84f
        val cy = h * 0.52f
        val r = minOf(w, h) * 0.34f
        val shield = Path().apply {
            moveTo(cx, cy - r)
            lineTo(cx + r * 0.82f, cy - r * 0.45f)
            lineTo(cx + r * 0.68f, cy + r * 0.45f)
            lineTo(cx, cy + r)
            lineTo(cx - r * 0.68f, cy + r * 0.45f)
            lineTo(cx - r * 0.82f, cy - r * 0.45f)
            close()
        }
        drawPath(
            path = shield,
            color = accentColor.copy(alpha = 0.12f)
        )
        drawPath(
            path = shield,
            color = accentColor.copy(alpha = 0.45f),
            style = Stroke(width = 2.5f)
        )

        // Glowing horizon energy bar
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, ElectricPurple, ElectricBlue, Color.Transparent)
            ),
            start = Offset(0f, h - 2f),
            end = Offset(w, h - 2f),
            strokeWidth = 4f
        )
    }
}

/**
 * Custom original esports emblem avatar selectable by players.
 */
@Composable
fun PlayerEmblemAvatar(
    avatarKey: String,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    val (ringColor, bgTop, accent) = when (avatarKey.uppercase()) {
        "PLASMA_WOLF" -> Triple(ElectricPurple, Color(0xFF28124B), ElectricBlue)
        "SOLAR_TITAN" -> Triple(TrophyGold, Color(0xFF31200E), StatusAmber)
        "EMERALD_GHOST" -> Triple(StatusEmerald, Color(0xFF0E2B24), ElectricBlue)
        else -> Triple(ElectricBlue, Color(0xFF121D38), ElectricPurple)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(bgTop, ArenaSurfaceElevated))
            )
            .border(2.dp, ringColor, CircleShape)
            .semantics { contentDescription = "Player avatar emblem $avatarKey" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // Inner crest polygon
            val crest = Path().apply {
                moveTo(cx, h * 0.18f)
                lineTo(w * 0.78f, h * 0.34f)
                lineTo(w * 0.72f, h * 0.68f)
                lineTo(cx, h * 0.84f)
                lineTo(w * 0.28f, h * 0.68f)
                lineTo(w * 0.22f, h * 0.34f)
                close()
            }
            drawPath(crest, color = accent.copy(alpha = 0.22f))
            drawPath(crest, color = ringColor, style = Stroke(width = w * 0.045f))

            // Glowing visor slash
            val visor = Path().apply {
                moveTo(w * 0.30f, cy - h * 0.04f)
                lineTo(cx, cy + h * 0.04f)
                lineTo(w * 0.70f, cy - h * 0.04f)
                lineTo(w * 0.64f, cy + h * 0.09f)
                lineTo(cx, cy + h * 0.14f)
                lineTo(w * 0.36f, cy + h * 0.09f)
                close()
            }
            drawPath(visor, color = Color.White)
            drawCircle(
                color = ringColor,
                radius = w * 0.07f,
                center = Offset(cx, h * 0.33f)
            )
        }
    }
}
