package com.example.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TumblerStyle
import com.example.ui.theme.CairoFontFamily

private val ARABIC_DIGITS = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
private val WESTERN_DIGITS = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

/**
 * Authentic 3D Cylindrical Combination Luggage Lock Counter.
 * Interactive, high-contrast, clickable everywhere with realistic rolling animations.
 */
@Composable
fun LuggageLockTumbler(
    count: Int,
    minDigits: Int = 3,
    style: TumblerStyle = TumblerStyle.GOLD,
    useArabicNumerals: Boolean = true,
    isCountingDown: Boolean = false,
    onClick: (() -> Unit)? = null,
    onWheelRoll: ((wheelIndex: Int, delta: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val nonNegativeCount = count.coerceAtLeast(0)
    val rawString = nonNegativeCount.toString()
    val padded = rawString.padStart(minDigits, '0')
    val digits = padded.map { it.digitToIntOrNull() ?: 0 }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag("luggage_lock_tumbler")
            .shadow(18.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.8f))
            .clip(RoundedCornerShape(22.dp))
            .background(style.getFrameGradient())
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        style.lightAccent,
                        style.primaryColor,
                        style.darkAccent,
                        style.lightAccent.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        // Decorative rivets/screws on lock casing corners
        LockRivets(color = style.lightAccent)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main Tumbler Lock Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left lock alignment pointer / reading line indicator notch
                LockIndexPointer(style = style, pointingRight = true)

                // Wheels Container Window with metallic beveled housing
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0D1117))
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Black,
                                    style.darkAccent,
                                    style.primaryColor.copy(alpha = 0.6f),
                                    Color.Black
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        digits.forEachIndexed { index, digit ->
                            SingleTumblerWheel(
                                targetDigit = digit,
                                wheelIndex = index,
                                style = style,
                                useArabicNumerals = useArabicNumerals,
                                isCountingDown = isCountingDown,
                                onClick = onClick,
                                onWheelRoll = onWheelRoll
                            )

                            // Divider knurled spacer between wheels
                            if (index < digits.size - 1) {
                                TumblerWheelDivider(style = style)
                            }
                        }
                    }

                    // Red combination lock reading alignment line
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val midY = size.height / 2f
                        drawLine(
                            color = Color(0x77FF2222),
                            start = Offset(0f, midY),
                            end = Offset(size.width, midY),
                            strokeWidth = 2f
                        )
                    }
                }

                // Right lock alignment pointer / reading line indicator notch
                LockIndexPointer(style = style, pointingRight = false)
            }
        }
    }
}

/**
 * Individual 3D rotary tumbler cylinder wheel.
 * Features bright high-contrast metallic cylinder body, bold black numbers,
 * top/bottom curved perspective numbers, and animated vertical rolling.
 */
