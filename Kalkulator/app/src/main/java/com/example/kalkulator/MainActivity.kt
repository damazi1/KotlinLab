package com.example.kalkulator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kalkulator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val currentExpression = StringBuilder()
    private val history = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberButtons()
        setupOperatorButtons()
        setupControlButtons()
        setupBaseConversionButtons()
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7,
            binding.button8, binding.button9
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                currentExpression.append(index)
                updateDisplay()
            }
        }

        binding.buttonDot.setOnClickListener {
            if (currentExpression.isEmpty() || isLastCharOperator()) {
                currentExpression.append("0.")
            } else if (!getCurrentNumber().contains(".")) {
                currentExpression.append(".")
            }
            updateDisplay()
        }
    }

    private fun getCurrentNumber(): String {
        val expr = currentExpression.toString()
        val lastOperatorIndex = expr.lastIndexOfAny(charArrayOf('+', '-', '*', '/', '^'))
        return if (lastOperatorIndex == -1) expr else expr.substring(lastOperatorIndex + 1)
    }

    private fun setupOperatorButtons() {
        binding.buttonAdd.setOnClickListener { appendOperator("+") }
        binding.buttonSubtract.setOnClickListener { appendOperator("-") }
        binding.buttonMultiply.setOnClickListener { appendOperator("×") }
        binding.buttonDivide.setOnClickListener { appendOperator("÷") }
        binding.buttonPow.setOnClickListener { appendOperator("^") }
    }

    private fun appendOperator(op: String) {
        if (currentExpression.isNotEmpty() && !isLastCharOperator()) {
            currentExpression.append(op)
            updateDisplay()
        }
    }

    private fun isLastCharOperator(): Boolean {
        if (currentExpression.isEmpty()) return true
        val last = currentExpression.last()
        return last in listOf('+', '-', '×', '÷', '^', '*', '/')
    }

    private fun setupControlButtons() {
        binding.buttonClear.setOnClickListener {
            currentExpression.clear()
            binding.textViewResult.text = "0"
            binding.previousText.text = ""
        }

        binding.buttonEquals.setOnClickListener {
            if (currentExpression.isNotEmpty() && !isLastCharOperator()) {
                val expression = currentExpression.toString()
                val result = calculateSequentially(expression)
                val formattedResult = formatResult(result)
                val historyEntry = "$expression = $formattedResult"
                history.add(historyEntry)
                binding.previousText.text = expression
                binding.textViewResult.text = formattedResult
                currentExpression.clear()
                currentExpression.append(formattedResult)
            }
        }

        binding.buttonHistory.setOnClickListener {
            val intent = android.content.Intent(this, HistoryActivity::class.java)
            intent.putStringArrayListExtra("history", history)
            startActivity(intent)
        }
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }

    private fun setupBaseConversionButtons() {
        var currentBase = 10

        binding.buttonDecimal.setOnClickListener {
            val value = currentExpression.toString().ifEmpty { "0" }
            val decimal = toDecimal(value, currentBase)
            currentExpression.clear()
            currentExpression.append(decimal.toString())
            currentBase = 10
            updateDisplay()
        }

        binding.buttonBinary.setOnClickListener {
            val value = currentExpression.toString().ifEmpty { "0" }
            val decimal = toDecimal(value, currentBase)
            currentExpression.clear()
            currentExpression.append(toBinary(decimal))
            currentBase = 2
            updateDisplay()
        }

        binding.buttonOctal.setOnClickListener {
            val value = currentExpression.toString().ifEmpty { "0" }
            val decimal = toDecimal(value, currentBase)
            currentExpression.clear()
            currentExpression.append(toOctal(decimal))
            currentBase = 8
            updateDisplay()
        }

        binding.buttonHex.setOnClickListener {
            val value = currentExpression.toString().ifEmpty { "0" }
            val decimal = toDecimal(value, currentBase)
            currentExpression.clear()
            currentExpression.append(toHexadecimal(decimal))
            currentBase = 16
            updateDisplay()
        }
    }

    private fun calculateSequentially(expression: String): Double {
        // Zamień symbole na standardowe operatory
        val normalizedExpr = expression.replace('×', '*').replace('÷', '/')

        val operators = mutableListOf<Char>()
        val numbers = mutableListOf<String>()
        var currentNum = StringBuilder()

        for (char in normalizedExpr) {
            if (char in listOf('+', '-', '*', '/', '^')) {
                if (currentNum.isNotEmpty()) {
                    numbers.add(currentNum.toString())
                    currentNum.clear()
                }
                operators.add(char)
            } else {
                currentNum.append(char)
            }
        }
        if (currentNum.isNotEmpty()) {
            numbers.add(currentNum.toString())
        }

        if (numbers.isEmpty()) return 0.0

        var result = numbers[0].toDoubleOrNull() ?: 0.0

        for (i in operators.indices) {
            val nextNum = numbers.getOrNull(i + 1)?.toDoubleOrNull() ?: 0.0
            result = when (operators[i]) {
                '+' -> result + nextNum
                '-' -> result - nextNum
                '*' -> result * nextNum
                '/' -> if (nextNum != 0.0) result / nextNum else Double.NaN
                '^' -> Math.pow(result, nextNum)
                else -> result
            }
        }

        return result
    }

    private fun updateDisplay() {
        binding.textViewResult.text = if (currentExpression.isEmpty()) "0" else currentExpression.toString()
    }

    private fun toDecimal(value: String, fromBase: Int): Long {
        return try {
            value.toLong(fromBase)
        } catch (e: NumberFormatException) {
            0L
        }
    }

    private fun toBinary(decimalValue: Long): String = decimalValue.toString(2)
    private fun toOctal(decimalValue: Long): String = decimalValue.toString(8)
    private fun toHexadecimal(decimalValue: Long): String = decimalValue.toString(16).uppercase()
}