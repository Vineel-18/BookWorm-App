package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.data.model.*
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartScreen(vm: AppViewModel, navController: NavController) {
    val cartItems    by vm.cart.collectAsState()
    val giftPoints   by vm.giftPoints.collectAsState()
    val address      by vm.address.collectAsState()
    val selDelivery  by vm.selDelivery.collectAsState()
    val isPremium    = vm.isPremium()
    val allBooks     = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }

    var couponInput        by remember { mutableStateOf("") }
    var couponDiscount     by remember { mutableIntStateOf(0) }
    var couponApplied      by remember { mutableStateOf("") }
    var giftRedeemed       by remember { mutableIntStateOf(0) }
    var showAddrSheet      by remember { mutableStateOf(false) }
    var toastMsg           by remember { mutableStateOf("") }
    val scope              = rememberCoroutineScope()

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    // Resolve cart books
    val cartBooks = cartItems.mapNotNull { ci ->
        val b = allBooks.find { it.id == ci.id }
        b?.let { Pair(it, ci.qty) }
    }

    val subtotal = cartBooks.sumOf { (b, qty) -> b.price * qty }
    val tax      = (subtotal * 0.12).toInt()
    val discount = (subtotal * 0.05).toInt()

    // Normalise delivery selection based on premium
    val effectiveDelivery = remember(isPremium, selDelivery) {
        if (isPremium && selDelivery !in listOf("prem_same", "prem_next")) "prem_same"
        else if (!isPremium && selDelivery in listOf("prem_same", "prem_next")) "standard"
        else selDelivery
    }

    val delivFee = resolvedDelivFee(effectiveDelivery, isPremium, subtotal)
    val total    = subtotal + tax - discount + delivFee - couponDiscount - giftRedeemed

    val COUPONS = buildMap<String, Int> {
        put("BOOK50",   50); put("BOOK100", 100); put("SAVE20", 20)
        if (isPremium) { put("PREM200", 200); put("PREM500", 500); put("WELCOME50", 50) }
    }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.CART, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cartItems.isEmpty()) {
                // Empty cart
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState(
                        icon = "🛒",
                        title = "Your cart is empty",
                        subtitle = "Add books to your cart to checkout.",
                        buttonText = "Browse Books",
                        onButton = { navController.navigate(Routes.HOME) }
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text("Shopping Cart", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 14.dp, top = 14.dp, bottom = 8.dp))
                    }

                    // Cart items
                    items(cartBooks) { (book, qty) ->
                        CartItemRow(
                            book     = book,
                            qty      = qty,
                            onQtyChange = { delta -> vm.changeQty(book.id, delta) },
                            onRemove    = { vm.removeFromCart(book.id); showToast("Item removed from cart") },
                            onClick     = { navController.navigate(Routes.bookDetail(book.id)) }
                        )
                    }

                    // You might also like
                    item {
                        val suggested = buildSuggested(cartBooks.map { it.first }, allBooks)
                        if (suggested.isNotEmpty()) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                Text("You might also like", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(suggested) { rec ->
                                        CartRecCard(
                                            book = rec,
                                            onClick = { navController.navigate(Routes.bookDetail(rec.id)) },
                                            onAddToCart = { vm.addToCart(rec.id); showToast("Added to cart ✓") }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Delivery address
                    item {
                        AddressCard(
                            address = address,
                            onEdit  = { showAddrSheet = true }
                        )
                    }

                    // Delivery options
                    item {
                        DeliveryOptionsCard(
                            isPremium      = isPremium,
                            subtotal       = subtotal,
                            selected       = effectiveDelivery,
                            onSelect       = { key ->
                                vm.selectDelivery(key)
                            }
                        )
                    }

                    // Order summary
                    item {
                        OrderSummaryCard(
                            subtotal       = subtotal,
                            tax            = tax,
                            discount       = discount,
                            delivFee       = delivFee,
                            couponDiscount = couponDiscount,
                            couponApplied  = couponApplied,
                            giftRedeemed   = giftRedeemed,
                            giftPoints     = giftPoints,
                            total          = total,
                            isPremium      = isPremium,
                            couponInput    = couponInput,
                            onCouponInput  = { couponInput = it },
                            onApplyCoupon  = {
                                val code = couponInput.trim().uppercase()
                                val off  = COUPONS[code]
                                if (off != null) {
                                    couponDiscount = off
                                    couponApplied  = code
                                    showToast("Coupon $code applied! ₹$off off ✓")
                                } else {
                                    showToast("Invalid coupon code")
                                }
                            },
                            onRedeemGift   = {
                                if (giftPoints > 0) {
                                    giftRedeemed = giftPoints
                                    vm.redeemGiftPoints()
                                    showToast("Gift points redeemed! ₹$giftPoints off ✓")
                                }
                            },
                            onProceedPay   = {
                                if (address == null || !address!!.isValid()) {
                                    showToast("Please add a delivery address first")
                                    showAddrSheet = true
                                } else {
                                    val totalStr = "₹$total"
                                    vm.proceedToPayment(totalStr, effectiveDelivery)
                                    navController.navigate(Routes.PAYMENT)
                                }
                            }
                        )
                    }

                    item { Spacer(Modifier.height(20.dp)) }
                }
            }

            // Address bottom sheet
            if (showAddrSheet) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showAddrSheet = false }
                )
                AddressSheet(
                    address   = address,
                    user      = vm.user.value,
                    onSave    = { addr -> vm.saveAddress(addr); showAddrSheet = false; showToast("Address saved ✓") },
                    onDismiss = { showAddrSheet = false }
                )
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Cart Item Row ────────────────────────────────────────────────────────────
@Composable
private fun CartItemRow(
    book: com.bookworm.app.data.model.Book,
    qty: Int,
    onQtyChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Cover
            BookCover(
                book     = book,
                modifier = Modifier.width(80.dp).height(105.dp).clickable { onClick() },
                titleFontSize = 7.sp
            )
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onClick() })
                Text("by ${book.author}", color = OrangeAccent, fontSize = 11.sp)
                Text(book.desc, color = MutedColor, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp)
                Text(book.format, color = MutedColor, fontSize = 10.sp)
                Text("₹${book.price}", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Text("Delivery by ${book.delivery}", color = MutedColor, fontSize = 10.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Qty controls
                    Surface(
                        color = Surface2Color,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("−", color = TextColor, fontSize = 16.sp, modifier = Modifier.clickable { onQtyChange(-1) }.padding(horizontal = 10.dp, vertical = 6.dp))
                            Text("$qty", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            Text("+", color = TextColor, fontSize = 16.sp, modifier = Modifier.clickable { onQtyChange(1) }.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                    Text("Remove", color = RedAccent, fontSize = 12.sp, modifier = Modifier.clickable { onRemove() })
                }
            }
        }
    }
}

