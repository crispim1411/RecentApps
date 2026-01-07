package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
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
            val intent = Intent().apply {
                component = ComponentName(
                    "com.sec.android.app.launcher",
                    "com.android.quickstep.RecentsActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }

            val options = ActivityOptions.makeBasic()
            val coverDisplay = displayManager.getDisplay(1)

            if (coverDisplay != null) {
                options.launchDisplayId = coverDisplay.displayId
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
