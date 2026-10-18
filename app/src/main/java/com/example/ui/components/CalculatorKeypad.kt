package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CalculatorUiState

enum class KeyStyle {
    NUMBER,
    FUNCTION,
    MODIFIER_SHIFT,
    MODIFIER_ALPHA,
    DELETE,
    CLEAR,
    EXECUTE,
    SYSTEM
}

@Composable
fun CalculatorKey(
    label: String,
    shiftLabel: String? = null,
    alphaLabel: String? = null,
    style: KeyStyle = KeyStyle.NUMBER,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = when (style) {
        KeyStyle.NUMBER -> Color(0xFF1E293B) // Dark Slate
        KeyStyle.FUNCTION -> Color(0xFF334155) // Slate
        KeyStyle.MODIFIER_SHIFT -> Color(0xFFB45309) // Amber Gold
        KeyStyle.MODIFIER_ALPHA -> Color(0xFFBE123C) // Crimson Red
        KeyStyle.DELETE -> Color(0xFF475569) // Greyish Slate
        KeyStyle.CLEAR -> Color(0xFFDC2626) // Vivid Red
        KeyStyle.EXECUTE -> Color(0xFF2563EB) // Bright Blue
        KeyStyle.SYSTEM -> Color(0xFF0F172A) // Deep Navy
    }

    val textColor = Color.White

    Column(
        modifier = modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SECONDARY PRINTED LABELS (SHIFT / ALPHA) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shiftLabel ?: "",
                color = Color(0xFFFBBF24), // Gold for SHIFT
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = alphaLabel ?: "",
                color = Color(0xFFF87171), // Red for ALPHA
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        // --- PHYSICAL KEY BUTTON BODY ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .testTag(testTag)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = if (label.length > 3) 13.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalculatorKeypad(
    uiState: CalculatorUiState,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // --- ROW 1: SYSTEM CONTROLS (SHIFT, ALPHA, HOME, CATALOG, TOOLS, SETTINGS) ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey(
                label = "SHIFT",
                style = if (uiState.isShiftActive) KeyStyle.CLEAR else KeyStyle.MODIFIER_SHIFT,
                testTag = "key_shift",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("SHIFT") }

            CalculatorKey(
                label = "ALPHA",
                style = if (uiState.isAlphaActive) KeyStyle.CLEAR else KeyStyle.MODIFIER_ALPHA,
                testTag = "key_alpha",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("ALPHA") }

            CalculatorKey(
                label = "HOME",
                style = KeyStyle.SYSTEM,
                testTag = "key_home",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("HOME") }

            CalculatorKey(
                label = "CATALOG",
                style = KeyStyle.SYSTEM,
                testTag = "key_catalog",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("CATALOG") }

            CalculatorKey(
                label = "TOOLS",
                style = KeyStyle.SYSTEM,
                testTag = "key_tools",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("TOOLS") }

            CalculatorKey(
                label = "⚙",
                style = KeyStyle.SYSTEM,
                testTag = "key_settings",
                modifier = Modifier.weight(1f)
            ) { onKeyClick("SETTINGS") }
        }

        // --- ROW 2: SCIENTIFIC FUNCTIONS (Abs, log, ln, sin, cos, tan) ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("Abs", null, null, KeyStyle.FUNCTION, "key_abs", Modifier.weight(1f)) { onKeyClick("abs") }
            CalculatorKey("log", "10ˣ", null, KeyStyle.FUNCTION, "key_log", Modifier.weight(1f)) { onKeyClick("log") }
            CalculatorKey("ln", "eˣ", null, KeyStyle.FUNCTION, "key_ln", Modifier.weight(1f)) { onKeyClick("ln") }
            CalculatorKey("sin", "sin⁻¹", null, KeyStyle.FUNCTION, "key_sin", Modifier.weight(1f)) { onKeyClick("sin") }
            CalculatorKey("cos", "cos⁻¹", null, KeyStyle.FUNCTION, "key_cos", Modifier.weight(1f)) { onKeyClick("cos") }
            CalculatorKey("tan", "tan⁻¹", null, KeyStyle.FUNCTION, "key_tan", Modifier.weight(1f)) { onKeyClick("tan") }
        }

        // --- ROW 3: POWERS, ROOTS & CALCULUS (x², √, xʸ, f(x), g(x), ∫) ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("x²", "x³", null, KeyStyle.FUNCTION, "key_square", Modifier.weight(1f)) { onKeyClick("square") }
            CalculatorKey("√", "³√", null, KeyStyle.FUNCTION, "key_sqrt", Modifier.weight(1f)) { onKeyClick("sqrt") }
            CalculatorKey("xʸ", "ʸ√x", null, KeyStyle.FUNCTION, "key_power", Modifier.weight(1f)) { onKeyClick("pow") }
            CalculatorKey("f(x)", null, null, KeyStyle.FUNCTION, "key_fx", Modifier.weight(1f)) { onKeyClick("fx") }
            CalculatorKey("g(x)", null, null, KeyStyle.FUNCTION, "key_gx", Modifier.weight(1f)) { onKeyClick("gx") }
            CalculatorKey("∫", "d/dx", null, KeyStyle.FUNCTION, "key_integral", Modifier.weight(1f)) { onKeyClick("diff_int") }
        }

        // --- ROW 4: MEMORY, ENG & EDITING (RCL, ENG, ◀, ▶, DEL, AC) ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("RCL", "STO", null, KeyStyle.FUNCTION, "key_rcl", Modifier.weight(1f)) { onKeyClick("RCL") }
            CalculatorKey("ENG", null, "i", KeyStyle.FUNCTION, "key_eng", Modifier.weight(1f)) { onKeyClick("i") }
            CalculatorKey("◀", null, null, KeyStyle.FUNCTION, "key_left", Modifier.weight(1f)) { onKeyClick("LEFT") }
            CalculatorKey("▶", null, null, KeyStyle.FUNCTION, "key_right", Modifier.weight(1f)) { onKeyClick("RIGHT") }
            CalculatorKey("DEL", "INS", null, KeyStyle.DELETE, "key_del", Modifier.weight(1f)) { onKeyClick("DEL") }
            CalculatorKey("AC", "OFF", null, KeyStyle.CLEAR, "key_ac", Modifier.weight(1f)) { onKeyClick("AC") }
        }

        // --- ROW 5: NUMBERS 7, 8, 9, PARENS, S⇔D ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("7", null, "A", KeyStyle.NUMBER, "key_7", Modifier.weight(1f)) { onKeyClick("7") }
            CalculatorKey("8", null, "B", KeyStyle.NUMBER, "key_8", Modifier.weight(1f)) { onKeyClick("8") }
            CalculatorKey("9", null, "C", KeyStyle.NUMBER, "key_9", Modifier.weight(1f)) { onKeyClick("9") }
            CalculatorKey("(", null, null, KeyStyle.FUNCTION, "key_lparen", Modifier.weight(1f)) { onKeyClick("lparen") }
            CalculatorKey(")", null, null, KeyStyle.FUNCTION, "key_rparen", Modifier.weight(1f)) { onKeyClick("rparen") }
            CalculatorKey("S⇔D", "a b/c", null, KeyStyle.FUNCTION, "key_format", Modifier.weight(1f)) { onKeyClick("SD") }
        }

        // --- ROW 6: NUMBERS 4, 5, 6, MULTIPLY, DIVIDE ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("4", null, "D", KeyStyle.NUMBER, "key_4", Modifier.weight(1f)) { onKeyClick("4") }
            CalculatorKey("5", null, "E", KeyStyle.NUMBER, "key_5", Modifier.weight(1f)) { onKeyClick("5") }
            CalculatorKey("6", null, "F", KeyStyle.NUMBER, "key_6", Modifier.weight(1f)) { onKeyClick("6") }
            CalculatorKey("×", "nPr", null, KeyStyle.FUNCTION, "key_mul", Modifier.weight(1.5f)) { onKeyClick("mul") }
            CalculatorKey("÷", "nCr", null, KeyStyle.FUNCTION, "key_div", Modifier.weight(1.5f)) { onKeyClick("div") }
        }

        // --- ROW 7: NUMBERS 1, 2, 3, PLUS, MINUS ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("1", null, "x", KeyStyle.NUMBER, "key_1", Modifier.weight(1f)) { onKeyClick("1") }
            CalculatorKey("2", null, "y", KeyStyle.NUMBER, "key_2", Modifier.weight(1f)) { onKeyClick("2") }
            CalculatorKey("3", null, "z", KeyStyle.NUMBER, "key_3", Modifier.weight(1f)) { onKeyClick("3") }
            CalculatorKey("+", "Pol", null, KeyStyle.FUNCTION, "key_add", Modifier.weight(1.5f)) { onKeyClick("add") }
            CalculatorKey("−", "Rec", null, KeyStyle.FUNCTION, "key_sub", Modifier.weight(1.5f)) { onKeyClick("sub") }
        }

        // --- ROW 8: NUMBER 0, DECIMAL, POW10, ANS, EXE ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CalculatorKey("0", null, null, KeyStyle.NUMBER, "key_0", Modifier.weight(1f)) { onKeyClick("0") }
            CalculatorKey("•", "Ran#", null, KeyStyle.NUMBER, "key_dot", Modifier.weight(1f)) { onKeyClick("dot") }
            CalculatorKey("×10ˣ", "π", "e", KeyStyle.FUNCTION, "key_exp10", Modifier.weight(1.2f)) { onKeyClick("10X") }
            CalculatorKey("Ans", "%", null, KeyStyle.FUNCTION, "key_ans", Modifier.weight(1.2f)) { onKeyClick("Ans") }
            CalculatorKey("EXE", null, null, KeyStyle.EXECUTE, "key_exe", Modifier.weight(1.6f)) { onKeyClick("EXE") }
        }
    }
}
