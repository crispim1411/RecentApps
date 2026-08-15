package com.crispim.recentapps

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Path
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy", "ClickableViewAccessibility")
class MainService : AccessibilityService() {
    private lateinit var displayManager: DisplayManager
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var navigationModeObserver: ContentObserver? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager

        updateOverlayForCurrentNavigationMode()
        registerNavigationModeObserver()
    }

    private fun isGestureNavigationEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(contentResolver, "navigation_mode") == 2
        } catch (_: Exception) {
            false
        }
    }

    private fun updateOverlayForCurrentNavigationMode() {
        if (isGestureNavigationEnabled()) {
            if (overlayView == null) addGestureOverlay()
        } else removeGestureOverlay()
    }

    private fun registerNavigationModeObserver() {
        val uri = Settings.Secure.getUriFor("navigation_mode")
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                updateOverlayForCurrentNavigationMode()
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        navigationModeObserver = observer
    }

    @Suppress("SameParameterValue")
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun addGestureOverlay() {
        val coverDisplay = displayManager.getDisplay(1) ?: return

        val displayContext = createDisplayContext(coverDisplay)
        val wm = displayContext.getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPx(26),
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }

        val handler = Handler(Looper.getMainLooper())
        val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
        var longPressTriggered = false
        var startX = 0f
        var startY = 0f

        val longPressRunnable = Runnable {
            longPressTriggered = true
            vibrate()
            startTriggerActivity()
        }

        val view = View(displayContext)
        view.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    longPressTriggered = false
                    handler.postDelayed(longPressRunnable, longPressTimeout)
                    true
                }

                MotionEvent.ACTION_MOVE -> true

                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (!longPressTriggered)
                        replayTap(startX, startY, handler, coverDisplay.displayId, wm, view, params)
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                    true
                }
                else -> false
            }
        }

        wm.addView(view, params)

        overlayView = view
        windowManager = wm
    }

    private fun replayTap(
        x: Float,
        y: Float,
        handler: Handler,
        displayId: Int,
        wm: WindowManager,
        view: View,
        params: WindowManager.LayoutParams
    ) {
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        wm.updateViewLayout(view, params)

        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 100)
        val gesture = GestureDescription.Builder()
            .addStroke(stroke)
            .setDisplayId(displayId)
            .build()

        fun restoreTouchable() {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            wm.updateViewLayout(view, params)
        }

        val dispatched = dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                restoreTouchable()
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                restoreTouchable()
            }
        }, handler)

        if (!dispatched) restoreTouchable()
    }

    private fun removeGestureOverlay() {
        overlayView?.let { v ->
            try {
                windowManager?.removeView(v)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        overlayView = null
        windowManager = null
    }

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

    override fun onDestroy() {
        super.onDestroy()
        removeGestureOverlay()
        navigationModeObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }

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
