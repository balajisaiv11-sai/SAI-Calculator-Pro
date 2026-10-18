package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.engine.*
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val exactFraction: String? = null
)

data class CalculatorUiState(
    val currentApp: CalculatorApp = CalculatorApp.CALCULATE,
    val expression: String = "",
    val cursorPosition: Int = 0,
    val resultText: String = "0",
    val exactFractionText: String? = null,
    val mixedFractionText: String? = null,
    val isShiftActive: Boolean = false,
    val isAlphaActive: Boolean = false,
    val isHypActive: Boolean = false,
    val isComplexMode: Boolean = false,
    val baseNMode: String = "DEC",
    val settings: CalculatorSettings = CalculatorSettings(),
    val variables: Map<String, Double> = mapOf(
        "A" to 0.0, "B" to 0.0, "C" to 0.0,
        "D" to 0.0, "E" to 0.0, "F" to 0.0,
        "x" to 0.0, "y" to 0.0, "z" to 0.0,
        "Ans" to 0.0
    ),
    val fxDefinition: String = "x^2",
    val gxDefinition: String = "2*x + 1",
    val history: List<HistoryItem> = emptyList(),
    val activeSheet: ActiveSheet? = null,
    val errorMessage: String? = null
)

enum class ActiveSheet {
    MENU,
    CATALOG,
    TOOLS,
    SETTINGS,
    FUNCTION_EXPLORER,
    COVERAGE_DASHBOARD,
    VARIABLES,
    HISTORY
}

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val mathEngine: MathEngine
        get() = MathEngine(
            settings = _uiState.value.settings,
            variables = _uiState.value.variables.toMutableMap(),
            ans = _uiState.value.variables["Ans"] ?: 0.0,
            fxDefinition = _uiState.value.fxDefinition,
            gxDefinition = _uiState.value.gxDefinition
        )

    fun onKeyPress(keyId: String) {
        val shift = _uiState.value.isShiftActive
        val alpha = _uiState.value.isAlphaActive

        when (keyId) {
            "SHIFT" -> {
                _uiState.update { it.copy(isShiftActive = !it.isShiftActive, isAlphaActive = false) }
                return
            }
            "ALPHA" -> {
                _uiState.update { it.copy(isAlphaActive = !it.isAlphaActive, isShiftActive = false) }
                return
            }
            "HOME" -> {
                openSheet(ActiveSheet.MENU)
                return
            }
            "SETTINGS" -> {
                openSheet(ActiveSheet.SETTINGS)
                return
            }
            "CATALOG" -> {
                openSheet(ActiveSheet.CATALOG)
                return
            }
            "TOOLS" -> {
                openSheet(ActiveSheet.TOOLS)
                return
            }
            "AC" -> {
                if (shift) {
                    // SHIFT + AC = Power Off / Reset
                    clearAll()
                } else {
                    _uiState.update { it.copy(expression = "", cursorPosition = 0, isShiftActive = false, isAlphaActive = false) }
                }
                return
            }
            "DEL" -> {
                deleteAtCursor()
                _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false) }
                return
            }
            "EXE", "=" -> {
                executeCalculation()
                _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false) }
                return
            }
            "FORMAT", "SD" -> {
                toggleResultFormat()
                return
            }
            "LEFT" -> {
                moveCursor(-1)
                return
            }
            "RIGHT" -> {
                moveCursor(1)
                return
            }
            "UP" -> {
                recallPreviousHistory()
                return
            }
            "DOWN" -> {
                recallNextHistory()
                return
            }
            "RCL" -> {
                openSheet(ActiveSheet.VARIABLES)
                return
            }
        }

        // Token mapping depending on SHIFT or ALPHA
        val token = mapKeyToToken(keyId, shift, alpha)
        if (token != null) {
            insertText(token)
        }

        // Reset modifier state after key execution
        if (shift || alpha) {
            _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false) }
        }
    }

    private fun mapKeyToToken(keyId: String, shift: Boolean, alpha: Boolean): String? {
        if (alpha) {
            return when (keyId) {
                "7" -> "A"
                "8" -> "B"
                "9" -> "C"
                "4" -> "D"
                "5" -> "E"
                "6" -> "F"
                "1" -> "x"
                "2" -> "y"
                "3" -> "z"
                "EXP", "10X" -> "e"
                else -> null
            }
        }

        if (shift) {
            return when (keyId) {
                "sin" -> "asin("
                "cos" -> "acos("
                "tan" -> "atan("
                "ln" -> "exp("
                "log" -> "10^("
                "sqrt" -> "cbrt("
                "pow" -> "^"
                "square" -> "^3"
                "frac" -> " + "
                "diff_int" -> "diff("
                "inv" -> "!"
                "dot" -> "Ran#"
                "EXP", "10X" -> "π"
                "Ans" -> "%"
                "SD" -> "mixed"
                else -> null
            }
        }

        // Primary key mappings
        return when (keyId) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> keyId
            "dot" -> "."
            "add" -> " + "
            "sub" -> " - "
            "mul" -> " × "
            "div" -> " ÷ "
            "neg" -> "(-)"
            "lparen" -> "("
            "rparen" -> ")"
            "comma" -> ", "
            "square" -> "²"
            "pow" -> "^"
            "sqrt" -> "√("
            "frac" -> "/"
            "sin" -> "sin("
            "cos" -> "cos("
            "tan" -> "tan("
            "ln" -> "ln("
            "log" -> "log("
            "abs" -> "abs("
            "inv" -> "^(-1)"
            "EXP", "10X" -> "×10^"
            "Ans" -> "Ans"
            "diff_int" -> "integrate("
            "fx" -> "fx("
            "gx" -> "gx("
            "i" -> "i"
            else -> null
        }
    }

    fun insertText(token: String) {
        val curr = _uiState.value.expression
        val pos = _uiState.value.cursorPosition.coerceIn(0, curr.length)
        val newExpr = curr.substring(0, pos) + token + curr.substring(pos)
        val newPos = pos + token.length
        _uiState.update { it.copy(expression = newExpr, cursorPosition = newPos, errorMessage = null) }
    }

    private fun deleteAtCursor() {
        val curr = _uiState.value.expression
        val pos = _uiState.value.cursorPosition
        if (pos > 0 && curr.isNotEmpty()) {
            val newExpr = curr.substring(0, pos - 1) + curr.substring(pos)
            _uiState.update { it.copy(expression = newExpr, cursorPosition = pos - 1) }
        }
    }

    private fun moveCursor(delta: Int) {
        val curr = _uiState.value.expression
        val newPos = (_uiState.value.cursorPosition + delta).coerceIn(0, curr.length)
        _uiState.update { it.copy(cursorPosition = newPos) }
    }

    fun executeCalculation() {
        val expr = _uiState.value.expression
        if (expr.isBlank()) return

        try {
            val result = mathEngine.evaluate(expr)
            val updatedVars = _uiState.value.variables.toMutableMap()
            updatedVars["Ans"] = result.numericValue

            val historyItem = HistoryItem(
                expression = expr,
                result = result.displayText,
                exactFraction = result.exactFractionText
            )

            _uiState.update {
                it.copy(
                    resultText = result.displayText,
                    exactFractionText = result.exactFractionText,
                    mixedFractionText = result.mixedFractionText,
                    variables = updatedVars,
                    history = listOf(historyItem) + it.history.take(49),
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    resultText = "Syntax ERROR",
                    errorMessage = e.message ?: "Calculation error"
                )
            }
        }
    }

    private fun toggleResultFormat() {
        val state = _uiState.value
        if (state.exactFractionText != null) {
            // Cycle between exact fraction, decimal, and mixed
            val currText = state.resultText
            val nextText = when (currText) {
                state.exactFractionText -> mathEngine.formatResult(state.variables["Ans"] ?: 0.0)
                state.mixedFractionText -> state.exactFractionText
                else -> state.mixedFractionText ?: state.exactFractionText
            }
            _uiState.update { it.copy(resultText = nextText ?: it.resultText) }
        }
    }

    private fun recallPreviousHistory() {
        val hist = _uiState.value.history
        if (hist.isNotEmpty()) {
            val item = hist.first()
            _uiState.update { it.copy(expression = item.expression, cursorPosition = item.expression.length) }
        }
    }

    private fun recallNextHistory() {
        _uiState.update { it.copy(expression = "", cursorPosition = 0) }
    }

    fun setAppMode(app: CalculatorApp) {
        _uiState.update {
            it.copy(
                currentApp = app,
                isComplexMode = (app == CalculatorApp.COMPLEX),
                activeSheet = null
            )
        }
    }

    fun updateSettings(newSettings: CalculatorSettings) {
        _uiState.update { it.copy(settings = newSettings) }
        // Re-format active result if present
        if (_uiState.value.expression.isNotBlank()) {
            executeCalculation()
        }
    }

    fun setVariable(name: String, value: Double) {
        val map = _uiState.value.variables.toMutableMap()
        map[name] = value
        _uiState.update { it.copy(variables = map) }
    }

    fun clearAll() {
        _uiState.update {
            it.copy(
                expression = "",
                cursorPosition = 0,
                resultText = "0",
                exactFractionText = null,
                mixedFractionText = null,
                isShiftActive = false,
                isAlphaActive = false,
                errorMessage = null
            )
        }
    }

    fun openSheet(sheet: ActiveSheet) {
        _uiState.update { it.copy(activeSheet = sheet) }
    }

    fun closeSheet() {
        _uiState.update { it.copy(activeSheet = null) }
    }
}
