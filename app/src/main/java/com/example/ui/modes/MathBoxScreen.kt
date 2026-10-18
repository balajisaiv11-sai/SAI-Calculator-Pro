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
import kotlin.random.Random

@Composable
fun MathBoxScreen(
    onBackToCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTool by remember { mutableStateOf(0) } // 0: Coin Toss, 1: Dice Roll
    var attempts by remember { mutableStateOf(50) }

    // Coin simulation results: count of heads per toss (for 2 coins: 0 heads, 1 head, 2 heads)
    var coinNum by remember { mutableStateOf(2) } // 1..3
    var coinCounts by remember { mutableStateOf(mapOf(0 to 0, 1 to 0, 2 to 0)) }

    // Dice simulation results: sums
    var diceNum by remember { mutableStateOf(2) } // 1..3
    var diceCounts by remember { mutableStateOf(mapOf<Int, Int>()) }

    fun runCoinSimulation() {
        val map = mutableMapOf<Int, Int>()
        for (i in 0..coinNum) map[i] = 0
        repeat(attempts) {
            var heads = 0
            repeat(coinNum) {
                if (Random.nextBoolean()) heads++
            }
            map[heads] = (map[heads] ?: 0) + 1
        }
        coinCounts = map
    }

    fun runDiceSimulation() {
        val map = mutableMapOf<Int, Int>()
        val minSum = diceNum
        val maxSum = diceNum * 6
        for (s in minSum..maxSum) map[s] = 0

        repeat(attempts) {
            var sum = 0
            repeat(diceNum) {
                sum += Random.nextInt(1, 7)
            }
            map[sum] = (map[sum] ?: 0) + 1
        }
        diceCounts = map
    }

    LaunchedEffect(selectedTool, attempts, coinNum, diceNum) {
        if (selectedTool == 0) runCoinSimulation() else runDiceSimulation()
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
            Text("Math Box Learning Simulation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedButton(
                onClick = onBackToCalculate,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E293B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text("Back to Calc", fontSize = 11.sp, color = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { selectedTool = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTool == 0) Color(0xFF2563EB) else Color(0xFF1E293B)),
                modifier = Modifier.weight(1f)
            ) { Text("Coin Toss") }

            Button(
                onClick = { selectedTool = 1 },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTool == 1) Color(0xFF2563EB) else Color(0xFF1E293B)),
                modifier = Modifier.weight(1f)
            ) { Text("Dice Roll") }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Parameters Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (selectedTool == 0) {
                    Text("Number of Coins: $coinNum", fontSize = 13.sp, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 2, 3).forEach { n ->
                            FilterChip(
                                selected = coinNum == n,
                                onClick = { coinNum = n },
                                label = { Text("$n coin${if (n > 1) "s" else ""}") }
                            )
                        }
                    }
                } else {
                    Text("Number of Dice: $diceNum", fontSize = 13.sp, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 2, 3).forEach { n ->
                            FilterChip(
                                selected = diceNum == n,
                                onClick = { diceNum = n },
                                label = { Text("$n dice") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Attempts: $attempts", fontSize = 13.sp, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(20, 50, 100, 250).forEach { att ->
                        FilterChip(
                            selected = attempts == att,
                            onClick = { attempts = att },
                            label = { Text("$att") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (selectedTool == 0) runCoinSimulation() else runDiceSimulation()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier.fillMaxWidth().testTag("simulate_button")
                ) {
                    Text("Re-simulate")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Probability Distribution Output
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Simulation Frequency Results (N = $attempts)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTool == 0) {
                    coinCounts.forEach { (heads, count) ->
                        val pct = if (attempts > 0) (count.toFloat() / attempts) * 100f else 0f
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$heads Heads", fontSize = 13.sp, color = Color.White)
                                Text("$count (${String.format("%.1f", pct)}%)", fontSize = 13.sp, color = Color(0xFF4ADE80), fontFamily = FontFamily.Monospace)
                            }
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0xFF334155)
                            )
                        }
                    }
                } else {
                    diceCounts.forEach { (sum, count) ->
                        val pct = if (attempts > 0) (count.toFloat() / attempts) * 100f else 0f
                        Column(modifier = Modifier.padding(vertical = 3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sum $sum", fontSize = 12.sp, color = Color.White)
                                Text("$count (${String.format("%.1f", pct)}%)", fontSize = 12.sp, color = Color(0xFF4ADE80), fontFamily = FontFamily.Monospace)
                            }
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp),
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}
