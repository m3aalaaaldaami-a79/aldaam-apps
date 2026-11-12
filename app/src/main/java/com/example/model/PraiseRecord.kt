package com.example.model

data class PraiseRecord(
    val id: String = System.currentTimeMillis().toString(),
    val dhikrName: String,
    val count: Int,
    val target: Int? = null,
    val doorType: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val vibrationStrength: String = "MEDIUM", // LIGHT, MEDIUM, STRONG
    val tumblerStyle: TumblerStyle = TumblerStyle.GOLD,
    val useArabicNumerals: Boolean = true,
    val fullScreenTapEnabled: Boolean = false,
    val keepScreenOn: Boolean = true
)
