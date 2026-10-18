package com.example.engine

data class UnitCategory(
    val name: String,
    val units: List<UnitItem>
)

data class UnitItem(
    val id: String,
    val name: String,
    val symbol: String,
    val toBaseFactor: Double, // Multiplier to SI base unit
    val baseOffset: Double = 0.0 // For temperature offset
)

object UnitConversionEngine {

    val categories = listOf(
        UnitCategory(
            name = "Length",
            units = listOf(
                UnitItem("m", "Meter", "m", 1.0),
                UnitItem("km", "Kilometer", "km", 1000.0),
                UnitItem("cm", "Centimeter", "cm", 0.01),
                UnitItem("mm", "Millimeter", "mm", 0.001),
                UnitItem("in", "Inch", "in", 0.0254),
                UnitItem("ft", "Foot", "ft", 0.3048),
                UnitItem("yd", "Yard", "yd", 0.9144),
                UnitItem("mile", "Mile", "mi", 1609.344),
                UnitItem("n_mile", "Nautical Mile", "NM", 1852.0)
            )
        ),
        UnitCategory(
            name = "Area",
            units = listOf(
                UnitItem("m2", "Square Meter", "m²", 1.0),
                UnitItem("km2", "Square Kilometer", "km²", 1000000.0),
                UnitItem("cm2", "Square Centimeter", "cm²", 0.0001),
                UnitItem("ha", "Hectare", "ha", 10000.0),
                UnitItem("acre", "Acre", "ac", 4046.8564224),
                UnitItem("sq_ft", "Square Foot", "ft²", 0.09290304)
            )
        ),
        UnitCategory(
            name = "Volume",
            units = listOf(
                UnitItem("L", "Liter", "L", 0.001),
                UnitItem("mL", "Milliliter", "mL", 0.000001),
                UnitItem("m3", "Cubic Meter", "m³", 1.0),
                UnitItem("gal_us", "Gallon (US)", "gal", 0.003785411784),
                UnitItem("gal_uk", "Gallon (UK)", "gal", 0.00454609),
                UnitItem("pt", "Pint (US)", "pt", 0.000473176473)
            )
        ),
        UnitCategory(
            name = "Mass",
            units = listOf(
                UnitItem("kg", "Kilogram", "kg", 1.0),
                UnitItem("g", "Gram", "g", 0.001),
                UnitItem("mg", "Milligram", "mg", 0.000001),
                UnitItem("t", "Metric Ton", "t", 1000.0),
                UnitItem("lb", "Pound", "lb", 0.45359237),
                UnitItem("oz", "Ounce", "oz", 0.028349523125)
            )
        ),
        UnitCategory(
            name = "Velocity",
            units = listOf(
                UnitItem("m_s", "Meter per second", "m/s", 1.0),
                UnitItem("km_h", "Kilometer per hour", "km/h", 1.0 / 3.6),
                UnitItem("mph", "Mile per hour", "mph", 0.44704),
                UnitItem("knot", "Knot", "kn", 1852.0 / 3600.0)
            )
        ),
        UnitCategory(
            name = "Pressure",
            units = listOf(
                UnitItem("Pa", "Pascal", "Pa", 1.0),
                UnitItem("kPa", "Kilopascal", "kPa", 1000.0),
                UnitItem("bar", "Bar", "bar", 100000.0),
                UnitItem("atm", "Atmosphere", "atm", 101325.0),
                UnitItem("mmHg", "Millimeter of mercury", "mmHg", 133.322387415),
                UnitItem("psi", "Pound per square inch", "psi", 6894.757)
            )
        ),
        UnitCategory(
            name = "Energy",
            units = listOf(
                UnitItem("J", "Joule", "J", 1.0),
                UnitItem("kJ", "Kilojoule", "kJ", 1000.0),
                UnitItem("cal", "Calorie", "cal", 4.184),
                UnitItem("kcal", "Kilocalorie", "kcal", 4184.0),
                UnitItem("kWh", "Kilowatt-hour", "kWh", 3600000.0),
                UnitItem("eV", "Electronvolt", "eV", 1.602176634e-19)
            )
        ),
        UnitCategory(
            name = "Temperature",
            units = listOf(
                UnitItem("degC", "Celsius", "°C", 1.0, 0.0),
                UnitItem("degF", "Fahrenheit", "°F", 5.0 / 9.0, -32.0),
                UnitItem("K", "Kelvin", "K", 1.0, -273.15)
            )
        )
    )

    fun convert(value: Double, fromUnit: UnitItem, toUnit: UnitItem): Double {
        if (fromUnit == toUnit) return value
        // Special case for temperature
        if (fromUnit.id in listOf("degC", "degF", "K") && toUnit.id in listOf("degC", "degF", "K")) {
            // First convert to Celsius
            val celsius = when (fromUnit.id) {
                "degC" -> value
                "degF" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            // Then convert Celsius to target
            return when (toUnit.id) {
                "degC" -> celsius
                "degF" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard SI base conversion
        val baseVal = value * fromUnit.toBaseFactor
        return baseVal / toUnit.toBaseFactor
    }
}
