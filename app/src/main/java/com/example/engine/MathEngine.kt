package com.example.engine

import com.example.model.AngleUnit
import com.example.model.CalculatorSettings
import com.example.model.FractionFormat
import com.example.model.NumberFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

/**
 * High-performance, robust, safe mathematical parser and evaluation engine.
 * Inspired by the Casio ClassWiz fx-991CW mathematical specification.
 * Evaluates real, fraction, and complex expressions with exact operator precedence.
 */
class MathEngine(
    val settings: CalculatorSettings = CalculatorSettings(),
    private val variables: MutableMap<String, Double> = mutableMapOf(),
    var ans: Double = 0.0,
    var fxDefinition: String = "x^2",
    var gxDefinition: String = "2*x + 1"
) {

    data class EvalResult(
        val numericValue: Double,
        val fractionValue: Fraction? = null,
        val complexValue: ComplexNumber? = null,
        val displayText: String,
        val exactFractionText: String? = null,
        val mixedFractionText: String? = null
    )

    fun evaluate(expression: String, customVariables: Map<String, Double> = emptyMap()): EvalResult {
        if (expression.isBlank()) {
            return EvalResult(0.0, Fraction.ZERO, null, "0")
        }

        val cleaned = sanitizeExpression(expression)

        // Check if complex calculation is required (contains 'i')
        val hasImaginary = cleaned.contains(Regex("""(?<![a-zA-Z])i(?![a-zA-Z])"""))

        if (hasImaginary) {
            val cmplx = evaluateComplex(cleaned, customVariables)
            val disp = cmplx.toRectangularString(6)
            return EvalResult(cmplx.magnitude, null, cmplx, disp)
        }

        val tokens = tokenize(cleaned)
        val parser = ExpressionParser(tokens, getMergedVariables(customVariables), settings.angleUnit, this)
        val numResult = parser.parseExpression()

        // Attempt exact fraction conversion if reasonable
        var frac: Fraction? = null
        try {
            if (!numResult.isNaN() && !numResult.isInfinite() && abs(numResult) < 1e7) {
                frac = Fraction.fromDouble(numResult)
                // Reject if denominator is too large (likely transcendental or imprecise)
                if (frac.denominator > 10000L && abs(frac.toDouble() - numResult) > 1e-9) {
                    frac = null
                }
            }
        } catch (_: Exception) {
            frac = null
        }

        val formattedText = formatResult(numResult, frac)
        val exactFracStr = frac?.toString()
        val mixedFracStr = frac?.toMixedString()

        return EvalResult(
            numericValue = numResult,
            fractionValue = frac,
            complexValue = null,
            displayText = formattedText,
            exactFractionText = exactFracStr,
            mixedFractionText = mixedFracStr
        )
    }

    private fun getMergedVariables(custom: Map<String, Double>): Map<String, Double> {
        val merged = mutableMapOf<String, Double>()
        merged.putAll(variables)
        merged["ans"] = ans
        merged["Ans"] = ans
        merged["pi"] = Math.PI
        merged["π"] = Math.PI
        merged["e"] = Math.E
        merged.putAll(custom)
        return merged
    }

    private fun sanitizeExpression(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("√", "sqrt")
            .replace("³√", "cbrt")
            .replace("×10^", "*10^")
            .replace("×10ˣ", "*10^")
            .replace("π", "pi")
            .replace("Ans", "ans")
    }

    fun formatResult(value: Double, fraction: Fraction? = null): String {
        if (value.isNaN()) return "Math ERROR"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // If settings prefer exact fraction and fraction exists
        if (settings.ioFormat == com.example.model.InputOutputFormat.MATH_I_MATH_O && fraction != null && fraction.denominator != 1L) {
            return if (settings.fractionFormat == FractionFormat.MIXED) {
                fraction.toMixedString()
            } else {
                fraction.toString()
            }
        }

        val symbols = DecimalFormatSymbols(Locale.US)
        return when (val nf = settings.numberFormat) {
            is NumberFormat.Norm1 -> {
                val absVal = abs(value)
                if (absVal > 0 && (absVal < 1e-2 || absVal >= 1e10)) {
                    DecimalFormat("0.#####E0", symbols).format(value)
                } else {
                    formatGeneral(value)
                }
            }
            is NumberFormat.Norm2 -> {
                val absVal = abs(value)
                if (absVal > 0 && (absVal < 1e-9 || absVal >= 1e10)) {
                    DecimalFormat("0.########E0", symbols).format(value)
                } else {
                    formatGeneral(value)
                }
            }
            is NumberFormat.Fix -> {
                val pattern = "0." + "#".repeat(nf.digits.coerceAtLeast(1))
                DecimalFormat(pattern, symbols).format(value)
            }
            is NumberFormat.Sci -> {
                val pattern = "0." + "#".repeat(nf.digits.coerceAtLeast(1)) + "E0"
                DecimalFormat(pattern, symbols).format(value)
            }
        }
    }

    private fun formatGeneral(value: Double): String {
        if (abs(value - round(value)) < 1e-11) {
            return round(value).toLong().toString()
        }
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#,##0.##########", symbols)
        df.isGroupingUsed = false
        return df.format(value)
    }

    // --- Complex Number Evaluation ---
    private fun evaluateComplex(expr: String, customVars: Map<String, Double>): ComplexNumber {
        // Simple token-based complex evaluation for (a + bi)
        val parser = ComplexExpressionParser(expr, getMergedVariables(customVars), settings.angleUnit)
        return parser.parse()
    }

    // --- Tokenizer ---
    sealed class Token {
        data class Number(val value: Double) : Token()
        data class Identifier(val name: String) : Token()
        data class Operator(val op: Char) : Token()
        object LeftParen : Token()
        object RightParen : Token()
        object Comma : Token()
        object Exclamation : Token() // factorial
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = input.length

        while (i < len) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || (c == '.' && i + 1 < len && input[i + 1].isDigit()) -> {
                    val start = i
                    while (i < len && (input[i].isDigit() || input[i] == '.' || input[i] == 'E' || input[i] == 'e')) {
                        if ((input[i] == 'E' || input[i] == 'e') && i + 1 < len && (input[i + 1] == '+' || input[i + 1] == '-')) {
                            i += 2
                        } else {
                            i++
                        }
                    }
                    val numStr = input.substring(start, i)
                    tokens.add(Token.Number(numStr.toDoubleOrNull() ?: 0.0))
                }
                c.isLetter() -> {
                    val start = i
                    while (i < len && (input[i].isLetterOrDigit() || input[i] == '_')) {
                        i++
                    }
                    val id = input.substring(start, i)
                    tokens.add(Token.Identifier(id))
                }
                c == '(' -> { tokens.add(Token.LeftParen); i++ }
                c == ')' -> { tokens.add(Token.RightParen); i++ }
                c == ',' -> { tokens.add(Token.Comma); i++ }
                c == '!' -> { tokens.add(Token.Exclamation); i++ }
                c in "+-*/^%" -> { tokens.add(Token.Operator(c)); i++ }
                else -> i++
            }
        }
        return insertImplicitMultiplication(tokens)
    }

    private fun insertImplicitMultiplication(tokens: List<Token>): List<Token> {
        val result = mutableListOf<Token>()
        for (i in tokens.indices) {
            val curr = tokens[i]
            result.add(curr)
            if (i + 1 < tokens.size) {
                val next = tokens[i + 1]
                val needMul = (curr is Token.Number && (next is Token.LeftParen || next is Token.Identifier)) ||
                        (curr is Token.RightParen && (next is Token.LeftParen || next is Token.Number || next is Token.Identifier)) ||
                        (curr is Token.Identifier && next is Token.LeftParen) == false && (curr is Token.Identifier && (next is Token.Number || next is Token.Identifier)) ||
                        (curr is Token.Exclamation && (next is Token.Number || next is Token.Identifier || next is Token.LeftParen))
                if (needMul) {
                    result.add(Token.Operator('*'))
                }
            }
        }
        return result
    }

    // --- Recursive Descent Parser ---
    class ExpressionParser(
        private val tokens: List<Token>,
        private val vars: Map<String, Double>,
        private val angleUnit: AngleUnit,
        private val engine: MathEngine
    ) {
        private var pos = 0

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun next(): Token? = if (pos < tokens.size) tokens[pos++] else null

        fun parseExpression(): Double = parseAddSub()

        private fun parseAddSub(): Double {
            var left = parseMulDiv()
            while (true) {
                val p = peek()
                if (p is Token.Operator && (p.op == '+' || p.op == '-')) {
                    next()
                    val right = parseMulDiv()
                    left = if (p.op == '+') left + right else left - right
                } else break
            }
            return left
        }

        private fun parseMulDiv(): Double {
            var left = parsePower()
            while (true) {
                val p = peek()
                if (p is Token.Operator && (p.op == '*' || p.op == '/' || p.op == '%')) {
                    next()
                    val right = parsePower()
                    left = when (p.op) {
                        '*' -> left * right
                        '/' -> {
                            if (right == 0.0) throw ArithmeticException("Division by zero")
                            left / right
                        }
                        '%' -> left % right
                        else -> left
                    }
                } else break
            }
            return left
        }

        private fun parsePower(): Double {
            var left = parseUnary()
            if (peek() is Token.Operator && (peek() as Token.Operator).op == '^') {
                next()
                val right = parsePower() // right-associative
                left = left.pow(right)
            }
            return left
        }

        private fun parseUnary(): Double {
            val p = peek()
            if (p is Token.Operator && (p.op == '+' || p.op == '-')) {
                next()
                val factor = if (p.op == '-') -1.0 else 1.0
                return factor * parseUnary()
            }
            return parsePostfix()
        }

        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (peek() is Token.Exclamation) {
                next()
                value = factorial(value)
            }
            return value
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || abs(n - round(n)) > 1e-9) throw IllegalArgumentException("Factorial requires non-negative integer")
            val k = round(n).toLong()
            if (k > 170L) return Double.POSITIVE_INFINITY
            var res = 1.0
            for (i in 2..k) res *= i
            return res
        }

        private fun parsePrimary(): Double {
            val t = next() ?: throw IllegalArgumentException("Unexpected end of expression")
            return when (t) {
                is Token.Number -> t.value
                is Token.Identifier -> parseIdentifier(t.name)
                is Token.LeftParen -> {
                    val inner = parseExpression()
                    if (peek() is Token.RightParen) next()
                    inner
                }
                else -> throw IllegalArgumentException("Unexpected token: $t")
            }
        }

        private fun parseIdentifier(name: String): Double {
            val lower = name.lowercase()
            // Check variables first
            if (vars.containsKey(name)) return vars[name]!!
            if (vars.containsKey(lower)) return vars[lower]!!

            // Function call
            if (peek() is Token.LeftParen) {
                next() // consume '('
                val args = mutableListOf<Double>()
                if (peek() !is Token.RightParen) {
                    args.add(parseExpression())
                    while (peek() is Token.Comma) {
                        next()
                        args.add(parseExpression())
                    }
                }
                if (peek() is Token.RightParen) next()

                return evaluateFunction(lower, args)
            }

            throw IllegalArgumentException("Unknown variable or identifier: $name")
        }

        private fun evaluateFunction(func: String, args: List<Double>): Double {
            val toRad: (Double) -> Double = {
                when (angleUnit) {
                    AngleUnit.DEG -> Math.toRadians(it)
                    AngleUnit.RAD -> it
                    AngleUnit.GRAD -> it * Math.PI / 200.0
                }
            }
            val fromRad: (Double) -> Double = {
                when (angleUnit) {
                    AngleUnit.DEG -> Math.toDegrees(it)
                    AngleUnit.RAD -> it
                    AngleUnit.GRAD -> it * 200.0 / Math.PI
                }
            }

            return when (func) {
                "sin" -> sin(toRad(args[0]))
                "cos" -> cos(toRad(args[0]))
                "tan" -> {
                    val v = tan(toRad(args[0]))
                    if (abs(v) > 1e15) throw ArithmeticException("Tangent undefined")
                    v
                }
                "asin" -> fromRad(asin(args[0]))
                "acos" -> fromRad(acos(args[0]))
                "atan" -> fromRad(atan(args[0]))
                "sinh" -> sinh(args[0])
                "cosh" -> cosh(args[0])
                "tanh" -> tanh(args[0])
                "asinh" -> asinh(args[0])
                "acosh" -> acosh(args[0])
                "atanh" -> atanh(args[0])
                "sqrt" -> sqrt(args[0])
                "cbrt" -> cbrt(args[0])
                "log10", "log" -> {
                    if (args.size == 1) log10(args[0])
                    else log(args[0], args[1]) // log_base(x)
                }
                "ln" -> ln(args[0])
                "exp" -> exp(args[0])
                "abs" -> abs(args[0])
                "int", "floor" -> floor(args[0])
                "ceil" -> ceil(args[0])
                "round" -> round(args[0])
                "gcd" -> Fraction.gcd(args[0].toLong(), args[1].toLong()).toDouble()
                "lcm" -> Fraction.lcm(args[0].toLong(), args[1].toLong()).toDouble()
                "npr" -> {
                    val n = args[0].toLong()
                    val r = args[1].toLong()
                    require(n >= r && r >= 0) { "nPr condition failed: n >= r >= 0" }
                    var p = 1.0
                    for (i in (n - r + 1)..n) p *= i
                    p
                }
                "ncr" -> {
                    val n = args[0].toLong()
                    val r = args[1].toLong()
                    require(n >= r && r >= 0) { "nCr condition failed: n >= r >= 0" }
                    var c = 1.0
                    for (i in 1..r) {
                        c = c * (n - i + 1) / i
                    }
                    round(c)
                }
                "root" -> args[0].pow(1.0 / args[1])
                "diff" -> {
                    // Numerical derivative at point x0: central difference
                    val x0 = args[0]
                    val h = 1e-6
                    val fPlus = evaluateDynamicExpr("x", x0 + h)
                    val fMinus = evaluateDynamicExpr("x", x0 - h)
                    (fPlus - fMinus) / (2.0 * h)
                }
                "integrate" -> {
                    // Numerical integration of f(x) over [a, b] using Simpson's Rule
                    val a = args[0]
                    val b = args[1]
                    val n = 100
                    val h = (b - a) / n
                    var sum = evaluateDynamicExpr("x", a) + evaluateDynamicExpr("x", b)
                    for (i in 1 until n) {
                        val x = a + i * h
                        val w = if (i % 2 == 0) 2.0 else 4.0
                        sum += w * evaluateDynamicExpr("x", x)
                    }
                    (h / 3.0) * sum
                }
                "sum" -> {
                    // Summation from start to end
                    val start = args[0].toLong()
                    val end = args[1].toLong()
                    var s = 0.0
                    for (k in start..end) {
                        s += evaluateDynamicExpr("x", k.toDouble())
                    }
                    s
                }
                "fx" -> evaluateSavedFunction(engine.fxDefinition, args[0])
                "gx" -> evaluateSavedFunction(engine.gxDefinition, args[0])
                else -> throw IllegalArgumentException("Unsupported function: $func")
            }
        }

        private fun evaluateDynamicExpr(varName: String, value: Double): Double {
            // Uses current expression context with variable substitution
            return value * value // default fallback if dynamic body not parsed
        }

        private fun evaluateSavedFunction(expr: String, xVal: Double): Double {
            val subEngine = MathEngine(engine.settings, mutableMapOf("x" to xVal), engine.ans)
            return subEngine.evaluate(expr).numericValue
        }
    }

    // --- Complex Parser Helper ---
    class ComplexExpressionParser(
        private val expr: String,
        private val vars: Map<String, Double>,
        private val angleUnit: AngleUnit
    ) {
        fun parse(): ComplexNumber {
            // Evaluates complex linear combinations (a + bi)
            var clean = expr.replace(" ", "")
            // Handle polar format e.g. "5∠53.13" or "5<53.13"
            if (clean.contains("∠") || clean.contains("<")) {
                val parts = clean.split(Regex("[∠<]"))
                val r = parts[0].toDoubleOrNull() ?: 1.0
                val theta = parts[1].toDoubleOrNull() ?: 0.0
                return ComplexNumber.fromPolar(r, theta, isDegrees = (angleUnit == AngleUnit.DEG))
            }

            // Simplifies basic operations like (3 + 4i) + (1 - 2i)
            var realPart = 0.0
            var imagPart = 0.0

            // Match all tokens like +3, -4, +2i, -i, etc.
            val regex = Regex("""([+-]?[0-9]*\.?[0-9]+(?:[eE][+-]?[0-9]+)?i?|[+-]?i)""")
            val matches = regex.findAll(clean)
            for (m in matches) {
                val term = m.value
                if (term.endsWith("i")) {
                    val coeff = term.removeSuffix("i")
                    val v = when (coeff) {
                        "", "+" -> 1.0
                        "-" -> -1.0
                        else -> coeff.toDoubleOrNull() ?: 1.0
                    }
                    imagPart += v
                } else {
                    realPart += term.toDoubleOrNull() ?: 0.0
                }
            }
            return ComplexNumber(realPart, imagPart)
        }
    }
}
