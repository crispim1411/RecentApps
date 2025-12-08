package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {

    private val SCAN_CODE_VOLUME_UP = 114
    private var pendingVolumeUpRunnable: Runnable? = null
    private val clickDelay = 300L
    private val handler = Handler(Looper.getMainLooper())

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
        val mainDisplay = displayManager.getDisplay(0)
        if (mainDisplay?.state == android.view.Display.STATE_ON) {
            return super.onKeyEvent(event)
        }

        val action = event.action

        if (event.scanCode == SCAN_CODE_VOLUME_UP && action == KeyEvent.ACTION_UP) {
            return true
        }

        if (event.scanCode == SCAN_CODE_VOLUME_UP && action == KeyEvent.ACTION_DOWN) {
            if (pendingVolumeUpRunnable != null) {
                handler.removeCallbacks(pendingVolumeUpRunnable!!)
                pendingVolumeUpRunnable = null
                openRecentApps()
                return true
            }
            else {
                pendingVolumeUpRunnable = Runnable {
                    adjustVolume()
                    pendingVolumeUpRunnable = null
                }
                handler.postDelayed(pendingVolumeUpRunnable!!, clickDelay)
                return true
            }
        }

        return super.onKeyEvent(event)
    }

    private fun adjustVolume() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI // Mostra a barra de volume na tela
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openRecentApps() {
        try {
            val samsungIntent = Intent()
            samsungIntent.component = android.content.ComponentName(
                "com.sec.android.app.launcher",
                "com.android.quickstep.RecentsActivity"
            )
            samsungIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            samsungIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) // Tenta forçar reinício

            val options = android.app.ActivityOptions.makeBasic()
            val displayManager = getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
            val targetDisplay = displayManager.getDisplay(1)

            if (targetDisplay != null) {
                options.launchDisplayId = targetDisplay.displayId
                startActivity(samsungIntent, options.toBundle())
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}