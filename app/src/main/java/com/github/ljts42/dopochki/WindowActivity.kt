package com.github.ljts42.dopochki

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class WindowActivity : AppCompatActivity() {
    private lateinit var window: View
    private lateinit var wm: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var REQUEST_CODE = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window)

        if (Settings.canDrawOverlays(this)) {
            launchCalculator()
        } else {
            checkPermission()
        }
    }

    private fun launchCalculator() {
        finish()
        makeWindow()
        wm.addView(window, layoutParams)

        val calculator = Calculator(window, getSystemService(CLIPBOARD_SERVICE) as ClipboardManager)

        calculator.initButtons()
        window.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            wm.removeView(window)
        }
    }

    private fun makeWindow() {
        window = LayoutInflater.from(this).inflate(R.layout.calculator_window, null)

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = 0
            y = 0
        }

        window.setOnTouchListener { view, motionEvent ->
            val params = view.layoutParams as WindowManager.LayoutParams
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = motionEvent.rawX.toInt()
                    val y = motionEvent.rawY.toInt()
                    view.tag = Pair(x - params.x, y - params.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    val tag = view.tag as Pair<Int, Int>
                    val x = motionEvent.rawX.toInt()
                    val y = motionEvent.rawY.toInt()
                    params.x = x - tag.first
                    params.y = y - tag.second
                    wm.updateViewLayout(view, params)
                }
                else -> {
                    view.performClick()
                }
            }
            true
        }
    }

    private fun checkPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                launchCalculator()
            } else {
                checkPermission();
            }
        }
    }
}