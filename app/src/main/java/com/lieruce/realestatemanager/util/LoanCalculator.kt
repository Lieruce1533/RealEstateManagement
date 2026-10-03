package com.lieruce.realestatemanager.util

import kotlin.math.pow

/**
 * Data container holding calculated mortgage amortization results.
 */
data class LoanResult(
    val principal: Double,
    val monthlyPayment: Double,
    val totalInterest: Double,
    val totalPayment: Double
)

/**
 * LoanCalculator provides pure mathematical mortgage and loan amortization computations,
 * completely decoupled from Android framework dependencies for fast local unit testing.
 */
object LoanCalculator {

    /**
     * Calculates monthly mortgage payment, total interest, and total loan cost
     * based on property price, down payment, annual interest rate, and term in years.
     */
    fun calculateLoan(
        price: Double,
        downPayment: Double,
        annualInterestRate: Double,
        termYears: Int
    ): LoanResult {
        // Ensure principal is non-negative
        val principal = (price - downPayment).coerceAtLeast(0.0)
        
        // Compute monthly interest rate and total payment months
        val monthlyInterestRate = (annualInterestRate / 100.0) / 12.0
        val totalMonths = (termYears * 12).coerceAtLeast(1)

        // Standard amortization formula: M = P [ r(1 + r)^n ] / [ (1 + r)^n – 1]
        val monthlyPayment = if (monthlyInterestRate == 0.0) {
            principal / totalMonths
        } else {
            val factor = (1.0 + monthlyInterestRate).pow(totalMonths.toDouble())
            principal * (monthlyInterestRate * factor) / (factor - 1.0)
        }

        val totalPayment = monthlyPayment * totalMonths
        val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)

        return LoanResult(
            principal = principal,
            monthlyPayment = monthlyPayment,
            totalInterest = totalInterest,
            totalPayment = totalPayment
        )
    }
}
