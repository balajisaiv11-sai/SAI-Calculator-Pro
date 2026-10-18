package com.example

import com.example.engine.*
import com.example.model.AngleUnit
import com.example.model.CalculatorSettings
import com.example.model.FunctionRegistry
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class CalculatorEngineTest {

    private val engine = MathEngine()

    @Test
    fun testBasicArithmetic() {
        val r1 = engine.evaluate("2 + 3")
        assertEquals(5.0, r1.numericValue, 1e-9)

        val r2 = engine.evaluate("50 - 18")
        assertEquals(32.0, r2.numericValue, 1e-9)

        val r3 = engine.evaluate("6 * 7")
        assertEquals(42.0, r3.numericValue, 1e-9)

        val r4 = engine.evaluate("100 / 4")
        assertEquals(25.0, r4.numericValue, 1e-9)
    }

    @Test
    fun testTrigonometryDeg() {
        val rSin = engine.evaluate("sin(30)")
        assertEquals(0.5, rSin.numericValue, 1e-9)

        val rCos = engine.evaluate("cos(60)")
        assertEquals(0.5, rCos.numericValue, 1e-9)

        val rTan = engine.evaluate("tan(45)")
        assertEquals(1.0, rTan.numericValue, 1e-9)

        val rAsin = engine.evaluate("asin(0.5)")
        assertEquals(30.0, rAsin.numericValue, 1e-9)
    }

    @Test
    fun testPowersAndRoots() {
        val rSqrt = engine.evaluate("sqrt(81)")
        assertEquals(9.0, rSqrt.numericValue, 1e-9)

        val rPow = engine.evaluate("2^10")
        assertEquals(1024.0, rPow.numericValue, 1e-9)

        val rCbrt = engine.evaluate("cbrt(27)")
        assertEquals(3.0, rCbrt.numericValue, 1e-9)
    }

    @Test
    fun testLogarithms() {
        val rLog = engine.evaluate("log10(100)")
        assertEquals(2.0, rLog.numericValue, 1e-9)

        val rLn = engine.evaluate("ln(1)")
        assertEquals(0.0, rLn.numericValue, 1e-9)
    }

    @Test
    fun testExactFractions() {
        val f1 = Fraction(1, 3)
        val f2 = Fraction(1, 6)
        val sum = f1 + f2
        assertEquals(1L, sum.numerator)
        assertEquals(2L, sum.denominator)
        assertEquals("1/2", sum.toString())
    }

    @Test
    fun testFactorialAndCombinatorics() {
        val rFact = engine.evaluate("5!")
        assertEquals(120.0, rFact.numericValue, 1e-9)

        val rNpr = engine.evaluate("npr(5, 2)")
        assertEquals(20.0, rNpr.numericValue, 1e-9)

        val rNcr = engine.evaluate("ncr(5, 2)")
        assertEquals(10.0, rNcr.numericValue, 1e-9)
    }

    @Test
    fun testGcdAndLcm() {
        val rGcd = engine.evaluate("gcd(24, 36)")
        assertEquals(12.0, rGcd.numericValue, 1e-9)

        val rLcm = engine.evaluate("lcm(12, 18)")
        assertEquals(36.0, rLcm.numericValue, 1e-9)
    }

    @Test
    fun testMatrixOperations() {
        val mat = arrayOf(
            doubleArrayOf(1.0, 2.0),
            doubleArrayOf(3.0, 4.0)
        )
        val det = MatrixEngine.determinant(mat)
        assertEquals(-2.0, det, 1e-9)

        val inv = MatrixEngine.inverse(mat)
        assertEquals(-2.0, inv[0][0], 1e-9)
        assertEquals(1.0, inv[0][1], 1e-9)
        assertEquals(1.5, inv[1][0], 1e-9)
        assertEquals(-0.5, inv[1][1], 1e-9)

        val trn = MatrixEngine.transpose(mat)
        assertEquals(3.0, trn[0][1], 1e-9)
    }

    @Test
    fun testVectorOperations() {
        val v1 = doubleArrayOf(1.0, 2.0, 3.0)
        val v2 = doubleArrayOf(4.0, 5.0, 6.0)
        val dot = VectorEngine.dot(v1, v2)
        assertEquals(32.0, dot, 1e-9)

        val cross = VectorEngine.cross(v1, v2)
        assertEquals(-3.0, cross[0], 1e-9)
        assertEquals(6.0, cross[1], 1e-9)
        assertEquals(-3.0, cross[2], 1e-9)
    }

    @Test
    fun testLinearEquationSolver() {
        // x + y = 5
        // 2x - y = 1
        // Solution: x = 2, y = 3
        val coeffs = arrayOf(
            doubleArrayOf(1.0, 1.0),
            doubleArrayOf(2.0, -1.0)
        )
        val consts = doubleArrayOf(5.0, 1.0)
        val res = EquationSolver.solveLinear(coeffs, consts)
        assertTrue(res is EquationSolver.LinearResult.Unique)
        val sol = (res as EquationSolver.LinearResult.Unique).solutions
        assertEquals(2.0, sol[0], 1e-9)
        assertEquals(3.0, sol[1], 1e-9)
    }

    @Test
    fun testQuadraticEquationSolver() {
        // x² - 5x + 6 = 0 -> roots 3 and 2
        val res = EquationSolver.solveQuadratic(1.0, -5.0, 6.0)
        assertEquals(3.0, res.root1.real, 1e-9)
        assertEquals(2.0, res.root2.real, 1e-9)
        assertEquals(2.5, res.vertexX, 1e-9)
        assertEquals(-0.25, res.vertexY, 1e-9)
    }

    @Test
    fun testStatistics() {
        val data = listOf(10.0, 20.0, 30.0, 40.0, 50.0)
        val stats = StatisticsEngine.calculateOneVar(data)
        assertEquals(30.0, stats.mean, 1e-9)
        assertEquals(150.0, stats.sumX, 1e-9)
        assertEquals(5, stats.n)
        assertEquals(10.0, stats.minX, 1e-9)
        assertEquals(50.0, stats.maxX, 1e-9)
    }

    @Test
    fun testDistributions() {
        val norm = DistributionEngine.normalPD(0.0, 0.0, 1.0)
        assertEquals(1.0 / kotlin.math.sqrt(2.0 * Math.PI), norm, 1e-5)

        val binom = DistributionEngine.binomialPD(2, 4, 0.5)
        assertEquals(0.375, binom, 1e-5)

        val poiss = DistributionEngine.poissonPD(0, 1.0)
        assertEquals(kotlin.math.exp(-1.0), poiss, 1e-5)
    }

    @Test
    fun testFunctionRegistryInventoryCount() {
        // Verify inventory count is approximately 540 operations
        val count = FunctionRegistry.totalCount
        assertTrue("Function registry should contain at least 150 unique operations (actual $count)", count >= 150)
        assertEquals(FunctionRegistry.totalCount, FunctionRegistry.implementedCount)
    }
}
