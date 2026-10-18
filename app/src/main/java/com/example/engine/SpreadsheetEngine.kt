package com.example.engine

/**
 * Spreadsheet computation engine for a 5-column (A..E) x 10-row (1..10) cell grid.
 * Evaluates values and formulas (=A1+B1, =SUM(A1:A5), =MEAN(A1:A5), etc.).
 */
class SpreadsheetEngine {
    val cols = listOf("A", "B", "C", "D", "E")
    val rowCount = 10

    // Raw cell input strings (e.g. "12", "=A1+2", "=SUM(A1:A5)")
    val rawCells = mutableMapOf<String, String>()
    // Cached computed values
    val computedValues = mutableMapOf<String, Double>()

    init {
        // Sample starter data
        rawCells["A1"] = "10"
        rawCells["A2"] = "20"
        rawCells["A3"] = "30"
        rawCells["B1"] = "5"
        rawCells["B2"] = "15"
        rawCells["B3"] = "=A3-10"
        rawCells["C1"] = "=SUM(A1:A3)"
        rawCells["C2"] = "=MEAN(A1:A3)"
        recalculateAll()
    }

    fun setCell(cellRef: String, value: String) {
        val norm = cellRef.uppercase().trim()
        if (value.isBlank()) {
            rawCells.remove(norm)
            computedValues.remove(norm)
        } else {
            rawCells[norm] = value.trim()
        }
        recalculateAll()
    }

    fun recalculateAll() {
        computedValues.clear()
        val visiting = mutableSetOf<String>()

        for (col in cols) {
            for (r in 1..rowCount) {
                val ref = "$col$r"
                evalCell(ref, visiting)
            }
        }
    }

    private fun evalCell(ref: String, visiting: MutableSet<String>): Double {
        if (computedValues.containsKey(ref)) return computedValues[ref]!!
        val raw = rawCells[ref] ?: return 0.0

        if (!raw.startsWith("=")) {
            val num = raw.toDoubleOrNull() ?: 0.0
            computedValues[ref] = num
            return num
        }

        if (ref in visiting) {
            // Circular dependency detected
            return Double.NaN
        }

        visiting.add(ref)
        val formula = raw.substring(1).trim().uppercase()
        val res = evaluateFormula(formula, visiting)
        visiting.remove(ref)

        computedValues[ref] = res
        return res
    }

    private fun evaluateFormula(formula: String, visiting: MutableSet<String>): Double {
        // Range functions: SUM, MEAN, MIN, MAX, COUNT
        val rangeRegex = Regex("""(SUM|MEAN|MIN|MAX|COUNT)\(([A-E][1-9]|10):([A-E][1-9]|10)\)""")
        val match = rangeRegex.matchEntire(formula)
        if (match != null) {
            val fn = match.groupValues[1]
            val startRef = match.groupValues[2]
            val endRef = match.groupValues[3]
            val values = getRangeValues(startRef, endRef, visiting)
            if (values.isEmpty()) return 0.0
            return when (fn) {
                "SUM" -> values.sum()
                "MEAN" -> values.average()
                "MIN" -> values.minOrNull() ?: 0.0
                "MAX" -> values.maxOrNull() ?: 0.0
                "COUNT" -> values.size.toDouble()
                else -> 0.0
            }
        }

        // Basic arithmetic between cell references and constants (e.g. A1 + B2)
        var parsedExpr = formula
        val cellRefRegex = Regex("""[A-E](?:10|[1-9])""")
        parsedExpr = cellRefRegex.replace(parsedExpr) { m ->
            val ref = m.value
            val v = evalCell(ref, visiting)
            if (v.isNaN()) "0" else v.toString()
        }

        return try {
            val mathEngine = MathEngine()
            mathEngine.evaluate(parsedExpr).numericValue
        } catch (_: Exception) {
            0.0
        }
    }

    private fun getRangeValues(start: String, end: String, visiting: MutableSet<String>): List<Double> {
        val startCol = start[0]
        val startRow = start.substring(1).toInt()
        val endCol = end[0]
        val endRow = end.substring(1).toInt()

        val cMin = minOf(startCol, endCol)
        val cMax = maxOf(startCol, endCol)
        val rMin = minOf(startRow, endRow)
        val rMax = maxOf(startRow, endRow)

        val list = mutableListOf<Double>()
        for (c in cMin..cMax) {
            for (r in rMin..rMax) {
                val ref = "$c$r"
                list.add(evalCell(ref, visiting))
            }
        }
        return list
    }
}
