package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lieruce.realestatemanager.Utils
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * LoanCalculatorScreen provides an interactive mortgage and loan simulation tool,
 * allowing agents to calculate monthly payments, total interest, and loan amortization
 * based on property price, down payment, interest rate, and loan term.
 * Supports switching currencies between Dollars ($) and Euros (€) dynamically.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen(
    modifier: Modifier = Modifier,
    initialPrice: Int? = null,
    viewModel: PropertyViewModel,
    onBackClick: () -> Unit
) {
    // Currency toggle state synced with ViewModel global setting or togglable locally
    var isEuros by remember { mutableStateOf(viewModel.isEuroCurrency) }

    // If initial price is in USD, convert to Euros if euro mode is active
    val adjustedInitialPrice = if (isEuros && initialPrice != null) {
        Utils.convertDollarToEuro(initialPrice)
    } else {
        initialPrice
    }

    // Input state variables (starts empty if launched from settings without an initial price)
    var priceText by remember { mutableStateOf(adjustedInitialPrice?.toString() ?: "") }
    var downPaymentText by remember { mutableStateOf("") }
    var interestRateText by remember { mutableStateOf("6.5") }
    var termYearsText by remember { mutableStateOf("30") }

    // Parse numerical values safely
    val price = priceText.toDoubleOrNull() ?: 0.0
    val downPayment = downPaymentText.toDoubleOrNull() ?: 0.0
    val annualInterestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val termYears = termYearsText.toIntOrNull() ?: 30

    // Mortgage calculations
    val principal = (price - downPayment).coerceAtLeast(0.0)
    val monthlyInterestRate = (annualInterestRate / 100.0) / 12.0
    val totalMonths = (termYears * 12).coerceAtLeast(1)

    val monthlyPayment = if (monthlyInterestRate == 0.0) {
        principal / totalMonths
    } else {
        val factor = (1.0 + monthlyInterestRate).pow(totalMonths.toDouble())
        principal * (monthlyInterestRate * factor) / (factor - 1.0)
    }

    val totalPayment = monthlyPayment * totalMonths
    val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)

    val currencySymbol = if (isEuros) "€" else "$"
    val currencyFormatter = NumberFormat.getNumberInstance(Locale.US)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortgage Loan Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Clear / reset button to clear calculator inputs
                    IconButton(onClick = {
                        priceText = ""
                        downPaymentText = ""
                        interestRateText = "6.5"
                        termYearsText = "30"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear Calculator")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Results Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Estimated Monthly Payment",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$currencySymbol${currencyFormatter.format(monthlyPayment.roundToLong())}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Interest", style = MaterialTheme.typography.bodySmall)
                                Text("$currencySymbol${currencyFormatter.format(totalInterest.roundToLong())}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Loan Cost", style = MaterialTheme.typography.bodySmall)
                                Text("$currencySymbol${currencyFormatter.format(totalPayment.roundToLong())}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Loan Parameters & Currency Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Loan Parameters",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            // Currency Switch ($ vs €)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$", style = MaterialTheme.typography.bodyMedium, fontWeight = if (!isEuros) FontWeight.Bold else FontWeight.Normal)
                                Switch(
                                    checked = isEuros,
                                    onCheckedChange = { checked ->
                                        // Convert existing price and down payment values when switching currency
                                        val currentPrice = priceText.toDoubleOrNull() ?: 0.0
                                        val currentDown = downPaymentText.toDoubleOrNull() ?: 0.0
                                        if (checked) {
                                            priceText = if (currentPrice > 0) Utils.convertDollarToEuro(currentPrice.toInt()).toString() else ""
                                            downPaymentText = if (currentDown > 0) Utils.convertDollarToEuro(currentDown.toInt()).toString() else ""
                                        } else {
                                            priceText = if (currentPrice > 0) Utils.convertEuroToDollar(currentPrice.toInt()).toString() else ""
                                            downPaymentText = if (currentDown > 0) Utils.convertEuroToDollar(currentDown.toInt()).toString() else ""
                                        }
                                        isEuros = checked
                                    }
                                )
                                Text("€", style = MaterialTheme.typography.bodyMedium, fontWeight = if (isEuros) FontWeight.Bold else FontWeight.Normal)
                            }
                        }

                        // Property Price Input
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) priceText = it },
                            label = { Text("Property Price ($currencySymbol)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Down Payment Input
                        OutlinedTextField(
                            value = downPaymentText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) downPaymentText = it },
                            label = { Text("Down Payment ($currencySymbol)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Row for Interest Rate and Term Years
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = interestRateText,
                                onValueChange = { interestRateText = it },
                                label = { Text("Interest Rate (%)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = termYearsText,
                                onValueChange = { if (it.all { char -> char.isDigit() }) termYearsText = it },
                                label = { Text("Term (Years)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        }
    }
}
