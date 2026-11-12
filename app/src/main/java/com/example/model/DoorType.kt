package com.example.model

enum class DoorType(val titleAr: String, val subtitleAr: String) {
    ZAHRAA("تسبيح الزهراء", "٣٤ تكبير • ٣٣ تحميد • ٣٣ تسبيح"),
    OPEN_ASCENDING("التسبيح المفتوح (تصاعدي)", "عدّ حر تصاعدي غير مقيّد"),
    OPEN_DESCENDING("التسبيح المفتوح (تنازلي)", "تحديد الهدف واسم الذكر والعد تنازلياً"),
    HISTORY("سجل الأذكار", "إحصائيات والتسبيحات المنجزة"),
    SETTINGS("إعدادات القفل", "تخصيص المظهر والصوت والاهتزاز")
}
