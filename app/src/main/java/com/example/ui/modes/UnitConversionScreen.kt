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
import com.example.engine.UnitConversionEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConversionScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember { UnitConversionEngine.categories }
    var selectedCatIdx by remember { mutableStateOf(0) }
    val currentCat = categories[selectedCatIdx]

    var fromUnitIdx by remember { mutableStateOf(0) }
    var toUnitIdx by remember { mutableStateOf(1.coerceAtMost(currentCat.units.size - 1)) }
    var inputValue by remember { mutableStateOf("100") }

    val fromUnit = currentCat.units.getOrElse(fromUnitIdx) { currentCat.units.first() }
    val toUnit = currentCat.units.getOrElse(toUnitIdx) { currentCat.units.last() }

    val convertedValue = remember(inputValue, fromUnit, toUnit) {
        val num = inputValue.toDoubleOrNull() ?: 0.0
        UnitConversionEngine.convert(num, fromUnit, toUnit)
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
            Text("Unit Conversion", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCatIdx,
            containerColor = Color(0xFF1E293B),
            contentColor = Color(0xFF38BDF8),
            edgePadding = 0.dp
        ) {
            categories.forEachIndexed { idx, cat ->
                Tab(
                    selected = selectedCatIdx == idx,
                    onClick = {
                        selectedCatIdx = idx
                        fromUnitIdx = 0
                        toUnitIdx = 1.coerceAtMost(categories[idx].units.size - 1)
                    },
                    text = { Text(cat.name, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("From:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("unit_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Source Unit: ${fromUnit.name} (${fromUnit.symbol})", fontSize = 12.sp, color = Color(0xFF38BDF8))

                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    currentCat.units.take(4).forEachIndexed { idx, u ->
                        FilterChip(
                            selected = fromUnitIdx == idx,
                            onClick = { fromUnitIdx = idx },
                            label = { Text(u.symbol, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Output Converted Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("To Target Unit: ${toUnit.name} (${toUnit.symbol})", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    currentCat.units.forEachIndexed { idx, u ->
                        FilterChip(
                            selected = toUnitIdx == idx,
                            onClick = { toUnitIdx = idx },
                            label = { Text(u.symbol, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                Text("Converted Result:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.6g", convertedValue)} ${toUnit.symbol}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4ADE80),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
