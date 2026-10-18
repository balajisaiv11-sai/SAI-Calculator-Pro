package com.example.engine

import kotlin.math.acos
import kotlin.math.sqrt

/**
 * 2D and 3D Vector calculation engine.
 * Dot Product, Cross Product, Magnitude, Unit Vector, Angle between vectors.
 */
object VectorEngine {

    fun dot(a: DoubleArray, b: DoubleArray): Double {
        require(a.size == b.size) { "Vectors must have same dimension" }
        return a.indices.sumOf { a[it] * b[it] }
    }

    fun magnitude(a: DoubleArray): Double = sqrt(dot(a, a))

    fun cross(a: DoubleArray, b: DoubleArray): DoubleArray {
        require(a.size == 3 && b.size == 3) { "Cross product is only defined for 3D vectors" }
        return doubleArrayOf(
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        )
    }

    fun unit(a: DoubleArray): DoubleArray {
        val mag = magnitude(a)
        require(mag > 1e-12) { "Cannot compute unit vector of zero vector" }
        return DoubleArray(a.size) { a[it] / mag }
    }

    fun angle(a: DoubleArray, b: DoubleArray, inDegrees: Boolean = true): Double {
        val magA = magnitude(a)
        val magB = magnitude(b)
        require(magA > 1e-12 && magB > 1e-12) { "Zero vector has no defined angle" }
        val cosTheta = (dot(a, b) / (magA * magB)).coerceIn(-1.0, 1.0)
        val rad = acos(cosTheta)
        return if (inDegrees) Math.toDegrees(rad) else rad
    }

    fun add(a: DoubleArray, b: DoubleArray): DoubleArray {
        require(a.size == b.size) { "Vectors must have same dimension" }
        return DoubleArray(a.size) { a[it] + b[it] }
    }

    fun subtract(a: DoubleArray, b: DoubleArray): DoubleArray {
        require(a.size == b.size) { "Vectors must have same dimension" }
        return DoubleArray(a.size) { a[it] - b[it] }
    }
}
