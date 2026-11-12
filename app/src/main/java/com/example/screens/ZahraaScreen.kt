package com.example.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LuggageLockTumbler
import com.example.components.PrayerClickButton
import com.example.data.PreferencesManager
import com.example.model.AppSettings
import com.example.model.PraiseRecord
import com.example.model.ZahraaStep
import com.example.sound.HapticFeedbackManager
import com.example.sound.SoundFeedbackManager
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnDarkSecondary

@Composable
fun ZahraaScreen(
    prefsManager: PreferencesManager,
    settings: AppSettings,
    soundManager: SoundFeedbackManager,
    hapticManager: HapticFeedbackManager,
    modifier: Modifier = Modifier
) {
    val initial = remember { prefsManager.getZahraaState() }
    var currentStep by remember { mutableStateOf(initial.first) }
    var stepCount by remember { mutableIntStateOf(initial.second) }
    var totalCount by remember { mutableIntStateOf(initial.third) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showFadlDialog by remember { mutableStateOf(false) }

    fun saveState() {
        prefsManager.saveZahraaState(currentStep, stepCount, totalCount)
    }

    fun handleIncrement() {
        if (showCompletionDialog) return

        if (totalCount >= 100) {
            showCompletionDialog = true
            return
        }

        if (stepCount >= currentStep.targetCount) {
            val nextStep = currentStep.next()
            if (nextStep != null) {
                currentStep = nextStep
                stepCount = 1
                totalCount = (totalCount + 1).coerceAtMost(100)
                soundManager.playTumblerClick(settings.soundEnabled)
                hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
                saveState()
                return
            } else {
                showCompletionDialog = true
                return
            }
        }

        val newStepCount = stepCount + 1
        val newTotalCount = (totalCount + 1).coerceAtMost(100)
        stepCount = newStepCount
        totalCount = newTotalCount
        saveState()

        if (newStepCount == currentStep.targetCount) {
            val nextStep = currentStep.next()
            if (nextStep != null) {
                soundManager.playCompletionChime(settings.soundEnabled)
                hapticManager.performStepComplete(settings.vibrationEnabled, settings.vibrationStrength)
            } else {
                soundManager.playCompletionChime(settings.soundEnabled)
                hapticManager.performMilestone(settings.vibrationEnabled, settings.vibrationStrength)
                showCompletionDialog = true
                prefsManager.addHistoryRecord(
                    PraiseRecord(
                        dhikrName = "تسبيح فاطمة الزهراء (ع)",
                        count = 100,
                        target = 100,
                        doorType = "تسبيح الزهراء"
                    )
                )
            }
        } else {
            soundManager.playTumblerClick(settings.soundEnabled)
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
        }
    }

    fun handleDecrement() {
        if (stepCount > 0) {
            stepCount -= 1
            if (totalCount > 0) totalCount -= 1
            soundManager.playTumblerClick(settings.soundEnabled)
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
            saveState()
        } else {
            // Roll back to previous step if possible
            val prev = currentStep.previous()
            if (prev != null) {
                currentStep = prev
                stepCount = prev.targetCount - 1
                if (totalCount > 0) totalCount -= 1
                soundManager.playTumblerClick(settings.soundEnabled)
                hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
                saveState()
            }
        }
    }

    fun handleReset() {
        currentStep = ZahraaStep.ALLAHU_AKBAR
        stepCount = 0
        totalCount = 0
        showCompletionDialog = false
        saveState()
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (settings.fullScreenTapEnabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { handleIncrement() }
                    )
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar with Section Title and Info icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "تسبيح الزهراء (عليها السلام)",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "المجموع: $totalCount / ١٠٠ تسبيحة",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                }

                IconButton(
                    onClick = { showFadlDialog = true },
                    modifier = Modifier.testTag("zahraa_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "فضل تسبيح الزهراء",
                        tint = GoldLight
                    )
                }
            }

            // Steps Progress Indicator Bar (3 tabs: 34, 33, 33)
            ZahraaStepIndicator(
                currentStep = currentStep,
                stepCount = stepCount,
                onSelectStep = { targetStep ->
                    currentStep = targetStep
                    stepCount = 0
                    saveState()
                }
            )

            // Current Dhikr Sacred Calligraphy Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, EmeraldBorder, RoundedCornerShape(20.dp)),
                color = EmeraldSurface,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "المرحلة ${currentStep.stepIndex} من ٣: ${currentStep.explanation}",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentStep.titleAr,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = TextOnDarkPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar for the current step
                    val stepFraction = stepCount.toFloat() / currentStep.targetCount.toFloat()
                    LinearProgressIndicator(
                        progress = { stepFraction },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldPrimary,
                        trackColor = EmeraldDark,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$stepCount من أصل ${currentStep.targetCount}",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                }
            }

            // The Luggage Lock Cylindrical Tumbler Counter
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قفل العداد الأسطواني (اضغط للعد):",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(
                        text = "$stepCount / ${currentStep.targetCount}",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = GoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LuggageLockTumbler(
                    count = stepCount,
                    minDigits = 3,
                    style = settings.tumblerStyle,
                    useArabicNumerals = settings.useArabicNumerals,
                    onClick = { handleIncrement() },
                    onWheelRoll = { _, delta ->
                        if (delta > 0) handleIncrement() else handleDecrement()
                    },
                    modifier = Modifier.fillMaxWidth(0.92f)
                )
            }

            // Big Tactile Click Button
            PrayerClickButton(
                onClick = { handleIncrement() },
                style = settings.tumblerStyle,
                label = "سَبِّحْ",
                sublabel = "بقي ${currentStep.targetCount - stepCount}",
                modifier = Modifier.padding(top = 8.dp)
            )

            // Bottom Actions: Undo (-1) and Reset
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { handleDecrement() },
                    enabled = (stepCount > 0 || currentStep != ZahraaStep.ALLAHU_AKBAR),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnDarkPrimary),
                    modifier = Modifier.testTag("zahraa_undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "تراجع",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تراجع (-١)", fontFamily = CairoFontFamily, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                    modifier = Modifier.testTag("zahraa_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة ضبط",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("بدء من جديد", fontFamily = CairoFontFamily, fontSize = 13.sp)
                }
            }

            if (settings.fullScreenTapEnabled) {
                Text(
                    text = "• وضع اللمس الكامل مفعّل: يمكنك الضغط بأي مكان على الشاشة للتسبيح",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = GoldLight.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Confirmation dialog for Reset
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text(
                    text = "إعادة تسبيح الزهراء؟",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل ترغب في إعادة العداد إلى الصفر والبدء من التكبير مجدداً؟",
                    fontFamily = CairoFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        handleReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("نعم، ابدأ من جديد", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Sacred Completion Dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "تم",
                    tint = GoldPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "مبارك! أتممت تسبيح الزهراء (ع)",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = GoldLight
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "تقبل الله طاعتكم وأثابكم خيراً بمحمد وآل محمد.",
                        fontFamily = CairoFontFamily,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "الدعاء المأثور عقب التسبيح:\n«لا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ»",
                            fontFamily = AmiriFontFamily,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = GoldLight
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompletionDialog = false
                        handleReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("تسبيح جديد", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionDialog = false }) {
                    Text("إغلاق", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Fadl Dialog
    if (showFadlDialog) {
        AlertDialog(
            onDismissRequest = { showFadlDialog = false },
            title = {
                Text(
                    text = "فضل تسبيح سيدة نساء العالمين (ع)",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "عن الإمام الصادق (عليه السلام):\n«تَسْبِيحُ فَاطِمَةَ فِي كُلِّ يَوْمٍ فِي دُبُرِ كُلِّ صَلَاةٍ أَحَبُّ إِلَيَّ مِنْ صَلَاةِ أَلْفِ رَكْعَةٍ فِي كُلِّ يَوْمٍ».",
                        fontFamily = AmiriFontFamily,
                        fontSize = 15.sp,
                        color = TextOnDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الترتيب الشرعي:\n١. الله أكبر (٣٤ مرة)\n٢. الحمد لله (٣٣ مرة)\n٣. سبحان الله (٣٣ مرة)\nالمجموع: ١٠٠ تسبيحة تثقل الميزان وتطرد الشيطان وترضي الرحمن.",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFadlDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("تم", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

/**
 * 3-Tab Stepper for Tasbih Fatima az-Zahra.
 */
@Composable
fun ZahraaStepIndicator(
    currentStep: ZahraaStep,
    stepCount: Int,
    onSelectStep: (ZahraaStep) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EmeraldSurfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ZahraaStep.values().forEach { step ->
            val isCurrent = step == currentStep
            val isCompleted = step.stepIndex < currentStep.stepIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            isCurrent -> Brush.linearGradient(listOf(GoldPrimary, GoldDark))
                            isCompleted -> Brush.linearGradient(listOf(EmeraldDark, EmeraldSurface))
                            else -> Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .clickable { onSelectStep(step) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = step.titleAr,
                        fontFamily = CairoFontFamily,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isCurrent) Color.Black else TextOnDarkPrimary
                    )
                    Text(
                        text = "${step.targetCount}x",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = if (isCurrent) Color(0xFF2A1C04) else GoldLight.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
