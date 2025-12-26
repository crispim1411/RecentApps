package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {

    private var pendingVolumeUpRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var audioManager: AudioManager
    private var hasVolumeRaised: Boolean = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        try {
            val displayManager =
                getSystemService(DISPLAY_SERVICE) as android.hardware.display.DisplayManager
            val mainDisplay = displayManager.getDisplay(0)
            if (event.scanCode != Constants.SCAN_CODE_VOLUME_UP
                || event.action == KeyEvent.ACTION_DOWN
                || mainDisplay?.state == android.view.Display.STATE_ON) {
                return super.onKeyEvent(event)
            }

            if (event.action == KeyEvent.ACTION_UP) {
                if (pendingVolumeUpRunnable != null) {
                    handler.removeCallbacks(pendingVolumeUpRunnable!!)
                    pendingVolumeUpRunnable = null
                    restoreVolume()
                    openRecentApps()
                    return true
                } else {
                    hasVolumeRaised = true
                    pendingVolumeUpRunnable = Runnable {
                        pendingVolumeUpRunnable = null
                        hasVolumeRaised = false
                    }
                    val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                    val clickDelay =
                        prefs.getInt(Constants.KEY_CLICK_DELAY, Constants.DEFAULT_CLICK_DELAY)
                            .toLong()
                    handler.postDelayed(pendingVolumeUpRunnable!!, clickDelay)
                }
            }
        } catch (e: Exception) {
            showToast(this, "onKeyEvent Error: ${e.message}")
        }

        return super.onKeyEvent(event)
    }

    private fun openRecentApps() {
        try {
            val recentAppsIntent = Intent()
            recentAppsIntent.component = android.content.ComponentName(
                Constants.SAMSUNG_LAUNCHER_PACKAGE,
                Constants.RECENTS_ACTIVITY_CLASS
            )
            recentAppsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            recentAppsIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

            val options = android.app.ActivityOptions.makeBasic()
            val displayManager = getSystemService(DISPLAY_SERVICE) as android.hardware.display.DisplayManager
            val targetDisplay = displayManager.getDisplay(1)

            if (targetDisplay != null) {
                options.launchDisplayId = targetDisplay.displayId
                startActivity(recentAppsIntent, options.toBundle())
            }
        } catch (e: Exception) {
            showToast(this, "onKeyEvent Error: ${e.message}")
        }
    }

    private fun restoreVolume() {
        if (hasVolumeRaised) {
            try {
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    currentVolume-2,
                    0
                )
            } catch (e: Exception) {
                showToast(this, "Failed to restore volume: ${e.message}")
            } finally {
                hasVolumeRaised = false
            }
        }
    }
}
