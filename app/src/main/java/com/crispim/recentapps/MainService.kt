package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.app.ActivityOptions
import android.hardware.display.DisplayManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {
    private lateinit var displayManager: DisplayManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            val packageName = event.packageName?.toString()
            event.source?.let { sourceNode ->
                val viewIdResourceName = sourceNode.viewIdResourceName
                if (packageName == "com.android.systemui" && viewIdResourceName == "com.android.systemui:id/home") {
                    vibrate()
                    openRecentApps()
                }
            }
        }
    }

    override fun onInterrupt() {}

    private fun vibrate() {
        getSystemService(Vibrator::class.java)?.let {
            if (it.hasVibrator()) {
                it.vibrate(
                    VibrationEffect.createOneShot(
                        30,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
    }

    private fun openRecentApps() {
        try {
            val recentAppsIntent = Intent()
            recentAppsIntent.component = android.content.ComponentName(
                Constants.SAMSUNG_LAUNCHER_PACKAGE,
                Constants.RECENTS_ACTIVITY_CLASS
            )
            recentAppsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val options = ActivityOptions.makeBasic()
            val coverDisplay = displayManager.getDisplay(1)

            if (coverDisplay != null) {
                options.launchDisplayId = coverDisplay.displayId
                startActivity(recentAppsIntent, options.toBundle())
            }
        } catch (e: Exception) {
            showToast(this, "onKeyEvent Error: ${e.message}")
        }
    }
}
