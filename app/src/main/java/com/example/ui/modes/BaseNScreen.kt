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

@Composable
fun BaseNScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeBase by remember { mutableStateOf("DEC") } // DEC, HEX, BIN, OCT
    var inputStr by remember { mutableStateOf("42") }

    val currentLong: Long = remember(inputStr, activeBase) {
        val radix = when (activeBase) {
            "HEX" -> 16
            "BIN" -> 2
            "OCT" -> 8
            else -> 10
        }
        inputStr.toLongOrNull(radix) ?: 0L
    }

    val decVal = currentLong.toString(10)
    val hexVal = currentLong.toString(16).uppercase()
    val binVal = currentLong.toString(2)
    val octVal = currentLong.toString(8)

    fun applyBitwise(op: (Long) -> Long) {
        val res = op(currentLong)
        val radix = when (activeBase) {
            "HEX" -> 16
            "BIN" -> 2
            "OCT" -> 8
            else -> 10
        }
        inputStr = res.toString(radix)
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
            Text("Base-N Programmer Mode", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Base Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("DEC", "HEX", "BIN", "OCT").forEach { base ->
                Button(
                    onClick = { activeBase = base },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeBase == base) Color(0xFF2563EB) else Color(0xFF1E293B)),
                    modifier = Modifier.weight(1f)
                ) { Text(base, fontSize = 12.sp) }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Field
        OutlinedTextField(
            value = inputStr,
            onValueChange = { inputStr = it },
            label = { Text("Input in $activeBase", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Conversions Display Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseRow("DEC", decVal, activeBase == "DEC")
                BaseRow("HEX", "0x$hexVal", activeBase == "HEX")
                BaseRow("BIN", binVal, activeBase == "BIN")
                BaseRow("OCT", "0o$octVal", activeBase == "OCT")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bitwise Operations
        Text("Bitwise Operations", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = { applyBitwise { it.inv() } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)), modifier = Modifier.weight(1f)) { Text("NOT") }
            Button(onClick = { applyBitwise { it shl 1 } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)), modifier = Modifier.weight(1f)) { Text("SHL") }
            Button(onClick = { applyBitwise { it shr 1 } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)), modifier = Modifier.weight(1f)) { Text("SHR") }
            Button(onClick = { applyBitwise { -it } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)), modifier = Modifier.weight(1f)) { Text("NEG") }
        }
    }
}

@Composable
fun BaseRow(label: String, value: String, isCurrent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrent) Color(0xFF2563EB).copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isCurrent) Color(0xFF38BDF8) else Color(0xFF94A3B8))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White, fontFamily = FontFamily.Monospace)
    }
}
