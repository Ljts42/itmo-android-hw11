package com.github.ljts42.dopochki

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class Calculator(
    private val root: View, private val clipboard: ClipboardManager
) {
    private val prevText = root.findViewById<TextView>(R.id.prevText)
    private val resultText = root.findViewById<TextView>(R.id.resultText)
    private val buttonDot = root.findViewById<Button>(R.id.buttonDot)

    var hasDot = false

    fun initButtons() {
        listOf(
            root.findViewById(R.id.button0),
            root.findViewById(R.id.button1),
            root.findViewById(R.id.button2),
            root.findViewById(R.id.button3),
            root.findViewById(R.id.button4),
            root.findViewById(R.id.button5),
            root.findViewById(R.id.button6),
            root.findViewById(R.id.button7),
            root.findViewById(R.id.button8),
            root.findViewById<Button>(R.id.button9),
        ).forEach { initNumber(it) }

        listOf(
            root.findViewById(R.id.buttonAdd),
            root.findViewById(R.id.buttonSub),
            root.findViewById(R.id.buttonMul),
            root.findViewById<Button>(R.id.buttonDiv)
        ).forEach { initOperation(it) }

        buttonDot.setOnClickListener {
            if (prevText.text.isEmpty() || prevText.text.last() in "+-*/") {
                if (resultText.text.last() == '-') {
                    resultText.append("0")
                }
                if (!hasDot) {
                    resultText.append(buttonDot.text)
                }
            } else {
                prevText.append(resultText.text)
                resultText.text = "0."
            }
            hasDot = true
        }

        root.findViewById<Button>(R.id.buttonClear).setOnClickListener {
            prevText.text = ""
            resultText.text = "0"
            hasDot = false
        }

        root.findViewById<Button>(R.id.buttonDel).setOnClickListener {
            if (resultText.text.length == 1) {
                if (prevText.text.isEmpty()) {
                    resultText.text = "0"
                } else if (prevText.text.last() in "+-*/") {
                    resultText.text = prevText.text.last().toString()
                    prevText.text = prevText.text.dropLast(1)
                } else {
                    resultText.text = prevText.text
                    prevText.text = ""
                    hasDot = buttonDot.text in resultText.text
                }
            } else {
                if (resultText.text.last() == '.') {
                    hasDot = false
                }
                resultText.text = resultText.text.dropLast(1)
            }
        }

        root.findViewById<Button>(R.id.buttonCopy).setOnClickListener {
            val clip = ClipData.newPlainText(resultText.text, resultText.text)
            clipboard.setPrimaryClip(clip)
        }

        root.findViewById<Button>(R.id.buttonEq).setOnClickListener {
            if (prevText.text.isNotEmpty()) {
                if (prevText.text.last() in "+-*/") {
                    if (resultText.text.last() == '.') {
                        resultText.append("0")
                    }
                    val left =
                        prevText.text.dropLast(1).toString().replace(buttonDot.text.toString(), ".")
                    val right = resultText.text.toString().replace(buttonDot.text.toString(), ".")
                    resultText.text = compute(
                        left, prevText.text.last(), right
                    ).replace(".", buttonDot.text.toString())
                } else {
                    resultText.text = prevText.text
                }
                hasDot = '.' in resultText.text
                prevText.text = ""
            }
        }
    }

    private fun compute(first: String, sign: Char, second: String): String {
        if (sign !in "+-*/") {
            return "wrong sign"
        }
        val left = try {
            BigDecimal(first)
        } catch (e: NumberFormatException) {
            return "wrong first number format"
        }
        val right = try {
            BigDecimal(second)
        } catch (e: NumberFormatException) {
            return "wrong second number format"
        }
        return try {
            DecimalFormat("0.#####").format(
                when (sign) {
                    '*' -> left.multiply(right)
                    '-' -> left.subtract(right)
                    '/' -> left.divide(right, 5, RoundingMode.HALF_UP)
                    else -> left.plus(right)
                }
            )
        } catch (e: ArithmeticException) {
            "division by zero"
        }
    }

    private fun initNumber(button: Button) {
        button.setOnClickListener {
            if (resultText.text.toString() == "0") {
                resultText.text = button.text
            } else if (prevText.text.isEmpty() || prevText.text.last() in "+-*/") {
                resultText.append(button.text)
            } else {
                prevText.append(resultText.text.last().toString())
                resultText.text = button.text
            }
        }
    }

    private fun initOperation(button: Button) {
        button.setOnClickListener {
            if (resultText.text.toString() == "-") {
                resultText.text = "0"
            }
            if (prevText.text.isEmpty()) {
                prevText.text = resultText.text
                hasDot = false
            } else if (prevText.text.last() in "+-*/") {
                if (resultText.text.last() == '.') {
                    resultText.append("0")
                }
                val left =
                    prevText.text.dropLast(1).toString().replace(buttonDot.text.toString(), ".")
                val right = resultText.text.toString().replace(buttonDot.text.toString(), ".")
                prevText.text = compute(
                    left, prevText.text.last(), right
                ).replace(".", buttonDot.text.toString())
            }
            resultText.text = button.text
        }
    }
}