// ─── Cart Rec Card ────────────────────────────────────────────────────────────
@Composable
private fun CartRecCard(book: com.bookworm.app.data.model.Book, onClick: () -> Unit, onAddToCart: () -> Unit) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.width(120.dp).clickable { onClick() }
    ) {
        Column {
            BookCover(book = book, modifier = Modifier.fillMaxWidth().height(150.dp), titleFontSize = 8.sp)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(book.title, color = TextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("by ${book.author}", color = OrangeAccent, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("₹${book.price}", color = TextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                Surface(
                    color = OrangeAccent.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp).clickable { onAddToCart() }
                ) {
                    Text("+ Cart", color = OrangeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

// ─── Address Card ─────────────────────────────────────────────────────────────
@Composable
private fun AddressCard(address: Address?, onEdit: () -> Unit) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("📍 Delivery Address", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (address != null) {
                    Text("✏️ Change", color = OrangeAccent, fontSize = 12.sp, modifier = Modifier.clickable { onEdit() })
                }
            }
            if (address != null) {
                Spacer(Modifier.height(8.dp))
                Surface(color = GreenAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                    Text("✓ Default Address", color = GreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("${address.first} ${address.last}", color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (address.oneLiner().isNotBlank()) Text(address.oneLiner(), color = MutedColor, fontSize = 12.sp)
                if (address.country.isNotBlank()) Text(address.country, color = MutedColor, fontSize = 12.sp)
                if (address.phone.isNotBlank())   Text("📞 ${address.phone}", color = MutedColor, fontSize = 12.sp)
                if (address.email.isNotBlank())   Text("✉️ ${address.email}", color = MutedColor, fontSize = 12.sp)
            } else {
                Spacer(Modifier.height(10.dp))
                Text("No address saved yet", color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
                BwButton(text = "+ Add Delivery Address", onClick = onEdit, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

// ─── Delivery Options Card ────────────────────────────────────────────────────
@Composable
private fun DeliveryOptionsCard(
    isPremium: Boolean,
    subtotal: Int,
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = if (isPremium) listOf("prem_same", "prem_next")
                  else           listOf("standard", "fast", "sameday")

    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚚 Delivery Speed", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (isPremium) {
                    Spacer(Modifier.width(6.dp))
                    Text("👑 All free for Premium", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            options.forEach { key ->
                val opt  = DELIVERY_OPTS[key] ?: return@forEach
                val isSel = key == selected
                val fee  = when {
                    isPremium          -> 0
                    key == "standard"  -> if (subtotal > 400) 0 else 40
                    else               -> opt.fee ?: 0
                }
                val feeLabel = if (fee == 0) "Free" else "+₹$fee"
                val borderCol = when {
                    isSel && isPremium -> GoldAccent
                    isSel              -> OrangeAccent
                    else               -> BorderColor
                }
                Surface(
                    color = if (isSel) Surface2Color else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onSelect(key) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio dot
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, if (isSel) OrangeAccent else BorderColor, CircleShape)
                                .padding(4.dp)
                        ) {
                            if (isSel) Box(modifier = Modifier.fillMaxSize().background(OrangeAccent, CircleShape))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(opt.label, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                opt.badge?.let { badge ->
                                    Spacer(Modifier.width(6.dp))
                                    val (bc, btxt) = when (badge) {
                                        "prem" -> Pair(GoldAccent, "👑 Perk")
                                        "fast" -> Pair(BlueAccent, "⚡ Fast")
                                        "same" -> Pair(GreenAccent,"🟢 Express")
                                        else   -> Pair(MutedColor, badge)
                                    }
                                    Surface(color = bc.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, bc.copy(alpha = 0.3f))) {
                                        Text(btxt, color = bc, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(opt.desc, color = MutedColor, fontSize = 11.sp)
                        }
                        Text(feeLabel, color = if (fee == 0) GreenAccent else TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Order Summary Card ───────────────────────────────────────────────────────
@Composable
private fun OrderSummaryCard(
    subtotal: Int, tax: Int, discount: Int, delivFee: Int,
    couponDiscount: Int, couponApplied: String,
    giftRedeemed: Int, giftPoints: Int,
    total: Int, isPremium: Boolean,
    couponInput: String, onCouponInput: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRedeemGift: () -> Unit,
    onProceedPay: () -> Unit
) {
    val cartQty = subtotal  // placeholder; actual qty is computed upstream

    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (isPremium) {
                Surface(
                    color = GoldAccent.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text("👑 Premium member · Express delivery included free", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp))
                }
            }

            Text("Order Summary", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 12.dp))

            SummaryRow("Price", "₹$subtotal")
            SummaryRow("Tax (12%)", "₹$tax")
            SummaryRow("Discount (5%)", "−₹$discount", valueColor = GreenAccent)
            SummaryRow("Delivery", if (delivFee == 0) "Free" else "₹$delivFee", valueColor = if (delivFee == 0) GreenAccent else TextColor)

            if (couponDiscount > 0) {
                SummaryRow("Coupon ($couponApplied)", "−₹$couponDiscount", valueColor = GreenAccent)
            }

            if (giftRedeemed > 0) {
                SummaryRow("Gift Points Redeemed", "−₹$giftRedeemed", valueColor = GreenAccent)
            }

            Spacer(Modifier.height(10.dp))

            // Coupon row
            if (isPremium) {
                Text("👑 Premium coupons: PREM200 (₹200) · PREM500 (₹500)", color = GoldAccent, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponInput,
                    onValueChange = onCouponInput,
                    placeholder = { Text("Coupon code", color = Color(0xFF444444), fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent, unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextColor, unfocusedTextColor = TextColor,
                        cursorColor = OrangeAccent, focusedContainerColor = Surface2Color, unfocusedContainerColor = Surface2Color
                    ),
                    modifier = Modifier.weight(1f).height(48.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextColor)
                )
                Button(
                    onClick = onApplyCoupon,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) { Text("Apply", fontSize = 13.sp) }
            }

            // Gift points redeem
            if (giftPoints > 0 && giftRedeemed == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gift Points ($giftPoints pts) ≈ ₹$giftPoints", color = MutedColor, fontSize = 12.sp)
                }
                Button(
                    onClick = onRedeemGift,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp).padding(bottom = 8.dp)
                ) {
                    Text("Redeem $giftPoints Gift Points", color = OrangeAccent, fontSize = 13.sp)
                }
            }

            // Total
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Text("₹${total.coerceAtLeast(0)}", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(14.dp))
            BwButton(text = "Pay Now →", onClick = onProceedPay)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = TextColor) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MutedColor, fontSize = 13.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Address Bottom Sheet ─────────────────────────────────────────────────────
@Composable
private fun AddressSheet(
    address: Address?,
    user: com.bookworm.app.data.model.User?,
    onSave: (Address) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf(address?.first  ?: (user?.name?.split(" ")?.firstOrNull() ?: "")) }
    var lastName  by remember { mutableStateOf(address?.last   ?: (user?.name?.split(" ")?.drop(1)?.joinToString(" ") ?: "")) }
    var street    by remember { mutableStateOf(address?.street ?: "") }
    var city      by remember { mutableStateOf(address?.city   ?: "") }
    var pin       by remember { mutableStateOf(address?.pin    ?: "") }
    var state     by remember { mutableStateOf(address?.state  ?: "") }
    var country   by remember { mutableStateOf(address?.country ?: "India") }
    var email     by remember { mutableStateOf(address?.email  ?: (user?.email ?: "")) }
    var phone     by remember { mutableStateOf(address?.phone  ?: "") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            color = Surface2Color,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF333333), RoundedCornerShape(2.dp)))
                }
                Text("Delivery Address", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 4.dp))
                Text(if (address != null) "Update your delivery address" else "Enter your delivery address",
                    color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(bottom = 16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BwTextField(value = firstName, onValueChange = { firstName = it }, label = "FIRST NAME", placeholder = "First Name", modifier = Modifier.weight(1f))
                    BwTextField(value = lastName,  onValueChange = { lastName  = it }, label = "LAST NAME",  placeholder = "Last Name",  modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                BwTextField(value = street, onValueChange = { street = it }, label = "STREET ADDRESS", placeholder = "Street address")
                Spacer(Modifier.height(12.dp))
                BwTextField(value = email, onValueChange = { email = it }, label = "EMAIL", placeholder = "you@example.com", keyboardType = KeyboardType.Email)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BwTextField(value = city, onValueChange = { city = it }, label = "CITY", placeholder = "City", modifier = Modifier.weight(1f))
                    BwTextField(value = pin,  onValueChange = { pin  = it }, label = "PIN CODE", placeholder = "000000", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                BwTextField(value = phone, onValueChange = { phone = it }, label = "PHONE NUMBER", placeholder = "98765 43210", keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BwTextField(value = state,   onValueChange = { state   = it }, label = "STATE",   placeholder = "State",  modifier = Modifier.weight(1f))
                    BwTextField(value = country, onValueChange = { country = it }, label = "COUNTRY", placeholder = "India",  modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                BwButton(
                    text = "Save Address",
                    color = OrangeAccent,
                    onClick = {
                        if (firstName.isBlank() || street.isBlank() || city.isBlank()) return@BwButton
                        onSave(Address(firstName, lastName, street, city, pin, state, country, email, phone))
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ─── Suggested books helper ───────────────────────────────────────────────────
private fun buildSuggested(cartBooks: List<com.bookworm.app.data.model.Book>, allBooks: List<com.bookworm.app.data.model.Book>): List<com.bookworm.app.data.model.Book> {
    val ids  = cartBooks.map { it.id }.toSet()
    val cats = cartBooks.flatMap { listOf(it.category) + it.genres }.toSet()
    val suggested = allBooks.filter { b -> b.id !in ids && (b.category in cats || b.genres.any { g -> g in cats }) }.take(6)
    val fallback  = allBooks.filter { b -> b.id !in ids && suggested.none { it.id == b.id } }
    return (suggested + fallback).take(6)
}
