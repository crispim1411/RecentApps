package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.crispim.coverspin.showToast

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {

    private var pendingVolumeUpRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        try {
            val displayManager =
                getSystemService(DISPLAY_SERVICE) as android.hardware.display.DisplayManager
            val mainDisplay = displayManager.getDisplay(0)

            if (event.action == KeyEvent.ACTION_DOWN
                || event.scanCode != AppConstants.SCAN_CODE_VOLUME_UP
                || mainDisplay?.state == android.view.Display.STATE_ON
            ) {
                return super.onKeyEvent(event)
            }

            if (event.action == KeyEvent.ACTION_UP) {
                if (pendingVolumeUpRunnable != null) {
                    handler.removeCallbacks(pendingVolumeUpRunnable!!)
                    pendingVolumeUpRunnable = null
                    openRecentApps()
                    return true
                } else {
                    pendingVolumeUpRunnable = Runnable {
                        pendingVolumeUpRunnable = null
                    }
                    val prefs = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
                    val clickDelay =
                        prefs.getInt(AppConstants.KEY_CLICK_DELAY, AppConstants.DEFAULT_CLICK_DELAY)
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
                AppConstants.SAMSUNG_LAUNCHER_PACKAGE,
                AppConstants.RECENTS_ACTIVITY_CLASS
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
}
