package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(vm: AppViewModel, navController: NavController) {
    val total   by vm.checkoutTotal.collectAsState()
    var method  by remember { mutableStateOf("credit") }  // credit|debit|upi|wallet

    // Credit / Debit fields
    var cardNumber by remember { mutableStateOf("") }
    var cardName   by remember { mutableStateOf("") }
    var cvv        by remember { mutableStateOf("") }
    var expiry     by remember { mutableStateOf("") }

    // UPI
    var upiId      by remember { mutableStateOf("") }

    var toastMsg   by remember { mutableStateOf("") }
    val scope      = rememberCoroutineScope()
    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.PAYMENT, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // Floating book decorations (simplified)
            FloatingBooksDecor()

            // Payment modal
            Surface(
                color = Surface2Color,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Complete Payment", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Amount:", color = MutedColor, fontSize = 11.sp)
                            Text(total, color = OrangeAccent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Payment method tabs
                    val methods = listOf("credit" to "Credit Card", "debit" to "Debit Card", "upi" to "UPI", "wallet" to "Wallet")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        methods.forEach { (key, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (method == key) OrangeAccent else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { method = key }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (method == key) Color.White else MutedColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Method-specific fields
                    when (method) {
                        "credit", "debit" -> {
                            BwTextField(value = cardNumber, onValueChange = { input ->
                                val digits = input.replace("-","").replace(" ","").filter { it.isDigit() }.take(16)
                                cardNumber = digits.chunked(4).joinToString("-")
                            }, label = "CARD NUMBER", placeholder = "0000-0000-0000-0000", keyboardType = KeyboardType.Number)
                            Spacer(Modifier.height(10.dp))
                            BwTextField(value = cardName, onValueChange = { cardName = it }, label = "NAME ON CARD", placeholder = "Your Name")
                            Spacer(Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                BwTextField(value = cvv, onValueChange = { if (it.length <= 3) cvv = it.filter { c -> c.isDigit() } },
                                    label = "CVV", placeholder = "•••", isPassword = true, keyboardType = KeyboardType.NumberPassword, modifier = Modifier.weight(1f))
                                BwTextField(value = expiry, onValueChange = { input ->
                                    val digits = input.filter { c -> c.isDigit() }.take(6)
                                    expiry = if (digits.length > 2) digits.substring(0,2) + "/" + digits.substring(2) else digits
                                }, label = "EXPIRY", placeholder = "MM/YYYY", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                            }
                        }
                        "upi" -> {
                            BwTextField(value = upiId, onValueChange = { upiId = it }, label = "UPI ID", placeholder = "yourname@upi", keyboardType = KeyboardType.Email)
                        }
                        "wallet" -> {
                            Surface(color = Surface2Color, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Wallet Balance: ₹500", color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Your wallet balance will be debited for $total", color = MutedColor, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    BwButton(
                        text = "Pay Now →",
                        onClick = {
                            // Validation
                            when (method) {
                                "credit", "debit" -> {
                                    val digits = cardNumber.replace("-", "")
                                    if (digits.length < 16) { showToast("Please enter a valid 16-digit card number"); return@BwButton }
                                    if (cvv.length < 3) { showToast("Please enter CVV"); return@BwButton }
                                }
                                "upi" -> {
                                    if (!upiId.contains("@")) { showToast("Please enter a valid UPI ID"); return@BwButton }
                                }
                            }
                            vm.completePayment()
                            navController.navigate(Routes.CONFIRMATION) {
                                popUpTo(Routes.CART) { inclusive = true }
                            }
                        }
                    )
                }
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Floating book emoji decorations ─────────────────────────────────────────
@Composable
private fun FloatingBooksDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
        val books = listOf("📗" to (0.1f to 0.05f), "📘" to (0.85f to 0.08f), "📙" to (0.05f to 0.6f),
                          "📕" to (0.88f to 0.55f), "📚" to (0.45f to 0.02f), "📖" to (0.7f to 0.8f))
        books.forEach { (emoji, pos) ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (pos.first * 350).dp,
                        top   = (pos.second * 600).dp
                    )
            ) {
                Text(emoji, fontSize = 24.sp, color = Color.White.copy(alpha = 0.08f))
            }
        }
    }
}
