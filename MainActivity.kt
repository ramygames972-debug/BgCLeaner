package com.example.bgcleaner

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.concurrent.thread

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.cleanBtn)
        val status = findViewById<TextView>(R.id.status)
        btn.requestFocus()
        btn.setOnClickListener {
            btn.isEnabled = false
            status.text = "جارٍ التنظيف..."
            thread {
                val result = cleanBackground()
                runOnUiThread {
                    status.text = result
                    btn.isEnabled = true
                    btn.requestFocus()
                }
            }
        }
    }

    private fun freeMb(am: ActivityManager): Long {
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem / (1024 * 1024)
    }

    private fun cleanBackground(): String {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val home = packageManager.resolveActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0
        )?.activityInfo?.packageName
        val before = freeMb(am)
        var count = 0
        for (app in packageManager.getInstalledApplications(0)) {
            val p = app.packageName
            if (p == packageName || p == home) continue
            try { am.killBackgroundProcesses(p); count++ } catch (_: Exception) {}
        }
        Thread.sleep(1500)
        val after = freeMb(am)
        return "تمت معالجة $count تطبيقًا\nالذاكرة الحرة: $before MB ← $after MB"
    }
}
