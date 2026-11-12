package com.example.sound

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticFeedbackManager(context: Context) {
    @Suppress("DEPRECATION")
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Tactile click for each tumbler step/increment.
     */
    fun performClick(enabled: Boolean, strength: String = "MEDIUM") {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return

        val duration = when (strength) {
            "LIGHT" -> 12L
            "STRONG" -> 25L
            else -> 18L
        }
        val amplitude = when (strength) {
            "LIGHT" -> 70
            "STRONG" -> 220
            else -> 140
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } catch (_: Exception) {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    /**
     * Distinct double vibration when completing a step (e.g. 34 Allahu Akbar -> Alhamdulillah).
     */
    fun performStepComplete(enabled: Boolean, strength: String = "MEDIUM") {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return

        val amp = when (strength) {
            "LIGHT" -> 100
            "STRONG" -> 240
            else -> 180
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val timings = longArrayOf(0, 45, 60, 50)
                val amplitudes = intArrayOf(0, amp, 0, (amp * 1.1).toInt().coerceAtMost(255))
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } catch (_: Exception) {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 45, 60, 50), -1)
        }
    }

    /**
     * Celebratory celebratory rhythmic vibration when finishing 100 or countdown reaches 0.
     */
    fun performMilestone(enabled: Boolean, strength: String = "MEDIUM") {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return

        val amp = when (strength) {
            "LIGHT" -> 120
            "STRONG" -> 255
            else -> 200
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val timings = longArrayOf(0, 50, 70, 70, 70, 120)
                val amplitudes = intArrayOf(0, (amp * 0.7).toInt(), 0, amp, 0, amp)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } catch (_: Exception) {
                @Suppress("DEPRECATION")
                vibrator.vibrate(250)
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 70, 70, 70, 120), -1)
        }
    }
}
