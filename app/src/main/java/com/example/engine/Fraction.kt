package com.example.engine

import kotlin.math.abs

/**
 * Representation of an exact rational number (Fraction).
 * Handles proper, improper, and mixed fractions with automatic simplification.
 */
data class Fraction(val numerator: Long, val denominator: Long = 1L) : Comparable<Fraction> {
    init {
        require(denominator != 0L) { "Denominator cannot be zero" }
    }

    val isNegative: Boolean = (numerator < 0) xor (denominator < 0)
    val absNumerator: Long = abs(numerator)
    val absDenominator: Long = abs(denominator)

    val simplified: Fraction by lazy {
        val g = gcd(absNumerator, absDenominator)
        val sNum = absNumerator / g
        val sDen = absDenominator / g
        if (isNegative) Fraction(-sNum, sDen) else Fraction(sNum, sDen)
    }

    operator fun plus(other: Fraction): Fraction {
        val num = this.numerator * other.denominator + other.numerator * this.denominator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplified
    }

    operator fun minus(other: Fraction): Fraction {
        val num = this.numerator * other.denominator - other.numerator * this.denominator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplified
    }

    operator fun times(other: Fraction): Fraction {
        val num = this.numerator * other.numerator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplified
    }

    operator fun div(other: Fraction): Fraction {
        require(other.numerator != 0L) { "Division by zero fraction" }
        val num = this.numerator * other.denominator
        val den = this.denominator * other.numerator
        return Fraction(num, den).simplified
    }

    fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

    fun toMixedString(): String {
        val simp = this.simplified
        val whole = simp.numerator / simp.denominator
        val rem = abs(simp.numerator % simp.denominator)
        return when {
            rem == 0L -> "$whole"
            whole == 0L -> "${simp.numerator}/${simp.denominator}"
            else -> "$whole ${rem}/${simp.denominator}"
        }
    }

    override fun toString(): String {
        val simp = this.simplified
        return if (simp.denominator == 1L) "${simp.numerator}" else "${simp.numerator}/${simp.denominator}"
    }

    override fun compareTo(other: Fraction): Int {
        val diff = this.numerator * other.denominator - other.numerator * this.denominator
        return diff.compareTo(0L)
    }

    companion object {
        val ZERO = Fraction(0L, 1L)
        val ONE = Fraction(1L, 1L)

        fun gcd(a: Long, b: Long): Long {
            var x = abs(a)
            var y = abs(b)
            while (y != 0L) {
                val temp = y
                y = x % y
                x = temp
            }
            return if (x == 0L) 1L else x
        }

        fun lcm(a: Long, b: Long): Long {
            if (a == 0L || b == 0L) return 0L
            return abs(a * b) / gcd(a, b)
        }

        fun fromDouble(value: Double, maxDenominator: Long = 100000L): Fraction {
            if (value.isNaN() || value.isInfinite()) throw IllegalArgumentException("Cannot convert NaN or Infinite to fraction")
            val isNeg = value < 0
            val x = abs(value)
            
            var m00 = 1L
            var m01 = 0L
            var m10 = 0L
            var m11 = 1L

            var z = x
            var previousError = Double.MAX_VALUE

            while (true) {
                val a = z.toLong()
                val newM00 = a * m00 + m01
                val newM10 = a * m10 + m11
                if (newM10 > maxDenominator) break

                m01 = m00
                m00 = newM00
                m11 = m10
                m10 = newM10

                val currentVal = m00.toDouble() / m10.toDouble()
                val error = abs(x - currentVal)
                if (error < 1e-12 || error >= previousError) break
                previousError = error
                val fracPart = z - a
                if (fracPart < 1e-14) break
                z = 1.0 / fracPart
            }

            val sign = if (isNeg) -1L else 1L
            return Fraction(sign * m00, m10).simplified
        }
    }
}
