package com.almusalli.app

import android.app.Activity
import android.os.Bundle
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.AudioAttributes
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class CallActivity : Activity() {
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        render()
        startRinging()
    }

    private fun render() {
        val bg = Color.rgb(8, 13, 19)
        val gold = Color.rgb(211, 177, 105)
        val muted = Color.rgb(177, 188, 198)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 40, 28, 40)
            setBackgroundColor(bg)
        }
        val brand = TextView(this).apply {
            text = "☾"
            textSize = 58f
            setTextColor(gold)
            gravity = Gravity.CENTER
        }
        root.addView(brand)
        val title = TextView(this).apply {
            text = "حان وقت الصلاة"
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 8)
        }
        root.addView(title)
        val subtitle = TextView(this).apply {
            text = "تقبّل الله منا ومنكم صالح الأعمال"
            textSize = 15f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }
        root.addView(subtitle)
        val spacer = TextView(this).apply { text = ""; height = 90 }
        root.addView(spacer)
        val answer = Button(this).apply {
            text = "إيقاف الرنين"
            textSize = 16f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(28, 155, 99))
            setOnClickListener {
                stopRinging()
                (getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
                    .cancel(PrayerAlarmReceiver.NOTIFICATION_ID)
                Toast.makeText(this@CallActivity, "تقبّل الله طاعتكم", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        root.addView(answer, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 14 })
        val dismiss = Button(this).apply {
            text = "إغلاق"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(65, 75, 86))
            setOnClickListener {
                stopRinging()
                (getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
                    .cancel(PrayerAlarmReceiver.NOTIFICATION_ID)
                finish()
            }
        }
        root.addView(dismiss, LinearLayout.LayoutParams(-1, 54))
        setContentView(root)
    }

    private fun rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = 24f
    }

    private fun startRinging() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, uri)
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            ringtone?.play()
        } catch (_: Exception) { }
    }

    private fun stopRinging() {
        try { ringtone?.stop() } catch (_: Exception) {}
    }

    override fun onDestroy() {
        stopRinging()
        super.onDestroy()
    }
}
