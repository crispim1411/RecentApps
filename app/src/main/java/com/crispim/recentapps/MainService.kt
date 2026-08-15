package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            val packageName = event.packageName?.toString()
            event.source?.let { sourceNode ->
                val viewIdResourceName = sourceNode.viewIdResourceName
                if (packageName == "com.android.systemui" && viewIdResourceName == "com.android.systemui:id/home") {
                    vibrate()
                    startTriggerActivity()
                }
            }
        }
    }

    override fun onInterrupt() {}

    private fun startTriggerActivity() {
        val intent = Intent(this, TriggerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        startActivity(intent)
    }

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
}