@Composable
fun SingleTumblerWheel(
    targetDigit: Int,
    wheelIndex: Int,
    style: TumblerStyle,
    useArabicNumerals: Boolean,
    isCountingDown: Boolean,
    onClick: (() -> Unit)?,
    onWheelRoll: ((wheelIndex: Int, delta: Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val digitStrings = if (useArabicNumerals) ARABIC_DIGITS else WESTERN_DIGITS

    Box(
        modifier = modifier
            .width(62.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(style.getWheelGradient())
            .border(1.dp, style.darkAccent.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .pointerInput(wheelIndex) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onDragEnd = {
                        if (kotlin.math.abs(totalDrag) < 15f) {
                            // It was a tap! Trigger click
                            onClick?.invoke()
                        } else if (onWheelRoll != null) {
                            if (totalDrag < -25f) {
                                onWheelRoll(wheelIndex, 1)
                            } else if (totalDrag > 25f) {
                                onWheelRoll(wheelIndex, -1)
                            }
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Animated rolling cylinder content
        AnimatedContent(
            targetState = targetDigit,
            transitionSpec = {
                if (isCountingDown) {
                    // Roll downwards
                    (slideInVertically(animationSpec = tween(220)) { -it } + fadeIn(tween(120)))
                        .togetherWith(slideOutVertically(animationSpec = tween(220)) { it } + fadeOut(tween(120)))
                } else {
                    // Roll upwards
                    (slideInVertically(animationSpec = tween(220)) { it } + fadeIn(tween(120)))
                        .togetherWith(slideOutVertically(animationSpec = tween(220)) { -it } + fadeOut(tween(120)))
                }
            },
            label = "WheelRoll_$wheelIndex"
        ) { currentDigit ->
            val prevDigit = (currentDigit - 1 + 10) % 10
            val nextDigit = (currentDigit + 1) % 10

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
            ) {
                // Top previous digit (curved backwards in 3D perspective)
                Text(
                    text = digitStrings[prevDigit],
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = style.digitColor.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center
                )

                // Active Center Digit in the reading window (Bold, ultra-crisp black/contrast)
                Text(
                    text = digitStrings[currentDigit],
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 38.sp,
                    color = style.digitColor,
                    textAlign = TextAlign.Center
                )

                // Bottom next digit (curved backwards in 3D perspective)
                Text(
                    text = digitStrings[nextDigit],
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = style.digitColor.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 3D Metallic Barrel Curvature, Specular Glare & Aperture Shading Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Top dark bevel shadow (curves into the lock housing)
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.Black.copy(alpha = 0.75f),
                    0.25f to Color.Black.copy(alpha = 0.35f),
                    0.40f to Color.Transparent
                ),
                size = Size(w, h * 0.40f)
            )

            // 2. Bottom dark bevel shadow (curves into the lock housing)
            drawRect(
                brush = Brush.verticalGradient(
                    0.60f to Color.Transparent,
                    0.75f to Color.Black.copy(alpha = 0.35f),
                    1.0f to Color.Black.copy(alpha = 0.75f)
                ),
                topLeft = Offset(0f, h * 0.60f),
                size = Size(w, h * 0.40f)
            )

            // 3. Center lens / reading line glass highlight strip
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.White.copy(alpha = 0.0f),
                    0.48f to Color.White.copy(alpha = 0.22f),
                    0.52f to Color.White.copy(alpha = 0.22f),
                    1.0f to Color.White.copy(alpha = 0.0f)
                ),
                topLeft = Offset(0f, h * 0.36f),
                size = Size(w, h * 0.28f)
            )

            // 4. Barrel side shadows
            drawRect(
                brush = Brush.horizontalGradient(
                    0.0f to Color.Black.copy(alpha = 0.3f),
                    0.12f to Color.Transparent,
                    0.88f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.3f)
                ),
                size = size
            )

            // 5. Outer aperture border
            drawRoundRect(
                color = style.darkAccent.copy(alpha = 0.5f),
                size = size,
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

/**
 * Metal divider between tumbler wheels.
 */
@Composable
fun TumblerWheelDivider(
    style: TumblerStyle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(5.dp)
            .height(130.dp)
            .background(
                Brush.verticalGradient(
                    0.0f to style.darkAccent,
                    0.3f to style.primaryColor,
                    0.5f to style.lightAccent,
                    0.7f to style.primaryColor,
                    1.0f to style.darkAccent
                )
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(2.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
            )
        }
    }
}

/**
 * Visual index pointer arrow.
 */
@Composable
fun LockIndexPointer(
    style: TumblerStyle,
    pointingRight: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(14.dp)
            .height(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val path = androidx.compose.ui.graphics.Path()

            if (pointingRight) {
                path.moveTo(0f, 0f)
                path.lineTo(w, h / 2f)
                path.lineTo(0f, h)
                path.close()
            } else {
                path.moveTo(w, 0f)
                path.lineTo(0f, h / 2f)
                path.lineTo(w, h)
                path.close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    listOf(style.lightAccent, style.primaryColor, style.darkAccent)
                )
            )

            drawPath(
                path = path,
                color = style.darkAccent,
                style = Stroke(width = 1.2f)
            )
        }
    }
}

/**
 * Vintage rivets on lock casing.
 */
@Composable
fun LockRivets(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        RivetDot(color = color, modifier = Modifier.align(Alignment.TopStart))
        RivetDot(color = color, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun RivetDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(x = 0.dp, y = (-2).dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
            .border(0.5.dp, Color.Black.copy(alpha = 0.6f), CircleShape)
    )
}
