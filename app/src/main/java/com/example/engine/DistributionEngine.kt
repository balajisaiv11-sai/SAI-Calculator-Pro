package com.example.engine

import kotlin.math.*

object DistributionEngine {

    fun normalPD(x: Double, mean: Double = 0.0, stdDev: Double = 1.0): Double {
        require(stdDev > 0) { "Standard deviation must be positive" }
        val z = (x - mean) / stdDev
        return (1.0 / (stdDev * sqrt(2.0 * Math.PI))) * exp(-0.5 * z * z)
    }

    fun normalCD(lower: Double, upper: Double, mean: Double = 0.0, stdDev: Double = 1.0): Double {
        require(stdDev > 0) { "Standard deviation must be positive" }
        val phiUpper = phi((upper - mean) / stdDev)
        val phiLower = phi((lower - mean) / stdDev)
        return (phiUpper - phiLower).coerceIn(0.0, 1.0)
    }

    private fun phi(z: Double): Double = 0.5 * (1.0 + erf(z / sqrt(2.0)))

    private fun erf(x: Double): Double {
        // Abramowitz and Stegun formula 7.1.26 approximation
        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911

        val sign = if (x < 0) -1.0 else 1.0
        val t = 1.0 / (1.0 + p * abs(x))
        val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * exp(-x * x)
        return sign * y
    }

    fun inverseNormal(area: Double, mean: Double = 0.0, stdDev: Double = 1.0): Double {
        require(area in 0.0..1.0 && stdDev > 0) { "Area must be between 0 and 1, std dev > 0" }
        // Rational approximation for inverse standard normal
        val q = area - 0.5
        val z: Double
        if (abs(q) <= 0.42) {
            val r = q * q
            z = q * (((-25.44106049637 * r + 41.39119773534) * r - 18.61500062529) * r + 2.50662823884) /
                    ((((3.13082909833 * r - 21.06224101826) * r + 23.08336743743) * r + 1.0))
        } else {
            val r = if (q > 0) 1.0 - area else area
            val s = sqrt(-2.0 * ln(r))
            val num = ((0.010328 * s + 0.802853) * s + 2.515517)
            val den = ((0.001308 * s + 0.189269) * s + 1.432788) * s + 1.0
            val t = s - num / den
            z = if (q > 0) t else -t
        }
        return mean + z * stdDev
    }

    fun binomialPD(x: Int, n: Int, p: Double): Double {
        require(n >= 0 && x in 0..n && p in 0.0..1.0) { "Invalid binomial parameters" }
        val nCr = nChooseK(n, x)
        return nCr * p.pow(x) * (1.0 - p).pow(n - x)
    }

    fun binomialCD(x: Int, n: Int, p: Double): Double {
        require(n >= 0 && p in 0.0..1.0) { "Invalid binomial parameters" }
        var sum = 0.0
        val limit = x.coerceIn(0, n)
        for (k in 0..limit) {
            sum += binomialPD(k, n, p)
        }
        return sum.coerceIn(0.0, 1.0)
    }

    fun poissonPD(x: Int, lambda: Double): Double {
        require(x >= 0 && lambda > 0) { "x must be >= 0, lambda > 0" }
        var fact = 1.0
        for (i in 2..x) fact *= i
        return (lambda.pow(x) * exp(-lambda)) / fact
    }

    fun poissonCD(x: Int, lambda: Double): Double {
        require(x >= 0 && lambda > 0) { "x must be >= 0, lambda > 0" }
        var sum = 0.0
        for (k in 0..x) {
            sum += poissonPD(k, lambda)
        }
        return sum.coerceIn(0.0, 1.0)
    }

    private fun nChooseK(n: Int, k: Int): Double {
        if (k < 0 || k > n) return 0.0
        if (k == 0 || k == n) return 1.0
        val r = min(k, n - k)
        var c = 1.0
        for (i in 1..r) {
            c = c * (n - i + 1) / i
        }
        return c
    }
}
