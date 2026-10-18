package com.example.engine

import kotlin.math.*

/**
 * Representation of a complex number z = real + imag * i.
 * Supports rectangular, polar (r∠θ), conjugate, argument, magnitude, and powers.
 */
data class ComplexNumber(val real: Double, val imag: Double = 0.0) {
    val magnitude: Double get() = hypot(real, imag)
    val argumentRad: Double get() = atan2(imag, real)
    val argumentDeg: Double get() = Math.toDegrees(argumentRad)

    operator fun plus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(this.real + other.real, this.imag + other.imag)

    operator fun minus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(this.real - other.real, this.imag - other.imag)

    operator fun times(other: ComplexNumber): ComplexNumber =
        ComplexNumber(
            this.real * other.real - this.imag * other.imag,
            this.real * other.imag + this.imag * other.real
        )

    operator fun div(other: ComplexNumber): ComplexNumber {
        val denom = other.real * other.real + other.imag * other.imag
        require(denom != 0.0) { "Complex division by zero" }
        return ComplexNumber(
            (this.real * other.real + this.imag * other.imag) / denom,
            (this.imag * other.real - this.real * other.imag) / denom
        )
    }

    operator fun plus(other: Double): ComplexNumber = ComplexNumber(this.real + other, this.imag)
    operator fun minus(other: Double): ComplexNumber = ComplexNumber(this.real - other, this.imag)
    operator fun times(other: Double): ComplexNumber = ComplexNumber(this.real * other, this.imag * other)
    operator fun div(other: Double): ComplexNumber {
        require(other != 0.0) { "Division by zero" }
        return ComplexNumber(this.real / other, this.imag / other)
    }

    fun conjugate(): ComplexNumber = ComplexNumber(real, -imag)

    fun pow(n: Double): ComplexNumber {
        val r = magnitude.pow(n)
        val theta = argumentRad * n
        return ComplexNumber(r * cos(theta), r * sin(theta))
    }

    fun sqrt(): ComplexNumber {
        val r = sqrt(magnitude)
        val theta = argumentRad / 2.0
        return ComplexNumber(r * cos(theta), r * sin(theta))
    }

    fun toRectangularString(decimals: Int = 4): String {
        val rStr = formatNum(real, decimals)
        val iAbs = abs(imag)
        val iStr = formatNum(iAbs, decimals)
        return when {
            abs(imag) < 1e-12 -> rStr
            abs(real) < 1e-12 -> if (imag < 0) "-${iStr}i" else "${iStr}i"
            imag < 0 -> "$rStr - ${iStr}i"
            else -> "$rStr + ${iStr}i"
        }
    }

    fun toPolarString(inDegrees: Boolean = true, decimals: Int = 4): String {
        val rStr = formatNum(magnitude, decimals)
        val angle = if (inDegrees) argumentDeg else argumentRad
        val unit = if (inDegrees) "°" else " rad"
        val angleStr = formatNum(angle, decimals)
        return "$rStr ∠ $angleStr$unit"
    }

    companion object {
        val ZERO = ComplexNumber(0.0, 0.0)
        val ONE = ComplexNumber(1.0, 0.0)
        val I = ComplexNumber(0.0, 1.0)

        fun fromPolar(r: Double, theta: Double, isDegrees: Boolean = false): ComplexNumber {
            val angle = if (isDegrees) Math.toRadians(theta) else theta
            return ComplexNumber(r * cos(angle), r * sin(angle))
        }

        private fun formatNum(n: Double, decimals: Int): String {
            if (abs(n - round(n)) < 1e-9) return round(n).toLong().toString()
            val factor = 10.0.pow(decimals)
            val rounded = round(n * factor) / factor
            return rounded.toString()
        }
    }
}
