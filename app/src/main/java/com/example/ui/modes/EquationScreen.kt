package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.engine.EquationSolver

enum class EquationType {
    SIMULTANEOUS_2,
    SIMULTANEOUS_3,
    POLYNOMIAL_2,
    POLYNOMIAL_3
}

@Composable
fun EquationScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(EquationType.SIMULTANEOUS_2) }

    // Simultaneous 2 inputs
    var a1 by remember { mutableStateOf("1") }
    var b1 by remember { mutableStateOf("1") }
    var c1 by remember { mutableStateOf("5") }
    var a2 by remember { mutableStateOf("2") }
    var b2 by remember { mutableStateOf("-1") }
    var c2 by remember { mutableStateOf("1") }

    // Polynomial inputs
    var pA by remember { mutableStateOf("1") }
    var pB by remember { mutableStateOf("-5") }
    var pC by remember { mutableStateOf("6") }
    var pD by remember { mutableStateOf("0") }

    var solutionText by remember { mutableStateOf<String?>(null) }

    fun solve() {
        try {
            when (selectedType) {
                EquationType.SIMULTANEOUS_2 -> {
                    val coeffs = arrayOf(
                        doubleArrayOf(a1.toDouble(), b1.toDouble()),
                        doubleArrayOf(a2.toDouble(), b2.toDouble())
                    )
                    val consts = doubleArrayOf(c1.toDouble(), c2.toDouble())
                    val res = EquationSolver.solveLinear(coeffs, consts)
                    solutionText = when (res) {
                        is EquationSolver.LinearResult.Unique -> "x = ${res.solutions[0]}\ny = ${res.solutions[1]}"
                        is EquationSolver.LinearResult.InfiniteSolutions -> "Infinitely many solutions"
                        is EquationSolver.LinearResult.NoSolution -> "No solution"
                    }
                }
                EquationType.POLYNOMIAL_2 -> {
                    val res = EquationSolver.solveQuadratic(pA.toDouble(), pB.toDouble(), pC.toDouble())
                    val minMax = if (res.isMinimum) "Min" else "Max"
                    solutionText = "x₁ = ${res.root1.toRectangularString(4)}\n" +
                            "x₂ = ${res.root2.toRectangularString(4)}\n" +
                            "Vertex ($minMax): x = ${res.vertexX}, y = ${res.vertexY}"
                }
                EquationType.POLYNOMIAL_3 -> {
                    val res = EquationSolver.solveCubic(pA.toDouble(), pB.toDouble(), pC.toDouble(), pD.toDouble())
                    solutionText = res.roots.mapIndexed { idx, r -> "x${idx + 1} = ${r.toRectangularString(4)}" }.joinToString("\n")
                }
                else -> {}
            }
        } catch (e: Exception) {
            solutionText = "Error: ${e.message}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Equation Solver", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Equation Type Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = { selectedType = EquationType.SIMULTANEOUS_2; solutionText = null },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == EquationType.SIMULTANEOUS_2) Color(0xFF2563EB) else Color(0xFF1E293B)),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) { Text("Linear (2-Var)", fontSize = 11.sp) }

            Button(
                onClick = { selectedType = EquationType.POLYNOMIAL_2; solutionText = null },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == EquationType.POLYNOMIAL_2) Color(0xFF2563EB) else Color(0xFF1E293B)),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) { Text("Quadratic (ax²)", fontSize = 11.sp) }

            Button(
                onClick = { selectedType = EquationType.POLYNOMIAL_3; solutionText = null },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedType == EquationType.POLYNOMIAL_3) Color(0xFF2563EB) else Color(0xFF1E293B)),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) { Text("Cubic (ax³)", fontSize = 11.sp) }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Inputs
        when (selectedType) {
            EquationType.SIMULTANEOUS_2 -> {
                Text("a₁x + b₁y = c₁", fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoeffField("a₁", a1, Modifier.weight(1f)) { a1 = it }
                    CoeffField("b₁", b1, Modifier.weight(1f)) { b1 = it }
                    CoeffField("c₁", c1, Modifier.weight(1f)) { c1 = it }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("a₂x + b₂y = c₂", fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoeffField("a₂", a2, Modifier.weight(1f)) { a2 = it }
                    CoeffField("b₂", b2, Modifier.weight(1f)) { b2 = it }
                    CoeffField("c₂", c2, Modifier.weight(1f)) { c2 = it }
                }
            }
            EquationType.POLYNOMIAL_2 -> {
                Text("ax² + bx + c = 0", fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoeffField("a", pA, Modifier.weight(1f)) { pA = it }
                    CoeffField("b", pB, Modifier.weight(1f)) { pB = it }
                    CoeffField("c", pC, Modifier.weight(1f)) { pC = it }
                }
            }
            EquationType.POLYNOMIAL_3 -> {
                Text("ax³ + bx² + cx + d = 0", fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CoeffField("a", pA, Modifier.weight(1f)) { pA = it }
                    CoeffField("b", pB, Modifier.weight(1f)) { pB = it }
                    CoeffField("c", pC, Modifier.weight(1f)) { pC = it }
                    CoeffField("d", pD, Modifier.weight(1f)) { pD = it }
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { solve() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("solve_equation_button"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Solve Equation", fontWeight = FontWeight.Bold)
        }

        if (solutionText != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Solution:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = solutionText!!,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun CoeffField(label: String, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF38BDF8),
            unfocusedBorderColor = Color(0xFF334155)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
