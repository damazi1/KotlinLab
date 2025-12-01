package com.example.kalkulator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.kalkulator.databinding.ActivityMainBinding
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentInput = StringBuilder()
    private var operand1: Double? = null
    private var pendingOperation = ""
    private var resultCalculated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numberClickListener = View.OnClickListener { v ->
            if (resultCalculated) {
                currentInput.clear()
                binding.previousText.text = ""
                resultCalculated = false
            }
            val button = v as Button
            currentInput.append(button.text)
            updateResultTextView()
        }

        binding.button0.setOnClickListener(numberClickListener)
        binding.button1.setOnClickListener(numberClickListener)
        binding.button2.setOnClickListener(numberClickListener)
        binding.button3.setOnClickListener(numberClickListener)
        binding.button4.setOnClickListener(numberClickListener)
        binding.button5.setOnClickListener(numberClickListener)
        binding.button6.setOnClickListener(numberClickListener)
        binding.button7.setOnClickListener(numberClickListener)
        binding.button8.setOnClickListener(numberClickListener)
        binding.button9.setOnClickListener(numberClickListener)

        binding.buttonDot.setOnClickListener {
            if (resultCalculated) {
                currentInput.clear()
                binding.previousText.text = ""
                resultCalculated = false
            }
            if (!currentInput.contains(".")) {
                currentInput.append(".")
                updateResultTextView()
            }
        }

        val operationClickListener = View.OnClickListener { v ->
            val button = v as Button
            val operation = button.text.toString()
            if (currentInput.isNotEmpty()) {
                operand1 = currentInput.toString().toDouble()
                binding.previousText.text = "$operand1 $operation"
                currentInput.clear()
                pendingOperation = operation
                resultCalculated = false
            }
        }

        binding.buttonAdd.setOnClickListener(operationClickListener)
        binding.buttonSubtract.setOnClickListener(operationClickListener)
        binding.buttonMultiply.setOnClickListener(operationClickListener)
        binding.buttonDivide.setOnClickListener(operationClickListener)
        binding.buttonPow.setOnClickListener(operationClickListener)

        binding.buttonEquals.setOnClickListener {
            if (currentInput.isNotEmpty() && operand1 != null) {
                val operand2 = currentInput.toString().toDouble()
                val result = performOperation(operand1!!, operand2, pendingOperation)
                binding.previousText.text = "${operand1!!} $pendingOperation $operand2 = $result"
                binding.textViewResult.text = result.toString()
                currentInput.clear()
                currentInput.append(result)
                operand1 = null
                pendingOperation = ""
                resultCalculated = true
            }
        }

        binding.buttonClear.setOnClickListener {
            currentInput.clear()
            operand1 = null
            pendingOperation = ""
            binding.textViewResult.text = "0"
            binding.previousText.text = ""
            resultCalculated = false
        }
    }

    private fun updateResultTextView() {
        binding.textViewResult.text = currentInput.toString()
    }

    private fun performOperation(operand1: Double, operand2: Double, operation: String): Double {
        return when (operation) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "*" -> operand1 * operand2
            "/" -> if (operand2 != 0.0) operand1 / operand2 else Double.NaN // Handle division by zero
            "^" -> operand1.pow(operand2)
            else -> 0.0
        }
    }
}