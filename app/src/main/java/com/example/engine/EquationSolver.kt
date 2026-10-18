package com.example.engine

import kotlin.math.*

/**
 * Solves simultaneous linear equations and polynomial equations up to degree 4.
 */
object EquationSolver {

    sealed class LinearResult {
        data class Unique(val solutions: List<Double>) : LinearResult()
        object InfiniteSolutions : LinearResult()
        object NoSolution : LinearResult()
    }

    data class QuadraticResult(
        val root1: ComplexNumber,
        val root2: ComplexNumber,
        val vertexX: Double,
        val vertexY: Double,
        val isMinimum: Boolean
    )

    data class PolynomialResult(
        val roots: List<ComplexNumber>
    )

    /**
     * Solves a system of n linear equations: A * x = B using Gaussian elimination with partial pivoting.
     */
    fun solveLinear(coefficients: Array<DoubleArray>, constants: DoubleArray): LinearResult {
        val n = constants.size
        val a = Array(n) { r ->
            DoubleArray(n + 1) { c ->
                if (c < n) coefficients[r][c] else constants[r]
            }
        }

        for (i in 0 until n) {
            var pivot = i
            for (j in i + 1 until n) {
                if (abs(a[j][i]) > abs(a[pivot][i])) pivot = j
            }

            if (abs(a[pivot][i]) < 1e-11) {
                // Check if all rows below are zero
                var allZero = true
                for (r in i until n) {
                    if (abs(a[r][n]) > 1e-11) return LinearResult.NoSolution
                    for (c in i until n) {
                        if (abs(a[r][c]) > 1e-11) allZero = false
                    }
                }
                if (allZero) return LinearResult.InfiniteSolutions
                continue
            }

            val temp = a[i]
            a[i] = a[pivot]
            a[pivot] = temp

            for (j in i + 1 until n) {
                val factor = a[j][i] / a[i][i]
                for (k in i until n + 1) {
                    a[j][k] -= factor * a[i][k]
                }
            }
        }

        // Back substitution
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = a[i][n]
            for (j in i + 1 until n) {
                sum -= a[i][j] * x[j]
            }
            if (abs(a[i][i]) < 1e-11) {
                return if (abs(sum) < 1e-11) LinearResult.InfiniteSolutions else LinearResult.NoSolution
            }
            x[i] = sum / a[i][i]
        }

        return LinearResult.Unique(x.toList())
    }

    /**
     * Solves quadratic ax² + bx + c = 0
     */
    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticResult {
        require(abs(a) > 1e-12) { "Coefficient 'a' cannot be zero in quadratic equation" }
        val discriminant = b * b - 4 * a * c
        val r1: ComplexNumber
        val r2: ComplexNumber

        if (discriminant >= 0) {
            val sqrtD = sqrt(discriminant)
            r1 = ComplexNumber((-b + sqrtD) / (2 * a), 0.0)
            r2 = ComplexNumber((-b - sqrtD) / (2 * a), 0.0)
        } else {
            val sqrtD = sqrt(-discriminant)
            r1 = ComplexNumber(-b / (2 * a), sqrtD / (2 * a))
            r2 = ComplexNumber(-b / (2 * a), -sqrtD / (2 * a))
        }

        val vx = -b / (2 * a)
        val vy = c - (b * b) / (4 * a)
        val isMin = a > 0

        return QuadraticResult(r1, r2, vx, vy, isMin)
    }

    /**
     * Solves cubic ax³ + bx² + cx + d = 0 using Cardano's formula.
     */
    fun solveCubic(a: Double, b: Double, c: Double, d: Double): PolynomialResult {
        require(abs(a) > 1e-12) { "Coefficient 'a' cannot be zero in cubic equation" }
        // Normalized: x³ + px² + qx + r = 0
        val p = b / a
        val q = c / a
        val r = d / a

        // Depressed cubic: t³ + At + B = 0 with x = t - p/3
        val bigA = q - (p * p) / 3.0
        val bigB = (2.0 * p * p * p) / 27.0 - (p * q) / 3.0 + r

        val delta = (bigB * bigB) / 4.0 + (bigA * bigA * bigA) / 27.0
        val shift = p / 3.0

        val roots = mutableListOf<ComplexNumber>()

        if (delta > 1e-11) {
            // One real root and two complex conjugate roots
            val u = cbrt(-bigB / 2.0 + sqrt(delta))
            val v = cbrt(-bigB / 2.0 - sqrt(delta))
            val t1 = u + v
            roots.add(ComplexNumber(t1 - shift, 0.0))

            val realPart = -(u + v) / 2.0 - shift
            val imagPart = (u - v) * sqrt(3.0) / 2.0
            roots.add(ComplexNumber(realPart, imagPart))
            roots.add(ComplexNumber(realPart, -imagPart))
        } else if (abs(delta) <= 1e-11) {
            // All roots are real, at least two are equal
            val u = cbrt(-bigB / 2.0)
            val t1 = 2.0 * u
            val t2 = -u
            roots.add(ComplexNumber(t1 - shift, 0.0))
            roots.add(ComplexNumber(t2 - shift, 0.0))
            roots.add(ComplexNumber(t2 - shift, 0.0))
        } else {
            // delta < 0: Three distinct real roots (Casus Irreducibilis)
            val rho = sqrt(-(bigA * bigA * bigA) / 27.0)
            val theta = acos((-bigB / (2.0 * rho)).coerceIn(-1.0, 1.0))
            val m = 2.0 * cbrt(rho)
            val t1 = m * cos(theta / 3.0)
            val t2 = m * cos((theta + 2.0 * Math.PI) / 3.0)
            val t3 = m * cos((theta + 4.0 * Math.PI) / 3.0)
            roots.add(ComplexNumber(t1 - shift, 0.0))
            roots.add(ComplexNumber(t2 - shift, 0.0))
            roots.add(ComplexNumber(t3 - shift, 0.0))
        }

        return PolynomialResult(roots)
    }

    /**
     * Solves quartic ax⁴ + bx³ + cx² + dx + e = 0 using Durand-Kerner numerical root-finding.
     */
    fun solveQuartic(a: Double, b: Double, c: Double, d: Double, e: Double): PolynomialResult {
        require(abs(a) > 1e-12) { "Coefficient 'a' cannot be zero in quartic equation" }
        val p = doubleArrayOf(e / a, d / a, c / a, b / a, 1.0)
        // Durand-Kerner method
        val roots = Array(4) { i ->
            val angle = 2.0 * Math.PI * i / 4.0 + 0.4
            ComplexNumber(0.4 * cos(angle), 0.9 * sin(angle))
        }

        for (iter in 0 until 100) {
            for (i in 0 until 4) {
                var polyVal = ComplexNumber(p[4], 0.0)
                for (j in 3 downTo 0) {
                    polyVal = polyVal * roots[i] + ComplexNumber(p[j], 0.0)
                }

                var denom = ComplexNumber.ONE
                for (j in 0 until 4) {
                    if (j != i) {
                        denom = denom * (roots[i] - roots[j])
                    }
                }
                if (denom.magnitude > 1e-12) {
                    roots[i] = roots[i] - (polyVal / denom)
                }
            }
        }
        return PolynomialResult(roots.toList())
    }
}
