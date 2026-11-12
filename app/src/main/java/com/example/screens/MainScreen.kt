package com.example.screens

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.model.DoorType
import com.example.sound.HapticFeedbackManager
import com.example.sound.SoundFeedbackManager
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDeepBackground
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnDarkSecondary

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val soundManager = remember { SoundFeedbackManager() }
    val hapticManager = remember { HapticFeedbackManager(context) }

    var currentDoor by remember { mutableStateOf(DoorType.ZAHRAA) }
    var settings by remember { mutableStateOf(prefsManager.getSettings()) }

    // Handle Keep Screen On
    val activity = context as? Activity
    DisposableEffect(settings.keepScreenOn) {
        if (settings.keepScreenOn) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // BackHandler to return to first door if in sub-screen
    BackHandler(enabled = currentDoor != DoorType.ZAHRAA) {
        currentDoor = DoorType.ZAHRAA
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeepBackground),
        containerColor = EmeraldDeepBackground,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .border(
                        width = 1.dp,
                        color = EmeraldBorder,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                containerColor = EmeraldSurface,
                tonalElevation = 8.dp
            ) {
                // Door 1: Zahraa
                NavigationBarItem(
                    selected = currentDoor == DoorType.ZAHRAA,
                    onClick = { currentDoor = DoorType.ZAHRAA },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "تسبيح الزهراء",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "الزهراء (ع)",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (currentDoor == DoorType.ZAHRAA) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = getNavColors(),
                    modifier = Modifier.testTag("nav_door_zahraa")
                )

                // Door 2: Open Ascending
                NavigationBarItem(
                    selected = currentDoor == DoorType.OPEN_ASCENDING,
                    onClick = { currentDoor = DoorType.OPEN_ASCENDING },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "مفتوح تصاعدي",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "تصاعدي",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (currentDoor == DoorType.OPEN_ASCENDING) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = getNavColors(),
                    modifier = Modifier.testTag("nav_door_ascending")
                )

                // Door 3: Open Descending
                NavigationBarItem(
                    selected = currentDoor == DoorType.OPEN_DESCENDING,
                    onClick = { currentDoor = DoorType.OPEN_DESCENDING },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = "مفتوح تنازلي",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "تنازلي (هدف)",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (currentDoor == DoorType.OPEN_DESCENDING) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = getNavColors(),
                    modifier = Modifier.testTag("nav_door_descending")
                )

                // History
                NavigationBarItem(
                    selected = currentDoor == DoorType.HISTORY,
                    onClick = { currentDoor = DoorType.HISTORY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "السجل",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "السجل",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (currentDoor == DoorType.HISTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = getNavColors(),
                    modifier = Modifier.testTag("nav_door_history")
                )

                // Settings
                NavigationBarItem(
                    selected = currentDoor == DoorType.SETTINGS,
                    onClick = { currentDoor = DoorType.SETTINGS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "القفل",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (currentDoor == DoorType.SETTINGS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = getNavColors(),
                    modifier = Modifier.testTag("nav_door_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            Crossfade(targetState = currentDoor, label = "DoorCrossfade") { door ->
                when (door) {
                    DoorType.ZAHRAA -> {
                        ZahraaScreen(
                            prefsManager = prefsManager,
                            settings = settings,
                            soundManager = soundManager,
                            hapticManager = hapticManager
                        )
                    }
                    DoorType.OPEN_ASCENDING -> {
                        OpenAscendingScreen(
                            prefsManager = prefsManager,
                            settings = settings,
                            soundManager = soundManager,
                            hapticManager = hapticManager
                        )
                    }
                    DoorType.OPEN_DESCENDING -> {
                        OpenDescendingScreen(
                            prefsManager = prefsManager,
                            settings = settings,
                            soundManager = soundManager,
                            hapticManager = hapticManager
                        )
                    }
                    DoorType.HISTORY -> {
                        HistoryScreen(prefsManager = prefsManager)
                    }
                    DoorType.SETTINGS -> {
                        SettingsScreen(
                            prefsManager = prefsManager,
                            settings = settings,
                            onSettingsChanged = { settings = it },
                            soundManager = soundManager,
                            hapticManager = hapticManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.Black,
    selectedTextColor = GoldLight,
    indicatorColor = GoldPrimary,
    unselectedIconColor = TextOnDarkSecondary,
    unselectedTextColor = TextOnDarkSecondary
)
