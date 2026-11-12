package com.example.model

enum class ZahraaStep(
    val stepIndex: Int,
    val titleAr: String,
    val targetCount: Int,
    val explanation: String
) {
    ALLAHU_AKBAR(
        stepIndex = 1,
        titleAr = "اللهُ أَكْبَرُ",
        targetCount = 34,
        explanation = "أربع وثلاثون تكبيرة"
    ),
    ALHAMDULILLAH(
        stepIndex = 2,
        titleAr = "الحَمْدُ لِلَّهِ",
        targetCount = 33,
        explanation = "ثلاث وثلاثون تحميدة"
    ),
    SUBHANALLAH(
        stepIndex = 3,
        titleAr = "سُبْحَانَ اللَّهِ",
        targetCount = 33,
        explanation = "ثلاث وثلاثون تسبيحة"
    );

    fun next(): ZahraaStep? {
        return when (this) {
            ALLAHU_AKBAR -> ALHAMDULILLAH
            ALHAMDULILLAH -> SUBHANALLAH
            SUBHANALLAH -> null
        }
    }

    fun previous(): ZahraaStep? {
        return when (this) {
            ALLAHU_AKBAR -> null
            ALHAMDULILLAH -> ALLAHU_AKBAR
            SUBHANALLAH -> ALHAMDULILLAH
        }
    }
}
