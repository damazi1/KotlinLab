package com.example.kalkulator

        import android.os.Bundle
        import androidx.appcompat.app.AppCompatActivity
        import com.example.kalkulator.databinding.ActivityMainBinding
        import java.util.Stack

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
                val lastOperatorIndex = expr.lastIndexOfAny(charArrayOf('+', '-', '*', '/', '^', '(', ')'))
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
                return last in listOf('+', '-', '×', '÷', '^', '*', '/', '(')
            }

            // Dodaj przyciski nawiasów w XML i tutaj:
            fun appendOpenParenthesis() {
                if (currentExpression.isEmpty() || isLastCharOperator() || currentExpression.last() == '(') {
                    currentExpression.append("(")
                    updateDisplay()
                }
            }

            fun appendCloseParenthesis() {
                val openCount = currentExpression.count { it == '(' }
                val closeCount = currentExpression.count { it == ')' }
                if (closeCount < openCount && !isLastCharOperator() && currentExpression.last() != '(') {
                    currentExpression.append(")")
                    updateDisplay()
                }
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
                        val result = calculateWithRPN(expression)
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

            /**
             * Główna funkcja obliczeniowa używająca RPN
             */
            private fun calculateWithRPN(expression: String): Double {
                val normalizedExpr = expression.replace('×', '*').replace('÷', '/')
                val tokens = tokenize(normalizedExpr)
                val postfix = infixToPostfix(tokens)
                return evaluatePostfix(postfix)
            }

            /**
             * Tokenizacja wyrażenia na listę tokenów (liczby i operatory)
             */
            private fun tokenize(expression: String): List<String> {
                val tokens = mutableListOf<String>()
                var currentNum = StringBuilder()

                for (char in expression) {
                    when {
                        char.isDigit() || char == '.' -> currentNum.append(char)
                        char in listOf('+', '-', '*', '/', '^', '(', ')') -> {
                            if (currentNum.isNotEmpty()) {
                                tokens.add(currentNum.toString())
                                currentNum.clear()
                            }
                            tokens.add(char.toString())
                        }
                    }
                }
                if (currentNum.isNotEmpty()) {
                    tokens.add(currentNum.toString())
                }

                return tokens
            }

            /**
             * Algorytm Shunting-yard: konwersja infix → postfix (RPN)
             */
            private fun infixToPostfix(tokens: List<String>): List<String> {
                val output = mutableListOf<String>()
                val operatorStack = Stack<String>()

                for (token in tokens) {
                    when {
                        // Liczba - dodaj do wyjścia
                        token.toDoubleOrNull() != null -> output.add(token)

                        // Nawias otwierający - dodaj na stos
                        token == "(" -> operatorStack.push(token)

                        // Nawias zamykający - zdejmij operatory do nawiasu otwierającego
                        token == ")" -> {
                            while (operatorStack.isNotEmpty() && operatorStack.peek() != "(") {
                                output.add(operatorStack.pop())
                            }
                            if (operatorStack.isNotEmpty()) {
                                operatorStack.pop() // Usuń '('
                            }
                        }

                        // Operator
                        isOperator(token) -> {
                            while (operatorStack.isNotEmpty() &&
                                operatorStack.peek() != "(" &&
                                shouldPopOperator(operatorStack.peek(), token)
                            ) {
                                output.add(operatorStack.pop())
                            }
                            operatorStack.push(token)
                        }
                    }
                }

                // Zdejmij pozostałe operatory
                while (operatorStack.isNotEmpty()) {
                    val op = operatorStack.pop()
                    if (op != "(") output.add(op)
                }

                return output
            }

            /**
             * Obliczenie wyrażenia w notacji postfix (RPN)
             */
            private fun evaluatePostfix(postfix: List<String>): Double {
                val stack = Stack<Double>()

                for (token in postfix) {
                    when {
                        token.toDoubleOrNull() != null -> stack.push(token.toDouble())
                        isOperator(token) -> {
                            if (stack.size < 2) return Double.NaN
                            val b = stack.pop()
                            val a = stack.pop()
                            val result = applyOperator(a, b, token)
                            stack.push(result)
                        }
                    }
                }

                return if (stack.size == 1) stack.pop() else Double.NaN
            }

            private fun isOperator(token: String): Boolean = token in listOf("+", "-", "*", "/", "^")

            private fun getPrecedence(op: String): Int = when (op) {
                "+", "-" -> 1
                "*", "/" -> 2
                "^" -> 3
                else -> 0
            }

            private fun isRightAssociative(op: String): Boolean = op == "^"

            private fun shouldPopOperator(stackOp: String, currentOp: String): Boolean {
                val stackPrec = getPrecedence(stackOp)
                val currentPrec = getPrecedence(currentOp)

                return if (isRightAssociative(currentOp)) {
                    stackPrec > currentPrec
                } else {
                    stackPrec >= currentPrec
                }
            }

            private fun applyOperator(a: Double, b: Double, op: String): Double = when (op) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> if (b != 0.0) a / b else Double.NaN
                "^" -> Math.pow(a, b)
                else -> Double.NaN
            }

            private fun formatResult(result: Double): String {
                return if (result.isNaN()) {
                    "Error"
                } else if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    String.format("%.10f", result).trimEnd('0').trimEnd('.')
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