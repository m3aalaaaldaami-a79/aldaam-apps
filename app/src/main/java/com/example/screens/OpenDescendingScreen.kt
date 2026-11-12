package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LuggageLockTumbler
import com.example.components.PrayerClickButton
import com.example.data.PreferencesManager
import com.example.model.AppSettings
import com.example.model.PraiseRecord
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

private val COMMON_TARGETS = listOf(33, 40, 70, 100, 313, 500, 1000)

private val POPULAR_ADHKAR = listOf(
    "أَسْتَغْفِرُ اللَّهَ رَبِّي وَأَتُوبُ إِلَيْهِ",
    "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ",
    "يَا حَيُّ يَا قَيُّومُ",
    "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
    "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
    "يَا عَلِيُّ يَا عَظِيمُ",
    "يَا فَتَّاحُ يَا رَزَّاقُ"
)

@Composable
fun OpenDescendingScreen(
    prefsManager: PreferencesManager,
    settings: AppSettings,
    soundManager: SoundFeedbackManager,
    hapticManager: HapticFeedbackManager,
    modifier: Modifier = Modifier
) {
    val initial = remember { prefsManager.getOpenDescendingState() }
    var remainingCount by remember { mutableIntStateOf(initial.first) }
    var targetCount by remember { mutableIntStateOf(initial.second) }
    var dhikrName by remember { mutableStateOf(initial.third) }

    // Is active countdown session running or in setup mode
    var isConfiguringTarget by remember { mutableStateOf(false) }

    // Dialog inputs
    var inputDhikr by remember { mutableStateOf(dhikrName) }
    var inputTargetString by remember { mutableStateOf(targetCount.toString()) }

    var showCompletionDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    fun saveState() {
        prefsManager.saveOpenDescendingState(remainingCount, targetCount, dhikrName)
    }

    fun handleDecrement() {
        if (showCompletionDialog) return

        if (remainingCount <= 0) {
            showCompletionDialog = true
            return
        }

        val newRemaining = remainingCount - 1
        remainingCount = newRemaining
        saveState()

        soundManager.playTumblerClick(settings.soundEnabled)

        if (newRemaining <= 0) {
            // Reached target! Completed countdown!
            soundManager.playCompletionChime(settings.soundEnabled)
            hapticManager.performMilestone(settings.vibrationEnabled, settings.vibrationStrength)
            showCompletionDialog = true

            // Log to history
            prefsManager.addHistoryRecord(
                PraiseRecord(
                    dhikrName = dhikrName,
                    count = targetCount,
                    target = targetCount,
                    doorType = "التسبيح التنازلي"
                )
            )
        } else {
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
        }
    }

    fun handleUndo() {
        if (remainingCount < targetCount) {
            remainingCount += 1
            saveState()
            soundManager.playTumblerClick(settings.soundEnabled)
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
        }
    }

    fun handleRestartCountdown() {
        remainingCount = targetCount
        showCompletionDialog = false
        saveState()
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (settings.fullScreenTapEnabled && !isConfiguringTarget) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { handleDecrement() }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التسبيح المفتوح (التنازلي)",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldLight
                    )
                    Text(
                        text = if (isConfiguringTarget) "حدد اسم الذكر والعدد الهدف" else "العد تنازلياً حتى الصفر",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                }

                OutlinedButton(
                    onClick = {
                        inputDhikr = dhikrName
                        inputTargetString = targetCount.toString()
                        isConfiguringTarget = !isConfiguringTarget
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                    modifier = Modifier.testTag("configure_target_button")
                ) {
                    Icon(
                        imageVector = if (isConfiguringTarget) Icons.Default.PlayArrow else Icons.Default.Edit,
                        contentDescription = "تعديل الهدف",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isConfiguringTarget) "بدء" else "تعديل",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp
                    )
                }
            }

            if (isConfiguringTarget) {
                // Setup / Configuration Mode
                TargetSetupCard(
                    inputDhikr = inputDhikr,
                    onDhikrChange = { inputDhikr = it },
                    inputTargetString = inputTargetString,
                    onTargetStringChange = { inputTargetString = it },
                    onSelectPresetTarget = { preset -> inputTargetString = preset.toString() },
                    onSelectPresetDhikr = { presetDhikr -> inputDhikr = presetDhikr },
                    onStart = {
                        val parsed = inputTargetString.toIntOrNull()?.coerceIn(1, 99999) ?: 100
                        dhikrName = if (inputDhikr.isNotBlank()) inputDhikr.trim() else "أَسْتَغْفِرُ اللَّهَ"
                        targetCount = parsed
                        remainingCount = parsed
                        isConfiguringTarget = false
                        saveState()
                    }
                )
            } else {
                // Active Countdown Mode

                // Current Dhikr & Progress Card
                val completed = (targetCount - remainingCount).coerceAtLeast(0)
                val progressFraction = if (targetCount > 0) completed.toFloat() / targetCount.toFloat() else 1f
                val percentInt = (progressFraction * 100).toInt()

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الهدف: $targetCount تسبيحة",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                color = GoldLight
                            )
                            Text(
                                text = "الإنجاز: $percentInt%",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                color = TextOnDarkSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = dhikrName,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = TextOnDarkPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldPrimary,
                            trackColor = EmeraldDark,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "المتبقي: $remainingCount من أصل $targetCount (أنجزت $completed)",
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            color = TextOnDarkSecondary
                        )
                    }
                }

                // The Cylindrical Luggage Lock Tumbler Counter (Counting Down)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.96f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "قفل العداد التنازلي (اضغط للعد):",
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Text(
                            text = "المتبقي: $remainingCount",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LuggageLockTumbler(
                        count = remainingCount,
                        minDigits = if (targetCount > 999) 4 else 3,
                        style = settings.tumblerStyle,
                        useArabicNumerals = settings.useArabicNumerals,
                        isCountingDown = true,
                        onClick = { handleDecrement() },
                        onWheelRoll = { _, delta ->
                            if (delta > 0) handleDecrement() else handleUndo()
                        },
                        modifier = Modifier.fillMaxWidth(0.96f)
                    )
                }

                // Tactile Prayer Button
                PrayerClickButton(
                    onClick = { handleDecrement() },
                    style = settings.tumblerStyle,
                    label = "سَبِّحْ",
                    sublabel = if (remainingCount > 0) "بقي $remainingCount" else "اكتمل الهدف!",
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Actions: Undo (+1) and Reset
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { handleUndo() },
                        enabled = remainingCount < targetCount,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnDarkPrimary),
                        modifier = Modifier.testTag("open_desc_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "تراجع",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تراجع (+١)", fontFamily = CairoFontFamily, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        modifier = Modifier.testTag("open_desc_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة البدء",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إعادة البدء", fontFamily = CairoFontFamily, fontSize = 13.sp)
                    }
                }

                if (settings.fullScreenTapEnabled) {
                    Text(
                        text = "• وضع اللمس الكامل مفعّل: اضغط بأي مكان على الشاشة للتسبيح",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldLight.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Reset Confirm Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text(
                    text = "إعادة بدء التسبيح التنازلي؟",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل تريد إعادة تعيين العداد التنازلي إلى $targetCount مرة أخرى؟",
                    fontFamily = CairoFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        handleRestartCountdown()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("نعم، أعد البدء", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Completion Celebration Dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "اكتمل الهدف",
                    tint = GoldPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "مبارك! أتممت الورد المستهدف",
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
                        text = "وصلت بنجاح إلى نهاية العد التنازلي وأكملت:\n«$dhikrName» ($targetCount مرة)",
                        fontFamily = CairoFontFamily,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "تم توثيق إنجازك وحفظه في سجل الأذكار.",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = GoldLight
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompletionDialog = false
                        handleRestartCountdown()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("إعادة نفس الورد", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCompletionDialog = false
                    isConfiguringTarget = true
                }) {
                    Text("تحديد ورد جديد", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

/**
 * Setup card where user enters Dhikr Name and Target Count then presses Start.
 */
@Composable
fun TargetSetupCard(
    inputDhikr: String,
    onDhikrChange: (String) -> Unit,
    inputTargetString: String,
    onTargetStringChange: (String) -> Unit,
    onSelectPresetTarget: (Int) -> Unit,
    onSelectPresetDhikr: (String) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, EmeraldBorder, RoundedCornerShape(20.dp)),
        color = EmeraldSurface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "١. أدخل اسم التسبيح أو اختر من الأذكار:",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldLight
            )

            OutlinedTextField(
                value = inputDhikr,
                onValueChange = onDhikrChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("desc_dhikr_input"),
                placeholder = { Text("مثال: أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = EmeraldBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Preset Adhkar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                POPULAR_ADHKAR.forEach { dhikr ->
                    FilterChip(
                        selected = dhikr == inputDhikr,
                        onClick = { onSelectPresetDhikr(dhikr) },
                        label = { Text(dhikr, fontFamily = CairoFontFamily, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = EmeraldSurfaceVariant,
                            labelColor = TextOnDarkPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "٢. أدخل عدد التسبيحات الهدف (Target):",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldLight
            )

            OutlinedTextField(
                value = inputTargetString,
                onValueChange = { str ->
                    if (str.all { it.isDigit() } && str.length <= 6) {
                        onTargetStringChange(str)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("desc_target_input"),
                placeholder = { Text("مثال: 100") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = EmeraldBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Quick Target Chips (33, 40, 70, 100, 313, 500, 1000)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                COMMON_TARGETS.forEach { preset ->
                    val isSelected = inputTargetString == preset.toString()
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPresetTarget(preset) },
                        label = { Text("$preset", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = EmeraldSurfaceVariant,
                            labelColor = TextOnDarkPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start Countdown Button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_desc_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "بدء التسبيح",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "بدء التسبيح التنازلي الآن",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
