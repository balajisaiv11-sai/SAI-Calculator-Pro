package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorApp
import com.example.model.FunctionRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogSheet(
    activeApp: CalculatorApp,
    onInsertToken: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val catalogCategories = remember {
        listOf(
            "Function Analysis",
            "Probability",
            "Numeric",
            "Angle/Coord",
            "Hyperbolic",
            "Logarithms",
            "Scientific Constants",
            "Unit Conversion",
            "Engineering Symbols"
        )
    }

    var selectedCat by remember { mutableStateOf(catalogCategories.first()) }

    val itemsForCategory = remember(selectedCat) {
        FunctionRegistry.allFunctions.filter { it.category == selectedCat }
    }

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CATALOG", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category selector tabs/chips
            ScrollableTabRow(
                selectedTabIndex = catalogCategories.indexOf(selectedCat),
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF38BDF8),
                edgePadding = 0.dp
            ) {
                catalogCategories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCat == cat,
                        onClick = { selectedCat = cat },
                        text = { Text(cat, fontSize = 12.sp, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(itemsForCategory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val token = if (item.notation.contains("(") || item.notation.length <= 2) item.notation else "${item.notation}("
                                onInsertToken(token)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text(item.expectedBehavior, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                            Text(
                                text = item.notation,
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
