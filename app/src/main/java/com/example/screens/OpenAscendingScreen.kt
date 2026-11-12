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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
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
import com.example.sound.HapticFeedbackManager
import com.example.sound.SoundFeedbackManager
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnDarkSecondary

private val COMMON_ADHKAR = listOf(
    "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
    "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
    "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ",
    "لَا إِلَهَ إِلَّا اللَّهُ",
    "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
    "سُبْحَانَ اللَّهِ العَظِيمِ",
    "حَسْبُنَا اللَّهُ وَنِعْمَ الوَكِيلُ"
)

@Composable
fun OpenAscendingScreen(
    prefsManager: PreferencesManager,
    settings: AppSettings,
    soundManager: SoundFeedbackManager,
    hapticManager: HapticFeedbackManager,
    modifier: Modifier = Modifier
) {
    val initial = remember { prefsManager.getOpenAscendingState() }
    var count by remember { mutableIntStateOf(initial.first) }
    var dhikrName by remember { mutableStateOf(initial.second) }
    var showEditDhikrDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var tempDhikrInput by remember { mutableStateOf(dhikrName) }

    fun saveState() {
        prefsManager.saveOpenAscendingState(count, dhikrName)
    }

    fun handleIncrement() {
        val newCount = count + 1
        count = newCount
        saveState()

        soundManager.playTumblerClick(settings.soundEnabled)

        // Check if hit milestone (every 33 or 100)
        if (newCount % 100 == 0) {
            hapticManager.performMilestone(settings.vibrationEnabled, settings.vibrationStrength)
            soundManager.playCompletionChime(settings.soundEnabled)
        } else if (newCount % 33 == 0) {
            hapticManager.performStepComplete(settings.vibrationEnabled, settings.vibrationStrength)
        } else {
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
        }
    }

    fun handleDecrement() {
        if (count > 0) {
            count -= 1
            saveState()
            soundManager.playTumblerClick(settings.soundEnabled)
            hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
        }
    }

    fun handleResetAndSave() {
        if (count > 0) {
            // Save current count into history
            prefsManager.addHistoryRecord(
                PraiseRecord(
                    dhikrName = dhikrName,
                    count = count,
                    doorType = "التسبيح المفتوح (تصاعدي)"
                )
            )
        }
        count = 0
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
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التسبيح المفتوح (التصاعدي)",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "عدّ حر لا نهائي بدون حد أقصى",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                }

                IconButton(
                    onClick = {
                        tempDhikrInput = dhikrName
                        showEditDhikrDialog = true
                    },
                    modifier = Modifier.testTag("edit_dhikr_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تغيير الذكر",
                        tint = GoldLight
                    )
                }
            }

            // Quick Dhikr Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                COMMON_ADHKAR.forEach { dhikr ->
                    val isSelected = dhikr == dhikrName
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            dhikrName = dhikr
                            saveState()
                        },
                        label = {
                            Text(
                                text = dhikr,
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = EmeraldSurfaceVariant,
                            labelColor = TextOnDarkPrimary
                        )
                    )
                }
            }

            // Current Dhikr Card
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
                        text = "الذِّكْرُ الحَالِيّ",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium
                    )

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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دورات الـ ٣٣: ${count / 33}",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                        Text(
                            text = "دورات الـ ١٠٠: ${count / 100}",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = TextOnDarkSecondary
                        )
                    }
                }
            }

            // Cylindrical Luggage Lock Tumbler Counter (4 digits)
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
                        text = "قفل العداد الأسطواني (اضغط للعد):",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(
                        text = "العدد الحالي: $count",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = GoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LuggageLockTumbler(
                    count = count,
                    minDigits = 4,
                    style = settings.tumblerStyle,
                    useArabicNumerals = settings.useArabicNumerals,
                    onClick = { handleIncrement() },
                    onWheelRoll = { _, delta ->
                        if (delta > 0) handleIncrement() else handleDecrement()
                    },
                    modifier = Modifier.fillMaxWidth(0.96f)
                )
            }

            // Big Prayer Button
            PrayerClickButton(
                onClick = { handleIncrement() },
                style = settings.tumblerStyle,
                label = "سَبِّحْ",
                sublabel = "العدد: $count",
                modifier = Modifier.padding(top = 8.dp)
            )

            // Actions: Undo, Reset / Save
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { handleDecrement() },
                    enabled = count > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnDarkPrimary),
                    modifier = Modifier.testTag("open_asc_undo_button")
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
                    enabled = count > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                    modifier = Modifier.testTag("open_asc_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تصفير",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصفير وحفظ", fontFamily = CairoFontFamily, fontSize = 13.sp)
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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Edit Dhikr Dialog
    if (showEditDhikrDialog) {
        AlertDialog(
            onDismissRequest = { showEditDhikrDialog = false },
            title = {
                Text(
                    text = "تحديد أو كتابة الذكر",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "اكتب الذكر أو الدعاء الذي ترغب في التسبيح به:",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempDhikrInput,
                        onValueChange = { tempDhikrInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("مثال: لا حول ولا قوة إلا بالله") },
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempDhikrInput.isNotBlank()) {
                            dhikrName = tempDhikrInput.trim()
                            saveState()
                        }
                        showEditDhikrDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("حفظ", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDhikrDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Reset Confirm Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text(
                    text = "تصفير وحفظ التسبيح؟",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "سيتم حفظ هذا الورد البالغ ($count تسبيحة) في سجل أذكارك وتصفير العداد للبدء من جديد.",
                    fontFamily = CairoFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        handleResetAndSave()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("نعم، صفّر واحفظ", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}
