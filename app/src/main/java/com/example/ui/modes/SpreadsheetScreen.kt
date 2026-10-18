package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SpreadsheetEngine

@Composable
fun SpreadsheetScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = remember { SpreadsheetEngine() }
    var selectedCell by remember { mutableStateOf("A1") }
    var cellEditInput by remember { mutableStateOf(engine.rawCells["A1"] ?: "") }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(selectedCell) {
        cellEditInput = engine.rawCells[selectedCell] ?: ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Spreadsheet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Formula Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF2563EB),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = selectedCell,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontFamily = FontFamily.Monospace
                )
            }

            OutlinedTextField(
                value = cellEditInput,
                onValueChange = { cellEditInput = it },
                placeholder = { Text("Value or =Formula (e.g. =SUM(A1:A3))", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("cell_formula_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    engine.setCell(selectedCell, cellEditInput)
                    refreshTrigger++
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("Set")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Spreadsheet Grid (5 Columns x 10 Rows)
        val hScroll = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(hScroll)
        ) {
            Column {
                // Header row
                Row {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(30.dp)
                            .background(Color(0xFF1E293B))
                            .border(0.5.dp, Color(0xFF334155))
                    )
                    engine.cols.forEach { c ->
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(30.dp)
                                .background(Color(0xFF1E293B))
                                .border(0.5.dp, Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Rows 1 to 10
                LazyColumn {
                    items(engine.rowCount) { rowIdx ->
                        val r = rowIdx + 1
                        Row {
                            // Row number
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(34.dp)
                                    .background(Color(0xFF1E293B))
                                    .border(0.5.dp, Color(0xFF334155)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$r", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            }

                            // Columns A..E
                            engine.cols.forEach { c ->
                                val ref = "$c$r"
                                val isSelected = ref == selectedCell
                                val value = engine.computedValues[ref]
                                val displayVal = if (value != null) {
                                    if (value % 1.0 == 0.0) value.toLong().toString() else String.format("%.2f", value)
                                } else ""

                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(34.dp)
                                        .background(if (isSelected) Color(0xFF2563EB).copy(alpha = 0.3f) else Color(0xFF0F172A))
                                        .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                        .clickable {
                                            selectedCell = ref
                                        }
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = displayVal,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.End,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
