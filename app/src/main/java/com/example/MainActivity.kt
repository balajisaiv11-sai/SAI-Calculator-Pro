package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CalculatorApp
import com.example.model.FunctionRegistry
import com.example.ui.components.*
import com.example.ui.modes.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveSheet
import com.example.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF020617) // Ultra-dark titanium slate
                ) {
                    ScientificCalculatorApp()
                }
            }
        }
    }
}

@Composable
fun ScientificCalculatorApp(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle Android system Back button: if on a sub-screen or sheet, go back
    BackHandler(enabled = uiState.currentApp != CalculatorApp.CALCULATE || uiState.activeSheet != null) {
        if (uiState.activeSheet != null) {
            viewModel.closeSheet()
        } else if (uiState.currentApp != CalculatorApp.CALCULATE) {
            viewModel.setAppMode(CalculatorApp.CALCULATE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF090D16),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Main Calculator Chassis / Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 500.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                // --- TOP APP HEADER / BRANDING & STATUS ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Sai CALCULATOR PRO",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF1E3A8A),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "fx-991CW",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "${FunctionRegistry.totalCount} Functions • Natural Textbook Display",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Quick access action shortcuts
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Function Explorer button
                        IconButton(
                            onClick = { viewModel.openSheet(ActiveSheet.FUNCTION_EXPLORER) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                .testTag("btn_explorer")
                        ) {
                            Text("fx", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Coverage Dashboard button
                        IconButton(
                            onClick = { viewModel.openSheet(ActiveSheet.COVERAGE_DASHBOARD) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                .testTag("btn_dashboard")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Coverage", tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                        }

                        // App Modes Launcher
                        IconButton(
                            onClick = { viewModel.openSheet(ActiveSheet.MENU) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF2563EB), RoundedCornerShape(6.dp))
                                .testTag("btn_menu")
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = "Apps", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- MAIN SCREEN DISPLAY / DEDICATED APP SCREENS ---
                when (uiState.currentApp) {
                    CalculatorApp.CALCULATE, CalculatorApp.COMPLEX -> {
                        // Standard Calculator LCD Display + Keypad
                        Column(modifier = Modifier.fillMaxSize()) {
                            LcdDisplay(
                                uiState = uiState,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            CalculatorKeypad(
                                uiState = uiState,
                                onKeyClick = { key -> viewModel.onKeyPress(key) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    CalculatorApp.EQUATION, CalculatorApp.INEQUALITY -> {
                        EquationScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.MATRIX -> {
                        MatrixScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.STATISTICS -> {
                        StatisticsScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.BASE_N -> {
                        BaseNScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.SPREADSHEET -> {
                        SpreadsheetScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.UNIT_CONV -> {
                        UnitConversionScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    CalculatorApp.MATH_BOX -> {
                        MathBoxScreen(onBackToCalculate = { viewModel.setAppMode(CalculatorApp.CALCULATE) })
                    }
                    else -> {
                        // Fallback to calculator LCD with mode tag
                        Column(modifier = Modifier.fillMaxSize()) {
                            LcdDisplay(uiState = uiState)
                            Spacer(modifier = Modifier.weight(1f))
                            CalculatorKeypad(uiState = uiState, onKeyClick = { viewModel.onKeyPress(it) })
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEETS ---
        when (uiState.activeSheet) {
            ActiveSheet.MENU -> {
                ModeSelectorSheet(
                    currentApp = uiState.currentApp,
                    onSelectApp = { app -> viewModel.setAppMode(app) },
                    onDismiss = { viewModel.closeSheet() },
                    onOpenCoverageDashboard = { viewModel.openSheet(ActiveSheet.COVERAGE_DASHBOARD) },
                    onOpenFunctionExplorer = { viewModel.openSheet(ActiveSheet.FUNCTION_EXPLORER) }
                )
            }
            ActiveSheet.FUNCTION_EXPLORER -> {
                FunctionExplorerSheet(
                    onInsertToken = { token -> viewModel.insertText(token) },
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.COVERAGE_DASHBOARD -> {
                CoverageDashboardSheet(
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.CATALOG -> {
                CatalogSheet(
                    activeApp = uiState.currentApp,
                    onInsertToken = { token -> viewModel.insertText(token) },
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.TOOLS -> {
                ToolsSheet(
                    activeApp = uiState.currentApp,
                    onExecuteTool = { tool -> viewModel.insertText(tool) },
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.SETTINGS -> {
                SettingsSheet(
                    settings = uiState.settings,
                    onUpdateSettings = { s -> viewModel.updateSettings(s) },
                    onResetAll = { viewModel.clearAll() },
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.VARIABLES -> {
                VariableInspectorSheet(
                    variables = uiState.variables,
                    ans = uiState.variables["Ans"] ?: 0.0,
                    onInsertVariable = { v -> viewModel.insertText(v) },
                    onStoreToVariable = { v, valNum -> viewModel.setVariable(v, valNum) },
                    onDismiss = { viewModel.closeSheet() }
                )
            }
            ActiveSheet.HISTORY, null -> {}
        }
    }
}
