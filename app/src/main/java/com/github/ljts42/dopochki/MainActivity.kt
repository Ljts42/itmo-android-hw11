package com.github.ljts42.dopochki

import android.content.ClipboardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.ljts42.dopochki.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var calc: Calculator
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calc = Calculator(binding.root, getSystemService(CLIPBOARD_SERVICE) as ClipboardManager)
        calc.initButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(HAS_DOT, calc.hasDot)
        outState.putString(PREV_LINE, binding.prevText.text.toString())
        outState.putString(RESULT_LINE, binding.resultText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        calc.hasDot = savedInstanceState.getBoolean(HAS_DOT)
        binding.prevText.text = savedInstanceState.getString(PREV_LINE)
        binding.resultText.text = savedInstanceState.getString(RESULT_LINE)
    }

    companion object {
        private const val HAS_DOT = "ljts42.MainActivity.has_dot"
        private const val PREV_LINE = "ljts42.MainActivity.prev_line"
        private const val RESULT_LINE = "ljts42.MainActivity.result_line"
    }
}