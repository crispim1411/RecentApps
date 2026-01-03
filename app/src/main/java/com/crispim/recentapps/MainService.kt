package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MainService : AccessibilityService() {

    private val SCAN_CODE_VOLUME_UP = 114
    private val SCAN_CODE_VOLUME_DOWN = 115

    private var pendingUp: Runnable? = null
    private var pendingDown: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val displayManager = getSystemService(DisplayManager::class.java)
        val mainDisplay = displayManager?.getDisplay(0)

        // Only work when phone is closed (cover screen)
        if (mainDisplay?.state == android.view.Display.STATE_ON) {
            return super.onKeyEvent(event)
        }

        val delay = getSharedPreferences("RecentAppsPrefs", MODE_PRIVATE)
            .getInt("CLICK_DELAY_MS", 300)
            .toLong()

        /* ---------------- VOLUME UP ---------------- */
        if (event.scanCode == SCAN_CODE_VOLUME_UP && event.action == KeyEvent.ACTION_DOWN) {
            if (pendingUp != null) {
                handler.removeCallbacks(pendingUp!!)
                pendingUp = null
                vibrate()
                openRecentApps()
            } else {
                pendingUp = Runnable {
                    adjustVolume(AudioManager.ADJUST_RAISE)
                    pendingUp = null
                }
                handler.postDelayed(pendingUp!!, delay)
            }
            return true
        }

        /* ---------------- VOLUME DOWN ---------------- */
        if (event.scanCode == SCAN_CODE_VOLUME_DOWN && event.action == KeyEvent.ACTION_DOWN) {
            if (pendingDown != null) {
                handler.removeCallbacks(pendingDown!!)
                pendingDown = null
                vibrate()
                toggleCoverSpin()
            } else {
                pendingDown = Runnable {
                    adjustVolume(AudioManager.ADJUST_LOWER)
                    pendingDown = null
                }
                handler.postDelayed(pendingDown!!, delay)
            }
            return true
        }

        return super.onKeyEvent(event)
    }

    /* ---------------- HELPERS ---------------- */

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

    private fun adjustVolume(direction: Int) {
        getSystemService(AudioManager::class.java)
            ?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                direction,
                AudioManager.FLAG_SHOW_UI
            )
    }

    /* ---------------- TOGGLE LOGIC ---------------- */

    private fun isCoverSpinOpen(): Boolean {
        val root = rootInActiveWindow ?: return false
        return root.packageName?.toString() == "com.crispim.coverspin"
    }

    private fun toggleCoverSpin() {
        try {
            if (isCoverSpinOpen()) {
                performGlobalAction(GLOBAL_ACTION_BACK)
            } else {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.crispim.coverspin",
                        "com.crispim.coverspin.MainActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            openCoverHome()
        }
    }

    /* ---------------- ACTIONS ---------------- */

    private fun openRecentApps() {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.sec.android.app.launcher",
                    "com.android.quickstep.RecentsActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val options = ActivityOptions.makeBasic()
            val coverDisplay = getSystemService(DisplayManager::class.java)
                ?.getDisplay(1)

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

    private fun openCoverHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}
