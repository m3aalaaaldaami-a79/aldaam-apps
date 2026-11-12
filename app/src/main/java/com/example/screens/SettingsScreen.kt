package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.LuggageLockTumbler
import com.example.data.PreferencesManager
import com.example.model.AppSettings
import com.example.model.TumblerStyle
import com.example.sound.HapticFeedbackManager
import com.example.sound.SoundFeedbackManager
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnDarkSecondary

@Composable
fun SettingsScreen(
    prefsManager: PreferencesManager,
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    soundManager: SoundFeedbackManager,
    hapticManager: HapticFeedbackManager,
    modifier: Modifier = Modifier
) {
    fun update(newSettings: AppSettings) {
        prefsManager.saveSettings(newSettings)
        onSettingsChanged(newSettings)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "إعدادات السبحة وقفل العداد",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = GoldLight
                )
                Text(
                    text = "تخصيص المظهر والصوت والاهتزاز وحساسية اللمس",
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp,
                    color = TextOnDarkSecondary
                )
            }
        }

        // Live Preview of the Luggage Lock Tumbler
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "معاينة شكل قفل الشنطة الأسطواني",
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp,
                    color = GoldLight
                )
                Spacer(modifier = Modifier.height(10.dp))

                LuggageLockTumbler(
                    count = 100,
                    minDigits = 3,
                    style = settings.tumblerStyle,
                    useArabicNumerals = settings.useArabicNumerals,
                    onClick = {
                        soundManager.playTumblerClick(settings.soundEnabled)
                        hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
                    },
                    onWheelRoll = { _, _ ->
                        soundManager.playTumblerClick(settings.soundEnabled)
                        hapticManager.performClick(settings.vibrationEnabled, settings.vibrationStrength)
                    },
                    modifier = Modifier.fillMaxWidth(0.88f)
                )
            }
        }

        // Section: Metallic Finish Theme
        SettingsSectionCard(
            title = "مظهر المعدن لقفل الشنطة",
            icon = Icons.Default.ColorLens
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TumblerStyle.values().forEach { style ->
                    val isSelected = style == settings.tumblerStyle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldSurfaceVariant else Color.Transparent)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GoldPrimary else EmeraldBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                update(settings.copy(tumblerStyle = style))
                                soundManager.playTumblerClick(settings.soundEnabled)
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(style.primaryColor)
                                    .border(1.dp, style.lightAccent, CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = style.titleAr,
                                fontFamily = CairoFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (isSelected) GoldLight else TextOnDarkPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section: Numbers Type (Arabic Indic vs Western)
        SettingsSectionCard(
            title = "نوع أرقام الأسطوانة",
            icon = Icons.Default.Numbers
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = settings.useArabicNumerals,
                    onClick = { update(settings.copy(useArabicNumerals = true)) },
                    label = { Text("أرقام مشرقية (٠ ١ ٢ ٣)", fontFamily = CairoFontFamily) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = EmeraldSurfaceVariant,
                        labelColor = TextOnDarkPrimary
                    )
                )

                FilterChip(
                    selected = !settings.useArabicNumerals,
                    onClick = { update(settings.copy(useArabicNumerals = false)) },
                    label = { Text("أرقام قياسية (0 1 2 3)", fontFamily = CairoFontFamily) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = EmeraldSurfaceVariant,
                        labelColor = TextOnDarkPrimary
                    )
                )
            }
        }

        // Section: Sound Feedback (Mechanical Click)
        SettingsToggleCard(
            title = "صوت تكتكة القفل الميكانيكي",
            subtitle = "إصدار صوت نقرة ميكانيكية حقيقية مع كل لفة للأسطوانة",
            icon = Icons.Default.VolumeUp,
            checked = settings.soundEnabled,
            onCheckedChange = { checked ->
                update(settings.copy(soundEnabled = checked))
                if (checked) soundManager.playTumblerClick(true)
            }
        )

        // Section: Haptic Feedback (Vibration)
        SettingsSectionCard(
            title = "الاهتزاز اللمسي (Haptics)",
            icon = Icons.Default.Vibration
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تفعيل الاهتزاز مع دوران الأسطوانة",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkPrimary
                    )
                    Switch(
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { checked ->
                            update(settings.copy(vibrationEnabled = checked))
                            if (checked) hapticManager.performClick(true, settings.vibrationStrength)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = GoldPrimary
                        )
                    )
                }

                if (settings.vibrationEnabled) {
                    Text(
                        text = "شدة الاهتزاز:",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = TextOnDarkSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("LIGHT" to "خفيف", "MEDIUM" to "متوسط", "STRONG" to "قوي").forEach { (code, label) ->
                            val isSelected = settings.vibrationStrength == code
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    update(settings.copy(vibrationStrength = code))
                                    hapticManager.performClick(true, code)
                                },
                                label = { Text(label, fontFamily = CairoFontFamily) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = EmeraldSurfaceVariant,
                                    labelColor = TextOnDarkPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Full-screen tap
        SettingsToggleCard(
            title = "التسبيح باللمس الكامل",
            subtitle = "إمكانية التسبيح بالنقر على أي مكان بالشاشة دون النظر للهاتف",
            icon = Icons.Default.TouchApp,
            checked = settings.fullScreenTapEnabled,
            onCheckedChange = { checked ->
                update(settings.copy(fullScreenTapEnabled = checked))
            }
        )

        // Section: Keep Screen On
        SettingsToggleCard(
            title = "إبقاء الشاشة مضاءة",
            subtitle = "منع قفل الهاتف تلقائياً أثناء جلسة التسبيح",
            icon = Icons.Default.Visibility,
            checked = settings.keepScreenOn,
            onCheckedChange = { checked ->
                update(settings.copy(keepScreenOn = checked))
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, EmeraldBorder, RoundedCornerShape(18.dp)),
        color = EmeraldSurface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GoldLight
                )
            }

            content()
        }
    }
}

@Composable
fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, EmeraldBorder, RoundedCornerShape(18.dp)),
        color = EmeraldSurface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextOnDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = TextOnDarkSecondary
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = GoldPrimary
                )
            )
        }
    }
}
