package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.PraiseRecord
import com.example.model.TumblerStyle
import com.example.model.ZahraaStep
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sebha_tumbler_prefs", Context.MODE_PRIVATE)

    fun getSettings(): AppSettings {
        val sound = prefs.getBoolean("sound_enabled", true)
        val vibration = prefs.getBoolean("vibration_enabled", true)
        val strength = prefs.getString("vibration_strength", "MEDIUM") ?: "MEDIUM"
        val styleName = prefs.getString("tumbler_style", TumblerStyle.GOLD.name) ?: TumblerStyle.GOLD.name
        val style = try { TumblerStyle.valueOf(styleName) } catch (_: Exception) { TumblerStyle.GOLD }
        val arabicNumerals = prefs.getBoolean("arabic_numerals", true)
        val fullScreenTap = prefs.getBoolean("fullscreen_tap", false)
        val keepScreenOn = prefs.getBoolean("keep_screen_on", true)

        return AppSettings(
            soundEnabled = sound,
            vibrationEnabled = vibration,
            vibrationStrength = strength,
            tumblerStyle = style,
            useArabicNumerals = arabicNumerals,
            fullScreenTapEnabled = fullScreenTap,
            keepScreenOn = keepScreenOn
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putBoolean("sound_enabled", settings.soundEnabled)
            .putBoolean("vibration_enabled", settings.vibrationEnabled)
            .putString("vibration_strength", settings.vibrationStrength)
            .putString("tumbler_style", settings.tumblerStyle.name)
            .putBoolean("arabic_numerals", settings.useArabicNumerals)
            .putBoolean("fullscreen_tap", settings.fullScreenTapEnabled)
            .putBoolean("keep_screen_on", settings.keepScreenOn)
            .apply()
    }

    // --- Zahraa Progress ---
    fun getZahraaState(): Triple<ZahraaStep, Int, Int> {
        val stepName = prefs.getString("zahraa_step", ZahraaStep.ALLAHU_AKBAR.name) ?: ZahraaStep.ALLAHU_AKBAR.name
        val step = try { ZahraaStep.valueOf(stepName) } catch (_: Exception) { ZahraaStep.ALLAHU_AKBAR }
        val stepCount = prefs.getInt("zahraa_step_count", 0)
        val totalCount = prefs.getInt("zahraa_total_count", 0)
        return Triple(step, stepCount, totalCount)
    }

    fun saveZahraaState(step: ZahraaStep, stepCount: Int, totalCount: Int) {
        prefs.edit()
            .putString("zahraa_step", step.name)
            .putInt("zahraa_step_count", stepCount)
            .putInt("zahraa_total_count", totalCount)
            .apply()
    }

    // --- Open Ascending Progress ---
    fun getOpenAscendingState(): Pair<Int, String> {
        val count = prefs.getInt("open_asc_count", 0)
        val dhikr = prefs.getString("open_asc_dhikr", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ") ?: "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ"
        return Pair(count, dhikr)
    }

    fun saveOpenAscendingState(count: Int, dhikr: String) {
        prefs.edit()
            .putInt("open_asc_count", count)
            .putString("open_asc_dhikr", dhikr)
            .apply()
    }

    // --- Open Descending Progress ---
    fun getOpenDescendingState(): Triple<Int, Int, String> {
        val remaining = prefs.getInt("open_desc_remaining", 100)
        val target = prefs.getInt("open_desc_target", 100)
        val dhikr = prefs.getString("open_desc_dhikr", "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ") ?: "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ"
        return Triple(remaining, target, dhikr)
    }

    fun saveOpenDescendingState(remaining: Int, target: Int, dhikr: String) {
        prefs.edit()
            .putInt("open_desc_remaining", remaining)
            .putInt("open_desc_target", target)
            .putString("open_desc_dhikr", dhikr)
            .apply()
    }

    // --- History Records ---
    fun getHistory(): List<PraiseRecord> {
        val jsonStr = prefs.getString("praise_history_json", null) ?: return emptyList()
        val list = mutableListOf<PraiseRecord>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    PraiseRecord(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        dhikrName = obj.optString("dhikrName", ""),
                        count = obj.optInt("count", 0),
                        target = if (obj.has("target")) obj.optInt("target") else null,
                        doorType = obj.optString("doorType", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.timestamp }
    }

    fun addHistoryRecord(record: PraiseRecord) {
        val current = getHistory().toMutableList()
        current.add(0, record)
        if (current.size > 200) {
            current.removeAt(current.size - 1)
        }
        val jsonArray = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("dhikrName", item.dhikrName)
            obj.put("count", item.count)
            item.target?.let { obj.put("target", it) }
            obj.put("doorType", item.doorType)
            obj.put("timestamp", item.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit()
            .putString("praise_history_json", jsonArray.toString())
            .putInt("all_time_total", getAllTimeTotal() + record.count)
            .apply()
    }

    fun clearHistory() {
        prefs.edit().remove("praise_history_json").apply()
    }

    fun getAllTimeTotal(): Int {
        return prefs.getInt("all_time_total", 0)
    }

    fun incrementAllTimeTotal(amount: Int = 1) {
        val current = getAllTimeTotal()
        prefs.edit().putInt("all_time_total", current + amount).apply()
    }
}
