package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectorSheet(
    currentApp: CalculatorApp,
    onSelectApp: (CalculatorApp) -> Unit,
    onDismiss: () -> Unit,
    onOpenCoverageDashboard: () -> Unit,
    onOpenFunctionExplorer: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Application Modes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = onOpenFunctionExplorer,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Explorer", fontSize = 12.sp, color = Color(0xFF38BDF8))
                    }

                    FilledTonalButton(
                        onClick = onOpenCoverageDashboard,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E293B)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Dashboard", fontSize = 12.sp, color = Color(0xFF4ADE80))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                items(CalculatorApp.values()) { app ->
                    val isSelected = app == currentApp
                    val bg = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
                    val borderColor = if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("app_mode_${app.name.lowercase()}")
                            .clickable {
                                onSelectApp(app)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(containerColor = bg),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val icon = when (app) {
                                CalculatorApp.CALCULATE -> Icons.Default.Calculate
                                CalculatorApp.COMPLEX -> Icons.Default.Numbers
                                CalculatorApp.BASE_N -> Icons.Default.Code
                                CalculatorApp.MATRIX -> Icons.Default.GridOn
                                CalculatorApp.VECTOR -> Icons.Default.Navigation
                                CalculatorApp.STATISTICS -> Icons.Default.BarChart
                                CalculatorApp.DISTRIBUTION -> Icons.Default.ShowChart
                                CalculatorApp.TABLE -> Icons.Default.TableChart
                                CalculatorApp.EQUATION -> Icons.Default.Functions
                                CalculatorApp.INEQUALITY -> Icons.Default.Balance
                                CalculatorApp.RATIO -> Icons.Default.Percent
                                CalculatorApp.SPREADSHEET -> Icons.Default.Grid4x4
                                CalculatorApp.UNIT_CONV -> Icons.Default.SyncAlt
                                CalculatorApp.MATH_BOX -> Icons.Default.Casino
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = app.title,
                                tint = if (isSelected) Color.White else Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = app.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
