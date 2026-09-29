package com.lieruce.realestatemanager

import com.lieruce.realestatemanager.util.LoanCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Local JVM unit tests for LoanCalculator pure mathematical mortgage computations.
 */
class LoanCalculatorTest {

    @Test
    fun testStandardMortgageCalculation() {
        val result = LoanCalculator.calculateLoan(
            price = 500000.0,
            downPayment = 100000.0,
            annualInterestRate = 6.0,
            termYears = 30
        )

        // Principal should be $400,000
        assertEquals(400000.0, result.principal, 0.01)
        // Monthly payment for $400k at 6% over 30 years is ~$2,398.20
        assertEquals(2398.20, result.monthlyPayment, 1.0)
    }

    @Test
    fun testZeroInterestRateCalculation() {
        val result = LoanCalculator.calculateLoan(
            price = 300000.0,
            downPayment = 60000.0,
            annualInterestRate = 0.0,
            termYears = 10
        )

        // Principal = 240,000; 10 years = 120 months; 240000 / 120 = 2000 per month
        assertEquals(240000.0, result.principal, 0.01)
        assertEquals(2000.0, result.monthlyPayment, 0.01)
        assertEquals(0.0, result.totalInterest, 0.01)
    }
}
