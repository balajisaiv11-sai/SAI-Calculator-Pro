package com.example.engine

import kotlin.math.abs

/**
 * Matrix calculation engine supporting 2x2, 3x3, and 4x4 matrices.
 * Implements Determinant, Inverse, Transpose, Matrix Arithmetic, and Identity generation.
 */
object MatrixEngine {

    fun determinant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        require(n > 0 && matrix.all { it.size == n }) { "Matrix must be square" }
        return when (n) {
            1 -> matrix[0][0]
            2 -> matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
            3 -> {
                val a = matrix
                a[0][0] * (a[1][1] * a[2][2] - a[1][2] * a[2][1]) -
                a[0][1] * (a[1][0] * a[2][2] - a[1][2] * a[2][0]) +
                a[0][2] * (a[1][0] * a[2][1] - a[1][1] * a[2][0])
            }
            else -> luDeterminant(matrix)
        }
    }

    private fun luDeterminant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        val a = Array(n) { r -> matrix[r].clone() }
        var det = 1.0

        for (i in 0 until n) {
            var pivot = i
            for (j in i + 1 until n) {
                if (abs(a[j][i]) > abs(a[pivot][i])) pivot = j
            }
            if (abs(a[pivot][i]) < 1e-12) return 0.0

            if (pivot != i) {
                val temp = a[i]
                a[i] = a[pivot]
                a[pivot] = temp
                det = -det
            }

            det *= a[i][i]
            for (j in i + 1 until n) {
                val factor = a[j][i] / a[i][i]
                for (k in i until n) {
                    a[j][k] -= factor * a[i][k]
                }
            }
        }
        return det
    }

    fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val result = Array(cols) { DoubleArray(rows) }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                result[c][r] = matrix[r][c]
            }
        }
        return result
    }

    fun inverse(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val n = matrix.size
        require(n > 0 && matrix.all { it.size == n }) { "Matrix must be square" }
        val det = determinant(matrix)
        if (abs(det) < 1e-12) throw ArithmeticException("Matrix is singular (determinant is zero); cannot invert")

        if (n == 2) {
            val d = matrix
            return arrayOf(
                doubleArrayOf(d[1][1] / det, -d[0][1] / det),
                doubleArrayOf(-d[1][0] / det, d[0][0] / det)
            )
        }

        // Gauss-Jordan elimination on [A | I]
        val aug = Array(n) { r ->
            DoubleArray(2 * n) { c ->
                if (c < n) matrix[r][c] else if (c - n == r) 1.0 else 0.0
            }
        }

        for (i in 0 until n) {
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(aug[k][i]) > abs(aug[maxRow][i])) maxRow = k
            }
            val temp = aug[i]
            aug[i] = aug[maxRow]
            aug[maxRow] = temp

            val div = aug[i][i]
            if (abs(div) < 1e-12) throw ArithmeticException("Matrix is singular")
            for (j in 0 until 2 * n) {
                aug[i][j] /= div
            }

            for (k in 0 until n) {
                if (k != i) {
                    val factor = aug[k][i]
                    for (j in 0 until 2 * n) {
                        aug[k][j] -= factor * aug[i][j]
                    }
                }
            }
        }

        val inv = Array(n) { DoubleArray(n) }
        for (r in 0 until n) {
            for (c in 0 until n) {
                inv[r][c] = aug[r][c + n]
            }
        }
        return inv
    }

    fun multiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val rA = a.size
        val cA = a[0].size
        val rB = b.size
        val cB = b[0].size
        require(cA == rB) { "Matrix dimensions incompatible for multiplication ($rA×$cA and $rB×$cB)" }

        val res = Array(rA) { DoubleArray(cB) }
        for (i in 0 until rA) {
            for (j in 0 until cB) {
                var sum = 0.0
                for (k in 0 until cA) {
                    sum += a[i][k] * b[k][j]
                }
                res[i][j] = sum
            }
        }
        return res
    }

    fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        require(a.size == b.size && a[0].size == b[0].size) { "Matrix dimensions must match for addition" }
        return Array(a.size) { r ->
            DoubleArray(a[0].size) { c -> a[r][c] + b[r][c] }
        }
    }

    fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        require(a.size == b.size && a[0].size == b[0].size) { "Matrix dimensions must match for subtraction" }
        return Array(a.size) { r ->
            DoubleArray(a[0].size) { c -> a[r][c] - b[r][c] }
        }
    }

    fun scalarMultiply(matrix: Array<DoubleArray>, scalar: Double): Array<DoubleArray> {
        return Array(matrix.size) { r ->
            DoubleArray(matrix[0].size) { c -> matrix[r][c] * scalar }
        }
    }
}
