package com.almusalli.app

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {
    private var selectedHour = 16
    private var selectedMinute = 55
    private lateinit var timeLabel: TextView
    private lateinit var statusLabel: TextView

    private val gold = Color.rgb(211, 177, 105)
    private val background = Color.rgb(8, 13, 19)
    private val panel = Color.rgb(20, 29, 39)
    private val white = Color.WHITE
    private val muted = Color.rgb(169, 181, 193)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        buildScreen()
        refreshStatus()
    }

    private fun buildScreen() {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(20))
            setBackgroundColor(background)
        }

        val brand = TextView(this).apply {
            text = "☾  المُصلّي"
            textSize = 27f
            setTextColor(gold)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        outer.addView(brand, matchWrap())

        val subtitle = TextView(this).apply {
            text = "رفيقك اليومي للعبادة والطمأنينة"
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(7), 0, dp(25))
        }
        outer.addView(subtitle, matchWrap())

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(25), dp(18), dp(25))
            background = rounded(panel, 22)
        }
        val welcome = TextView(this).apply {
            text = "السلام عليكم"
            textSize = 16f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }
        hero.addView(welcome, matchWrap())
        val title = TextView(this).apply {
            text = "حافظ على صلاتك"
            textSize = 25f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(5))
        }
        hero.addView(title, matchWrap())
        val detail = TextView(this).apply {
            text = "اجعل الصلاة موعدك الأجمل في اليوم"
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }
        hero.addView(detail, matchWrap())
        outer.addView(hero, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(20) })

        val section = TextView(this).apply {
            text = "تنبيه الصلاة"
            textSize = 18f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(2), dp(5), 0, dp(12))
        }
        outer.addView(section, matchWrap())

        val timeCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(17), dp(18), dp(17))
            background = rounded(panel, 18)
        }
        val choose = TextView(this).apply {
            text = "وقت التنبيه اليومي"
            textSize = 13f
            setTextColor(muted)
        }
        timeCard.addView(choose, matchWrap())
        timeLabel = TextView(this).apply {
            textSize = 34f
            setTextColor(gold)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(8), 0, dp(12))
        }
        timeCard.addView(timeLabel, matchWrap())
        val pickButton = makeButton("اختيار الوقت", gold, background)
        pickButton.setOnClickListener {
            TimePickerDialog(this, { _: TimePicker, h: Int, m: Int ->
                selectedHour = h
                selectedMinute = m
                updateTimeLabel()
            }, selectedHour, selectedMinute, false).show()
        }
        timeCard.addView(pickButton, matchWrap())
        outer.addView(timeCard, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(14) })

        val scheduleButton = makeButton("تفعيل التنبيه والرنين", gold, background)
        scheduleButton.setOnClickListener { scheduleReminder() }
        outer.addView(scheduleButton, LinearLayout.LayoutParams(-1, dp(54)).apply { bottomMargin = dp(12) })

        val cancelButton = makeButton("إيقاف التنبيه اليومي", Color.rgb(45, 58, 71), white)
        cancelButton.setOnClickListener {
            PrayerAlarmReceiver.cancel(this)
            refreshStatus("تم إيقاف التنبيه اليومي")
        }
        outer.addView(cancelButton, LinearLayout.LayoutParams(-1, dp(50)).apply { bottomMargin = dp(14) })

        statusLabel = TextView(this).apply {
            textSize = 12f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(12))
        }
        outer.addView(statusLabel, matchWrap())

        val footer = TextView(this).apply {
            text = "القرآن  •  الأذكار  •  التسبيح  •  القبلة\nنسخة تأسيسية — ستضاف الوحدات تباعًا"
            textSize = 12f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, 0)
        }
        outer.addView(footer, matchWrap())

        setContentView(outer)
        updateTimeLabel()
    }

    private fun scheduleReminder() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 200)
            refreshStatus("اسمح بالإشعارات ثم اضغط تفعيل مرة أخرى")
            return
        }
        if (Build.VERSION.SDK_INT >= 31) {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")))
                refreshStatus("اسمح بالمنبهات الدقيقة ثم ارجع واضغط تفعيل")
                return
            }
        }
        getSharedPreferences("almusalli", MODE_PRIVATE).edit()
            .putInt("hour", selectedHour).putInt("minute", selectedMinute).putBoolean("enabled", true).apply()
        PrayerAlarmReceiver.schedule(this, selectedHour, selectedMinute)
        refreshStatus("تم تفعيل التنبيه اليومي")
        if (Build.VERSION.SDK_INT >= 34) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                Uri.parse("package:$packageName"))
            try { startActivity(intent) } catch (_: Exception) { }
        }
    }

    private fun refreshStatus(message: String? = null) {
        val enabled = getSharedPreferences("almusalli", MODE_PRIVATE).getBoolean("enabled", false)
        statusLabel.text = message ?: if (enabled) "● التنبيه اليومي مفعّل" else "التنبيه غير مفعّل"
        statusLabel.setTextColor(if (enabled) gold else muted)
    }

    private fun updateTimeLabel() {
        val ampm = if (selectedHour < 12) "ص" else "م"
        val h = when (val v = selectedHour % 12) { 0 -> 12; else -> v }
        timeLabel.text = String.format(Locale.getDefault(), "%02d:%02d %s", h, selectedMinute, ampm)
    }

    private fun makeButton(text: String, bg: Int, fg: Int) = Button(this).apply {
        this.text = text
        setTextColor(fg)
        textSize = 14f
        isAllCaps = false
        background = rounded(bg, 14)
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
}
