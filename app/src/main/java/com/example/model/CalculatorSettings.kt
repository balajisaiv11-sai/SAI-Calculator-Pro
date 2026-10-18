package com.example.model

enum class AngleUnit(val label: String, val shortName: String) {
    DEG("Degree", "DEG"),
    RAD("Radian", "RAD"),
    GRAD("Gradian", "GRAD")
}

enum class InputOutputFormat(val label: String) {
    MATH_I_MATH_O("MathI / MathO (Natural Textbook)"),
    MATH_I_DECIMAL_O("MathI / DecimalO"),
    LINE_I_LINE_O("LineI / LineO"),
    LINE_I_DECIMAL_O("LineI / DecimalO")
}

enum class FractionFormat(val label: String) {
    IMPROPER("d/c (Improper)"),
    MIXED("a b/c (Mixed)")
}

sealed class NumberFormat {
    object Norm1 : NumberFormat()
    object Norm2 : NumberFormat()
    data class Fix(val digits: Int) : NumberFormat()
    data class Sci(val digits: Int) : NumberFormat()

    fun label(): String = when (this) {
        is Norm1 -> "Norm 1"
        is Norm2 -> "Norm 2"
        is Fix -> "Fix $digits"
        is Sci -> "Sci $digits"
    }
}

enum class CalculatorApp(val title: String, val subtitle: String, val iconName: String) {
    CALCULATE("Calculate", "General scientific calculations", "calculate"),
    COMPLEX("Complex", "Complex number arithmetic (a+bi, r∠θ)", "complex"),
    BASE_N("Base-N", "Binary, Octal, Decimal, Hexadecimal", "base_n"),
    MATRIX("Matrix", "Matrix computations up to 4×4", "matrix"),
    VECTOR("Vector", "2D & 3D Vector operations", "vector"),
    STATISTICS("Statistics", "1-Var & 2-Var Regression", "statistics"),
    DISTRIBUTION("Distribution", "Normal, Binomial, Poisson probabilities", "distribution"),
    TABLE("Table", "Numerical value tables for f(x) and g(x)", "table"),
    EQUATION("Equation", "Simultaneous & Polynomial equations", "equation"),
    INEQUALITY("Inequality", "Polynomial inequalities", "inequality"),
    RATIO("Ratio", "Ratio and proportion solver", "ratio"),
    SPREADSHEET("Spreadsheet", "Spreadsheet grid with formulas", "spreadsheet"),
    UNIT_CONV("Unit Conversion", "40+ metric and imperial units", "unit_conv"),
    MATH_BOX("Math Box", "Coin toss & dice roll simulations", "math_box")
}

data class CalculatorSettings(
    val angleUnit: AngleUnit = AngleUnit.DEG,
    val ioFormat: InputOutputFormat = InputOutputFormat.MATH_I_MATH_O,
    val fractionFormat: FractionFormat = FractionFormat.IMPROPER,
    val numberFormat: NumberFormat = NumberFormat.Norm1,
    val engineeringNotation: Boolean = false,
    val hapticFeedback: Boolean = true,
    val keySound: Boolean = false,
    val highContrastLcd: Boolean = false
)
