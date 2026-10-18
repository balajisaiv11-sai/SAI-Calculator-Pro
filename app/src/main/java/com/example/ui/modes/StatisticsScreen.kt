package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.OneVarStats
import com.example.engine.StatisticsEngine
import com.example.engine.TwoVarStats

@Composable
fun StatisticsScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Data Table, 1: 1-Var Results, 2: 2-Var Regression
    var xInput by remember { mutableStateOf("") }
    var yInput by remember { mutableStateOf("") }

    val dataX = remember { mutableStateListOf(10.0, 20.0, 30.0, 40.0, 50.0) }
    val dataY = remember { mutableStateListOf(15.0, 28.0, 35.0, 48.0, 62.0) }

    var oneVarStats by remember { mutableStateOf<OneVarStats?>(null) }
    var twoVarStats by remember { mutableStateOf<TwoVarStats?>(null) }

    fun calculate() {
        if (dataX.isNotEmpty()) {
            oneVarStats = StatisticsEngine.calculateOneVar(dataX)
            if (dataX.size == dataY.size && dataX.size >= 2) {
                twoVarStats = StatisticsEngine.calculateTwoVar(dataX, dataY)
            }
        }
    }

    LaunchedEffect(dataX.size, dataY.size) {
        calculate()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Statistics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs: Data vs 1-Var vs 2-Var
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E293B),
            contentColor = Color(0xFF38BDF8)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Data Table") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("1-Var Stats") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("2-Var (y=a+bx)") })
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // Data Entry
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = xInput,
                        onValueChange = { xInput = it },
                        placeholder = { Text("x val") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = yInput,
                        onValueChange = { yInput = it },
                        placeholder = { Text("y val") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    IconButton(
                        onClick = {
                            val x = xInput.toDoubleOrNull()
                            val y = yInput.toDoubleOrNull() ?: 0.0
                            if (x != null) {
                                dataX.add(x)
                                dataY.add(y)
                                xInput = ""
                                yInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF2563EB), RoundedCornerShape(8.dp))
                            .testTag("add_stat_row_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Table Rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(dataX) { idx, xVal ->
                        val yVal = if (idx < dataY.size) dataY[idx] else 0.0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${idx + 1}", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text("x: $xVal", fontSize = 14.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                            Text("y: $yVal", fontSize = 14.sp, color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace)
                            IconButton(onClick = {
                                dataX.removeAt(idx)
                                if (idx < dataY.size) dataY.removeAt(idx)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            1 -> {
                // 1-Var Stats Results
                val s = oneVarStats
                if (s != null) {
                    val metrics = listOf(
                        "Sample Size (n)" to "${s.n}",
                        "Mean (x̄)" to String.format("%.4f", s.mean),
                        "Sum of Values (Σx)" to String.format("%.4f", s.sumX),
                        "Sum of Squares (Σx²)" to String.format("%.4f", s.sumX2),
                        "Sample Std Dev (sx)" to String.format("%.4f", s.sampleStdDev),
                        "Population Std Dev (σx)" to String.format("%.4f", s.popStdDev),
                        "Sample Variance (s²x)" to String.format("%.4f", s.sampleVar),
                        "Population Variance (σ²x)" to String.format("%.4f", s.popVar),
                        "Minimum (minX)" to "${s.minX}",
                        "First Quartile (Q1)" to "${s.q1}",
                        "Median (Med)" to "${s.median}",
                        "Third Quartile (Q3)" to "${s.q3}",
                        "Maximum (maxX)" to "${s.maxX}"
                    )
                    StatMetricList(metrics)
                } else {
                    Text("No data entered.", color = Color(0xFF94A3B8))
                }
            }
            2 -> {
                // 2-Var Regression Results
                val r = twoVarStats
                if (r != null) {
                    val metrics = listOf(
                        "Regression Model" to "y = a + bx",
                        "Intercept (a)" to String.format("%.4f", r.interceptA),
                        "Slope (b)" to String.format("%.4f", r.slopeB),
                        "Correlation (r)" to String.format("%.4f", r.correlationR),
                        "R² (Coefficient of Determination)" to String.format("%.4f", r.rSquared),
                        "Mean of x (x̄)" to String.format("%.4f", r.meanX),
                        "Mean of y (ȳ)" to String.format("%.4f", r.meanY),
                        "Sample Std Dev x (sx)" to String.format("%.4f", r.stdDevX),
                        "Sample Std Dev y (sy)" to String.format("%.4f", r.stdDevY)
                    )
                    StatMetricList(metrics)
                } else {
                    Text("Need at least 2 data pairs for regression.", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
fun StatMetricList(metrics: List<Pair<String, String>>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(metrics.size) { idx ->
            val (label, value) = metrics[idx]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, fontSize = 13.sp, color = Color(0xFFCBD5E1))
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80), fontFamily = FontFamily.Monospace)
            }
        }
    }
}
