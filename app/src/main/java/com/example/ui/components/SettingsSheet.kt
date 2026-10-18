package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: CalculatorSettings,
    onUpdateSettings: (CalculatorSettings) -> Unit,
    onResetAll: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calculator Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Angle Unit
            Text("Angle Unit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AngleUnit.values().forEach { unit ->
                    val isSelected = settings.angleUnit == unit
                    OutlinedButton(
                        onClick = { onUpdateSettings(settings.copy(angleUnit = unit)) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155))
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(unit.shortName, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Input / Output Format
            Text("Input / Output Format", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
            Spacer(modifier = Modifier.height(6.dp))
            InputOutputFormat.values().forEach { fmt ->
                val isSelected = settings.ioFormat == fmt
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onUpdateSettings(settings.copy(ioFormat = fmt)) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onUpdateSettings(settings.copy(ioFormat = fmt)) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF38BDF8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(fmt.label, fontSize = 13.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Fraction Format
            Text("Fraction Display", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FractionFormat.values().forEach { frac ->
                    val isSelected = settings.fractionFormat == frac
                    OutlinedButton(
                        onClick = { onUpdateSettings(settings.copy(fractionFormat = frac)) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(frac.label, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Number Format
            Text("Number Format", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isNorm1 = settings.numberFormat is NumberFormat.Norm1
                val isNorm2 = settings.numberFormat is NumberFormat.Norm2
                OutlinedButton(
                    onClick = { onUpdateSettings(settings.copy(numberFormat = NumberFormat.Norm1)) },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isNorm1) Color(0xFF2563EB) else Color(0xFF1E293B)),
                    modifier = Modifier.weight(1f)
                ) { Text("Norm 1", color = Color.White) }
                OutlinedButton(
                    onClick = { onUpdateSettings(settings.copy(numberFormat = NumberFormat.Norm2)) },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isNorm2) Color(0xFF2563EB) else Color(0xFF1E293B)),
                    modifier = Modifier.weight(1f)
                ) { Text("Norm 2", color = Color.White) }
                OutlinedButton(
                    onClick = { onUpdateSettings(settings.copy(numberFormat = NumberFormat.Fix(3))) },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (settings.numberFormat is NumberFormat.Fix) Color(0xFF2563EB) else Color(0xFF1E293B)),
                    modifier = Modifier.weight(1f)
                ) { Text("Fix 3", color = Color.White) }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Reset All
            Button(
                onClick = {
                    onResetAll()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Reset All (Clear Memory & Restore Defaults)", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
