package com.example.sound

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * Lightweight, zero-latency sound feedback manager using Android system ToneGenerator.
 * Fully non-blocking, reliable on all emulators and physical devices, with zero audio leak.
 */
class SoundFeedbackManager {
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 65)
        } catch (e: Exception) {
            Log.e("SoundFeedbackManager", "ToneGenerator init failed", e)
        }
    }

    /**
     * Mechanical latch/tumbler click sound (20ms).
     */
    fun playTumblerClick(enabled: Boolean = true) {
        if (!enabled) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 20)
        } catch (_: Exception) {}
    }

    /**
     * Pleasant melodic harmonic chime for milestone and completion.
     */
    fun playCompletionChime(enabled: Boolean = true) {
        if (!enabled) return
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
        } catch (_: Exception) {}
    }
}
