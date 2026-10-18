package com.example.engine

import kotlin.math.sqrt

data class OneVarStats(
    val n: Int,
    val mean: Double,
    val sumX: Double,
    val sumX2: Double,
    val popVar: Double,
    val sampleVar: Double,
    val popStdDev: Double,
    val sampleStdDev: Double,
    val minX: Double,
    val q1: Double,
    val median: Double,
    val q3: Double,
    val maxX: Double
)

data class TwoVarStats(
    val n: Int,
    val meanX: Double,
    val meanY: Double,
    val stdDevX: Double,
    val stdDevY: Double,
    val interceptA: Double,
    val slopeB: Double,
    val correlationR: Double,
    val rSquared: Double
) {
    fun estimateY(x: Double): Double = interceptA + slopeB * x
    fun estimateX(y: Double): Double = if (slopeB != 0.0) (y - interceptA) / slopeB else Double.NaN
}

object StatisticsEngine {

    fun calculateOneVar(data: List<Double>): OneVarStats {
        require(data.isNotEmpty()) { "Data cannot be empty" }
        val n = data.size
        val sorted = data.sorted()
        val sumX = data.sum()
        val sumX2 = data.sumOf { it * it }
        val mean = sumX / n

        val popVar = data.sumOf { (it - mean) * (it - mean) } / n
        val sampleVar = if (n > 1) data.sumOf { (it - mean) * (it - mean) } / (n - 1) else 0.0
        val popSd = sqrt(popVar)
        val sampleSd = sqrt(sampleVar)

        val minX = sorted.first()
        val maxX = sorted.last()
        val med = medianOf(sorted)
        val q1 = medianOf(sorted.subList(0, n / 2))
        val q3 = medianOf(sorted.subList((n + 1) / 2, n))

        return OneVarStats(
            n = n, mean = mean, sumX = sumX, sumX2 = sumX2,
            popVar = popVar, sampleVar = sampleVar,
            popStdDev = popSd, sampleStdDev = sampleSd,
            minX = minX, q1 = q1, median = med, q3 = q3, maxX = maxX
        )
    }

    private fun medianOf(list: List<Double>): Double {
        if (list.isEmpty()) return 0.0
        val mid = list.size / 2
        return if (list.size % 2 == 1) list[mid] else (list[mid - 1] + list[mid]) / 2.0
    }

    fun calculateTwoVar(xList: List<Double>, yList: List<Double>): TwoVarStats {
        require(xList.size == yList.size && xList.size >= 2) { "Need at least 2 coordinate pairs" }
        val n = xList.size
        val sumX = xList.sum()
        val sumY = yList.sum()
        val meanX = sumX / n
        val meanY = sumY / n

        var ssXX = 0.0
        var ssYY = 0.0
        var ssXY = 0.0

        for (i in 0 until n) {
            val dx = xList[i] - meanX
            val dy = yList[i] - meanY
            ssXX += dx * dx
            ssYY += dy * dy
            ssXY += dx * dy
        }

        val b = if (ssXX != 0.0) ssXY / ssXX else 0.0
        val a = meanY - b * meanX
        val r = if (ssXX > 0 && ssYY > 0) ssXY / sqrt(ssXX * ssYY) else 0.0
        val r2 = r * r

        val sdX = sqrt(ssXX / (n - 1))
        val sdY = sqrt(ssYY / (n - 1))

        return TwoVarStats(
            n = n, meanX = meanX, meanY = meanY,
            stdDevX = sdX, stdDevY = sdY,
            interceptA = a, slopeB = b,
            correlationR = r, rSquared = r2
        )
    }
}
