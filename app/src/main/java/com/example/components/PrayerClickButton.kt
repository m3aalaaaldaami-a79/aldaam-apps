package com.example.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TumblerStyle
import com.example.ui.theme.CairoFontFamily

/**
 * Tactile, prominent circular prayer button with mechanical press feedback,
 * gold metallic ring, ripple animation, and Islamic aesthetic.
 */
@Composable
fun PrayerClickButton(
    onClick: () -> Unit,
    style: TumblerStyle = TumblerStyle.GOLD,
    label: String = "سَبِّحْ",
    sublabel: String? = "اضغط للعد",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 900f),
        label = "pressScale"
    )

    Box(
        modifier = modifier
            .testTag("prayer_click_button")
            .size(165.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 4.dp else 16.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.85f)
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        style.primaryColor.copy(alpha = 0.45f),
                        style.darkAccent.copy(alpha = 0.75f),
                        Color(0xFF06140E)
                    )
                )
            )
            .border(
                width = 4.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        style.lightAccent,
                        style.primaryColor,
                        style.darkAccent,
                        style.lightAccent,
                        style.primaryColor
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = style.lightAccent),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner decorative rings
        Canvas(modifier = Modifier.size(145.dp)) {
            // Inner gold ring
            drawCircle(
                color = style.primaryColor.copy(alpha = 0.5f),
                radius = size.minDimension / 2f - 4.dp.toPx(),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Dotted ring
            drawCircle(
                color = style.lightAccent.copy(alpha = 0.3f),
                radius = size.minDimension / 2f - 12.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = "تسبيح",
                tint = style.lightAccent,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    color = style.lightAccent.copy(alpha = 0.9f)
                )
            }
        }
    }
}
