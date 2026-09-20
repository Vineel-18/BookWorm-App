package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.data.model.PremiumPending
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─── Premium Plans Screen ─────────────────────────────────────────────────────
@Composable
fun PremiumScreen(vm: AppViewModel, navController: NavController) {
    val premInfo   = vm.premium.collectAsState().value
    val isPremium  = vm.isPremium()
    var selectedPlan by remember { mutableStateOf("annual") }
    var toastMsg   by remember { mutableStateOf("") }
    val scope      = rememberCoroutineScope()
    val clipboard  = LocalClipboardManager.current

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    val PLANS = mapOf(
        "monthly"  to PremiumPending("monthly",  "Monthly",  "₹149/mo",  30),
        "annual"   to PremiumPending("annual",   "Annual",   "₹999/yr",  365),
        "lifetime" to PremiumPending("lifetime", "Lifetime", "₹2,499",   99999)
    )

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.PREMIUM, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color(0xFF1a1200), Color(0xFF2d1f00), BgColor)
                            )
                        )
                        .border(BorderStroke(1.dp, Color(0xFF3d2e00)))
                        .padding(horizontal = 18.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👑", fontSize = 48.sp)
                        Spacer(Modifier.height(10.dp))
                        // Gradient text
                        Box {
                            Text(
                                "Book Worm Premium",
                                fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                                style = androidx.compose.ui.text.TextStyle(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(GoldAccent, OrangeAccent)
                                    )
                                )
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Unlock exclusive books, free delivery on every order,\nand members-only coupons.",
                            color = MutedColor, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp
                        )
                    }
                }

                // Active plan banner
                if (isPremium && premInfo != null) {
                    val expDate = if (premInfo.days >= 99999) "Lifetime"
                    else SimpleDateFormat("d MMM yyyy", Locale("en","IN")).format(Date(premInfo.expiry))
                    Surface(
                        color = GreenAccent.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GreenAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("👑", fontSize = 28.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Premium Active", color = GreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${premInfo.label} plan · ${premInfo.price}" +
                                    if (premInfo.plan != "lifetime") " · Renews $expDate" else " · Never expires",
                                    color = MutedColor, fontSize = 12.sp
                                )
                            }
                            Surface(
                                color = RedAccent.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.clickable { vm.cancelPremium(); showToast("Premium cancelled.") }
                            ) {
                                Text("Cancel", color = RedAccent, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                    }
                }

                // Perks
                PremSectionTitle("WHAT YOU GET")
                val perks = listOf(
                    Triple("🚀", "Free Delivery — Always",      "No minimum order. Every order ships free."),
                    Triple("🔐", "Premium Books",               "Access 15+ exclusive titles unavailable to free users."),
                    Triple("🎟️", "Members-Only Coupons",        "Unlock PREM200, PREM500 & monthly surprise codes."),
                    Triple("⚡", "Early Access",                "Shop new launches 48 hours before everyone else."),
                    Triple("💎", "Priority Support",            "Skip the queue — get help within 2 hours.")
                )
                Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    perks.forEach { (icon, title, sub) -> PerkRow(icon, title, sub) }
                }

                // Plan cards
                PremSectionTitle("CHOOSE YOUR PLAN")
                Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlanCard(
                        id = "monthly", name = "Monthly", price = "₹149", period = "/ month",
                        oldPrice = null, saveBadge = null,
                        features = listOf("All premium perks", "Cancel anytime", "Billed monthly"),
                        isPopular = false, isSelected = selectedPlan == "monthly",
                        enabled = !isPremium
                    ) { selectedPlan = "monthly" }

                    PlanCard(
                        id = "annual", name = "Annual", price = "₹999", period = "/ year",
                        oldPrice = "₹1,788", saveBadge = "Save 44%",
                        features = listOf("All premium perks", "2 months free", "Best value"),
                        isPopular = true, isSelected = selectedPlan == "annual",
                        enabled = !isPremium
                    ) { selectedPlan = "annual" }

                    PlanCard(
                        id = "lifetime", name = "Lifetime", price = "₹2,499", period = " one-time",
                        oldPrice = "₹4,999", saveBadge = "Save 50%",
                        features = listOf("All premium perks forever", "Pay once, never again", "Includes future perks"),
                        isPopular = false, isSelected = selectedPlan == "lifetime",
                        enabled = !isPremium
                    ) { selectedPlan = "lifetime" }
                }

                // CTA
                if (!isPremium) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                        BwButton(
                            text = "Get Premium →",
                            color = GoldAccent,
                            onClick = {
                                val pending = PLANS[selectedPlan] ?: return@BwButton
                                vm.savePremiumPending(pending)
                                navController.navigate(Routes.PREM_PAYMENT)
                            }
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("Secure payment · Cancel anytime · Instant activation",
                            color = MutedColor, fontSize = 11.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }
                }

                // Exclusive coupons
                PremSectionTitle("YOUR EXCLUSIVE COUPONS")
                Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val coupons = listOf(
                        Triple("PREM200",   "₹200 off on orders above ₹799",  "Valid this month"),
                        Triple("PREM500",   "₹500 off on orders above ₹1999", "Valid till Dec 2025"),
                        Triple("FREESHIP",  "Free delivery — always applied",  "Auto-applied"),
                        Triple("WELCOME50", "50% off your next order",         "One-time use")
                    )
                    coupons.forEach { (code, desc, valid) ->
                        CouponCard(
                            code = code, desc = desc, valid = valid,
                            locked = !isPremium,
                            onCopy = if (isPremium) { { clipboard.setText(AnnotatedString(code)); showToast("Coupon $code copied! ✓") } } else null
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Helper composables ───────────────────────────────────────────────────────
@Composable
private fun PremSectionTitle(text: String) {
    Text(text, color = MutedColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 18.dp, top = 18.dp, bottom = 8.dp))
}

@Composable
private fun PerkRow(icon: String, title: String, sub: String) {
    Surface(color = SurfaceColor, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, BorderColor)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(icon, fontSize = 26.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(sub, color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Surface(color = GoldAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))) {
                Text("PERK", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
        }
    }
}

@Composable
private fun PlanCard(
    id: String, name: String, price: String, period: String,
    oldPrice: String?, saveBadge: String?,
    features: List<String>,
    isPopular: Boolean, isSelected: Boolean, enabled: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = when { isSelected && isPopular -> GoldAccent; isSelected -> OrangeAccent; isPopular -> GoldAccent; else -> BorderColor }
    Surface(
        color = if (isPopular) Color(0xFF1c1600) else SurfaceColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled) { onSelect() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            if (isPopular) {
                Surface(color = GoldAccent, shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp), modifier = Modifier.align(Alignment.End).padding(end = 18.dp)) {
                    Text("⭐ Most Popular", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                // Radio
                Box(modifier = Modifier.size(20.dp).border(2.dp, if (isSelected) OrangeAccent else BorderColor, CircleShape).padding(4.dp)) {
                    if (isSelected) Box(modifier = Modifier.fillMaxSize().background(OrangeAccent, CircleShape))
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, color = TextColor, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(period, color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
                if (oldPrice != null) {
                    Text(oldPrice, color = MutedColor, fontSize = 12.sp,
                        style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                        modifier = Modifier.padding(start = 6.dp, bottom = 3.dp))
                }
                if (saveBadge != null) {
                    Surface(color = GreenAccent.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, GreenAccent.copy(alpha = 0.3f)), modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)) {
                        Text(saveBadge, color = GreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            features.forEach { f ->
                Row(modifier = Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✓ ", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(f, color = MutedColor, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CouponCard(code: String, desc: String, valid: String, locked: Boolean, onCopy: (() -> Unit)?) {
    Surface(
        color = if (locked) SurfaceColor.copy(alpha = 0.5f) else SurfaceColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (locked) BorderColor.copy(alpha = 0.3f) else Color(0xFF3a2e00)),
        modifier = Modifier.fillMaxWidth().alpha(if (locked) 0.5f else 1f)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(color = GoldAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))) {
                Text(code, color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(desc, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(if (locked) "🔒 Subscribe to unlock" else valid, color = MutedColor, fontSize = 11.sp)
            }
            if (onCopy != null) {
                Surface(color = Color.Transparent, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, BorderColor), modifier = Modifier.clickable { onCopy() }) {
                    Text("Copy", color = MutedColor, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
        }
    }
}

// ─── Premium Payment Screen ───────────────────────────────────────────────────
@Composable
fun PremiumPaymentScreen(vm: AppViewModel, navController: NavController) {
    val pending by vm.premiumPending.collectAsState()
    var method  by remember { mutableStateOf("credit") }
    var cardNum by remember { mutableStateOf("") }
    var cardNm  by remember { mutableStateOf("") }
    var cvv     by remember { mutableStateOf("") }
    var expiry  by remember { mutableStateOf("") }
    var upiId   by remember { mutableStateOf("") }
    var toastMsg by remember { mutableStateOf("") }
    val scope   = rememberCoroutineScope()

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController, showBack = true) },
        bottomBar = { BookWormBottomNav(navController, Routes.PREMIUM, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Surface(
                color = Surface2Color,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Premium Payment", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(6.dp))
                    pending?.let {
                        Text("${it.label} plan — ${it.price}", color = OrangeAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(16.dp))

                    // Method tabs
                    val methods = listOf("credit" to "Credit", "debit" to "Debit", "upi" to "UPI", "wallet" to "Wallet")
                    Row(modifier = Modifier.fillMaxWidth().background(SurfaceColor, RoundedCornerShape(8.dp)).border(1.dp, BorderColor, RoundedCornerShape(8.dp)).padding(3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        methods.forEach { (key, label) ->
                            Box(modifier = Modifier.weight(1f).background(if (method == key) OrangeAccent else Color.Transparent, RoundedCornerShape(6.dp)).clickable { method = key }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(label, color = if (method == key) Color.White else MutedColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    when (method) {
                        "credit","debit" -> {
                            BwTextField(value = cardNum, onValueChange = { input ->
                                val d = input.replace("-","").filter { it.isDigit() }.take(16)
                                cardNum = d.chunked(4).joinToString("-")
                            }, label = "CARD NUMBER", placeholder = "0000-0000-0000-0000", keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            Spacer(Modifier.height(10.dp))
                            BwTextField(value = cardNm, onValueChange = { cardNm = it }, label = "NAME ON CARD", placeholder = "Your Name")
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                BwTextField(value = cvv, onValueChange = { if (it.length <= 3) cvv = it.filter { c -> c.isDigit() } },
                                    label = "CVV", placeholder = "•••", isPassword = true,
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
                                    modifier = Modifier.weight(1f))
                                BwTextField(value = expiry, onValueChange = { input ->
                                    val d = input.filter { it.isDigit() }.take(6)
                                    expiry = if (d.length > 2) d.substring(0,2) + "/" + d.substring(2) else d
                                }, label = "EXPIRY", placeholder = "MM/YYYY",
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                                    modifier = Modifier.weight(1f))
                            }
                        }
                        "upi"    -> BwTextField(value = upiId, onValueChange = { upiId = it }, label = "UPI ID", placeholder = "yourname@upi")
                        "wallet" -> {
                            Surface(color = Surface2Color, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Wallet Balance: ₹500", color = TextColor, fontSize = 13.sp)
                                    Text("Wallet will be charged for ${pending?.price ?: "₹0"}", color = MutedColor, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    BwButton(text = "Activate Premium →", color = GoldAccent, onClick = {
                        when (method) {
                            "credit","debit" -> {
                                if (cardNum.replace("-","").length < 16) { showToast("Enter a valid card number"); return@BwButton }
                                if (cvv.length < 3) { showToast("Enter CVV"); return@BwButton }
                            }
                            "upi" -> { if (!upiId.contains("@")) { showToast("Enter a valid UPI ID"); return@BwButton } }
                        }
                        pending?.let { vm.activatePremium(it) }
                        navController.navigate(Routes.PREM_CONFIRM) { popUpTo(Routes.PREMIUM) { inclusive = false } }
                    })
                }
            }
            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Premium Confirmation Screen ──────────────────────────────────────────────
@Composable
fun PremiumConfirmationScreen(vm: AppViewModel, navController: NavController) {
    val premInfo = vm.premium.collectAsState().value

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.PREMIUM, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Surface(
                color = Surface2Color, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👑", fontSize = 52.sp)
                    Spacer(Modifier.height(14.dp))
                    Text("Welcome to Premium!", color = GoldAccent, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        premInfo?.let { "You're now on the ${it.label} plan — ${it.price}" } ?: "Your premium subscription is now active.",
                        color = MutedColor, fontSize = 13.sp, textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    BwButton(text = "Start Enjoying Premium →", color = GoldAccent, onClick = {
                        navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                    })
                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = { navController.navigate(Routes.PREMIUM) }) {
                        Text("View Premium Page", color = OrangeAccent, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
