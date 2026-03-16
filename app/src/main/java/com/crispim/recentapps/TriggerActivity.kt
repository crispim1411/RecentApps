package com.crispim.recentapps

import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle

class TriggerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        openRecentApps()
        
        finish()
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

            val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
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
