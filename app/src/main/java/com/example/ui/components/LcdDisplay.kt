package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleUnit
import com.example.viewmodel.CalculatorUiState

val LcdBackground = Color(0xFFD4E6D9) // Classic Casio ClassWiz greenish-grey LCD
val LcdText = Color(0xFF14241B) // Deep black-green LCD pixel text
val LcdAccent = Color(0xFF28543E)
val LcdHighlight = Color(0xFFB8D4C2)

@Composable
fun LcdDisplay(
    uiState: CalculatorUiState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = LcdBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // --- TOP STATUS BAR / ANNUNCIATORS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Shift Annunciator
                    if (uiState.isShiftActive) {
                        Surface(
                            color = Color(0xFFD97706),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier.padding(horizontal = 1.dp)
                        ) {
                            Text(
                                text = "S",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Alpha Annunciator
                    if (uiState.isAlphaActive) {
                        Surface(
                            color = Color(0xFFDC2626),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier.padding(horizontal = 1.dp)
                        ) {
                            Text(
                                text = "A",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Mode Annunciator (DEG / RAD / GRAD)
                    Text(
                        text = uiState.settings.angleUnit.shortName,
                        color = LcdText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Math Input Indicator
                    Text(
                        text = "Math",
                        color = LcdText.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Current App Title Tag
                    Text(
                        text = uiState.currentApp.title.uppercase(),
                        color = LcdAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Solar / Battery Indicator
                    Text(
                        text = "■",
                        color = LcdText.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- EXPRESSION INPUT LINE (With Live Cursor) ---
            val expr = uiState.expression
            val cursorPos = uiState.cursorPosition.coerceIn(0, expr.length)
            val beforeCursor = expr.substring(0, cursorPos)
            val afterCursor = expr.substring(cursorPos)

            val scrollState = rememberScrollState()
            LaunchedEffect(expr.length) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp)
                    .horizontalScroll(scrollState),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (beforeCursor.isEmpty() && afterCursor.isEmpty()) " " else beforeCursor,
                        color = LcdText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )

                    // Blinking cursor
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(20.dp)
                            .background(LcdText.copy(alpha = cursorAlpha))
                    )

                    Text(
                        text = afterCursor,
                        color = LcdText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- RESULT READOUT LINE (Right-aligned) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = uiState.resultText,
                        color = if (uiState.errorMessage != null) Color(0xFFB91C1C) else LcdText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    )

                    if (uiState.exactFractionText != null && uiState.exactFractionText != uiState.resultText) {
                        Text(
                            text = "≈ ${uiState.exactFractionText}",
                            color = LcdAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
