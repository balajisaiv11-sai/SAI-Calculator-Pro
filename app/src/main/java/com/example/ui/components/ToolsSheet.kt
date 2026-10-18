package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorApp

data class ToolCommand(val name: String, val notation: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsSheet(
    activeApp: CalculatorApp,
    onExecuteTool: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val toolsList = when (activeApp) {
        CalculatorApp.CALCULATE -> listOf(
            ToolCommand("Factorial", "!", "Appends factorial"),
            ToolCommand("Permutation", "nPr", "Combinatorics nPr"),
            ToolCommand("Combination", "nCr", "Combinatorics nCr"),
            ToolCommand("Polar Coordinates", "Pol(", "Rectangular to polar"),
            ToolCommand("Rectangular Coordinates", "Rec(", "Polar to rectangular"),
            ToolCommand("Greatest Common Divisor", "gcd(", "GCD of two numbers"),
            ToolCommand("Least Common Multiple", "lcm(", "LCM of two numbers")
        )
        CalculatorApp.COMPLEX -> listOf(
            ToolCommand("Complex Conjugate", "conjg(", "Conjugate a - bi"),
            ToolCommand("Argument Angle", "arg(", "Angle in active unit"),
            ToolCommand("Real Part", "rep(", "Extract real component"),
            ToolCommand("Imaginary Part", "imp(", "Extract imaginary coefficient"),
            ToolCommand("Polar Form", "▶r∠θ", "Convert to polar magnitude and angle")
        )
        CalculatorApp.BASE_N -> listOf(
            ToolCommand("Decimal Base", "DEC", "Switch to decimal base"),
            ToolCommand("Hexadecimal Base", "HEX", "Switch to hexadecimal base"),
            ToolCommand("Binary Base", "BIN", "Switch to binary base"),
            ToolCommand("Octal Base", "OCT", "Switch to octal base"),
            ToolCommand("Bitwise AND", " and ", "Bitwise AND"),
            ToolCommand("Bitwise OR", " or ", "Bitwise OR"),
            ToolCommand("Bitwise XOR", " xor ", "Bitwise XOR"),
            ToolCommand("Bitwise NOT", " not ", "Bitwise NOT")
        )
        CalculatorApp.MATRIX -> listOf(
            ToolCommand("Determinant", "det(", "Calculate determinant"),
            ToolCommand("Inverse", "inv(", "Matrix inverse"),
            ToolCommand("Transpose", "trn(", "Matrix transpose"),
            ToolCommand("Identity Matrix", "Identity(3)", "3x3 Identity matrix")
        )
        CalculatorApp.VECTOR -> listOf(
            ToolCommand("Dot Product", " • ", "Vector dot product"),
            ToolCommand("Cross Product", " × ", "Vector cross product"),
            ToolCommand("Magnitude", "Norm(", "Vector length"),
            ToolCommand("Unit Vector", "UnitV(", "Normalize vector")
        )
        else -> listOf(
            ToolCommand("Recall Ans", "Ans", "Insert last calculation answer"),
            ToolCommand("Clear Display", "AC", "Clear current input")
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOOLS (${activeApp.title})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(toolsList) { tool ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onExecuteTool(tool.notation)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tool.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text(tool.description, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                            Text(
                                text = tool.notation,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
