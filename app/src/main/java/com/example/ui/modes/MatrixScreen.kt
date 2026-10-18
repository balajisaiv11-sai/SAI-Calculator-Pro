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
import com.example.engine.MatrixEngine

@Composable
fun MatrixScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dim by remember { mutableStateOf(2) } // 2x2 or 3x3

    // Matrix elements stored as 3x3 string grid
    var m00 by remember { mutableStateOf("1") }
    var m01 by remember { mutableStateOf("2") }
    var m02 by remember { mutableStateOf("0") }

    var m10 by remember { mutableStateOf("3") }
    var m11 by remember { mutableStateOf("4") }
    var m12 by remember { mutableStateOf("0") }

    var m20 by remember { mutableStateOf("0") }
    var m21 by remember { mutableStateOf("0") }
    var m22 by remember { mutableStateOf("1") }

    var resultText by remember { mutableStateOf<String?>(null) }

    fun getArray(): Array<DoubleArray> {
        return if (dim == 2) {
            arrayOf(
                doubleArrayOf(m00.toDoubleOrNull() ?: 0.0, m01.toDoubleOrNull() ?: 0.0),
                doubleArrayOf(m10.toDoubleOrNull() ?: 0.0, m11.toDoubleOrNull() ?: 0.0)
            )
        } else {
            arrayOf(
                doubleArrayOf(m00.toDoubleOrNull() ?: 0.0, m01.toDoubleOrNull() ?: 0.0, m02.toDoubleOrNull() ?: 0.0),
                doubleArrayOf(m10.toDoubleOrNull() ?: 0.0, m11.toDoubleOrNull() ?: 0.0, m12.toDoubleOrNull() ?: 0.0),
                doubleArrayOf(m20.toDoubleOrNull() ?: 0.0, m21.toDoubleOrNull() ?: 0.0, m22.toDoubleOrNull() ?: 0.0)
            )
        }
    }

    fun calculateDet() {
        try {
            val d = MatrixEngine.determinant(getArray())
            resultText = "Det(A) = $d"
        } catch (e: Exception) {
            resultText = "Error: ${e.message}"
        }
    }

    fun calculateInv() {
        try {
            val inv = MatrixEngine.inverse(getArray())
            val rows = inv.map { row -> row.joinToString("  ") { String.format("%.3f", it) } }
            resultText = "Inverse A⁻¹:\n" + rows.joinToString("\n")
        } catch (e: Exception) {
            resultText = "Error: ${e.message}"
        }
    }

    fun calculateTrn() {
        try {
            val trn = MatrixEngine.transpose(getArray())
            val rows = trn.map { row -> row.joinToString("  ") { String.format("%.2f", it) } }
            resultText = "Transpose Aᵀ:\n" + rows.joinToString("\n")
        } catch (e: Exception) {
            resultText = "Error: ${e.message}"
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
            Text("Matrix Operations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dimension Selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Dimension:", fontSize = 13.sp, color = Color(0xFF94A3B8))
            Button(
                onClick = { dim = 2 },
                colors = ButtonDefaults.buttonColors(containerColor = if (dim == 2) Color(0xFF2563EB) else Color(0xFF1E293B))
            ) { Text("2 × 2") }
            Button(
                onClick = { dim = 3 },
                colors = ButtonDefaults.buttonColors(containerColor = if (dim == 3) Color(0xFF2563EB) else Color(0xFF1E293B))
            ) { Text("3 × 3") }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Matrix Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Matrix A:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.height(8.dp))

                // Row 0
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoeffField("A[0,0]", m00, Modifier.weight(1f)) { m00 = it }
                    CoeffField("A[0,1]", m01, Modifier.weight(1f)) { m01 = it }
                    if (dim == 3) CoeffField("A[0,2]", m02, Modifier.weight(1f)) { m02 = it }
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoeffField("A[1,0]", m10, Modifier.weight(1f)) { m10 = it }
                    CoeffField("A[1,1]", m11, Modifier.weight(1f)) { m11 = it }
                    if (dim == 3) CoeffField("A[1,2]", m12, Modifier.weight(1f)) { m12 = it }
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Row 2 (if 3x3)
                if (dim == 3) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CoeffField("A[2,0]", m20, Modifier.weight(1f)) { m20 = it }
                        CoeffField("A[2,1]", m21, Modifier.weight(1f)) { m21 = it }
                        CoeffField("A[2,2]", m22, Modifier.weight(1f)) { m22 = it }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { calculateDet() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("matrix_det_button")
            ) { Text("Determinant", fontSize = 12.sp) }

            Button(
                onClick = { calculateInv() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("matrix_inv_button")
            ) { Text("Inverse", fontSize = 12.sp) }

            Button(
                onClick = { calculateTrn() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("matrix_trn_button")
            ) { Text("Transpose", fontSize = 12.sp) }
        }

        if (resultText != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = resultText!!,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
