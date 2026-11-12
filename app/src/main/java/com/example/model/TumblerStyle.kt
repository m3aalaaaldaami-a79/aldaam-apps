package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class TumblerStyle(
    val titleAr: String,
    val primaryColor: Color,
    val lightAccent: Color,
    val darkAccent: Color,
    val digitColor: Color,
    val wheelBackgroundTop: Color,
    val wheelBackgroundCenter: Color,
    val wheelBackgroundBottom: Color,
    val frameBorder: Color,
    val frameBackground: Color
) {
    GOLD(
        titleAr = "الذهب الملكي",
        primaryColor = Color(0xFFE5B842),
        lightAccent = Color(0xFFFFF2B2),
        darkAccent = Color(0xFF8A6510),
        digitColor = Color(0xFF141005), // Deep crisp bold black on gold cylinder
        wheelBackgroundTop = Color(0xFF8C6512),
        wheelBackgroundCenter = Color(0xFFF5CE58), // Bright gold cylinder center
        wheelBackgroundBottom = Color(0xFF6E4E09),
        frameBorder = Color(0xFFF3CA5A),
        frameBackground = Color(0xFF1C1508)
    ),
    SILVER(
        titleAr = "الكروم الفضي",
        primaryColor = Color(0xFFCBD5E1),
        lightAccent = Color(0xFFFFFFFF),
        darkAccent = Color(0xFF64748B),
        digitColor = Color(0xFF0F172A), // Crisp bold black on chrome cylinder
        wheelBackgroundTop = Color(0xFF64748B),
        wheelBackgroundCenter = Color(0xFFE2E8F0), // Bright chrome cylinder center
        wheelBackgroundBottom = Color(0xFF475569),
        frameBorder = Color(0xFF94A3B8),
        frameBackground = Color(0xFF0F172A)
    ),
    BRONZE(
        titleAr = "البرونز العتيق",
        primaryColor = Color(0xFFD9884E),
        lightAccent = Color(0xFFFFD4B2),
        darkAccent = Color(0xFF6B3610),
        digitColor = Color(0xFF1A0A02), // Deep crisp bold black on bronze cylinder
        wheelBackgroundTop = Color(0xFF7A3E13),
        wheelBackgroundCenter = Color(0xFFE09A65), // Bright bronze cylinder center
        wheelBackgroundBottom = Color(0xFF572A09),
        frameBorder = Color(0xFFA56336),
        frameBackground = Color(0xFF1F1108)
    );

    fun getWheelGradient(): Brush {
        return Brush.verticalGradient(
            0.0f to wheelBackgroundTop,
            0.2f to wheelBackgroundCenter,
            0.5f to lightAccent,
            0.8f to wheelBackgroundCenter,
            1.0f to wheelBackgroundBottom
        )
    }

    fun getFrameGradient(): Brush {
        return Brush.verticalGradient(
            0.0f to frameBackground,
            0.5f to darkAccent.copy(alpha = 0.6f),
            1.0f to frameBackground
        )
    }
}
