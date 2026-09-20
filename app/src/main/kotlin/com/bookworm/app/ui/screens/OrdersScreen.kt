package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
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
fun OrdersScreen(vm: AppViewModel, navController: NavController) {
    LaunchedEffect(Unit) { vm.syncOrderStatuses() }

    val rawOrders     by vm.orders.collectAsState()
    var toastMsg      by remember { mutableStateOf("") }
    var returnOrderId by remember { mutableStateOf<String?>(null) }
    val scope         = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) { delay(30_000); vm.syncOrderStatuses() }
    }

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.ORDERS, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (rawOrders.isEmpty()) {
                EmptyState("📦", "No orders yet", "Your completed orders will appear here.",
                    "Browse Books") { navController.navigate(Routes.HOME) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            "My Orders", color = TextColor, fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 10.dp)
                        )
                    }
                    items(rawOrders) { order ->
                        OrderCard(
                            order       = order,
                            onCancel    = { vm.cancelOrder(order.id); showToast("Order cancelled") },
                            onReturn    = { returnOrderId = order.id },
                            onBookClick = { navController.navigate(Routes.bookDetail(it)) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }

            // Return bottom sheet
            returnOrderId?.let { ordId ->
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f))
                    .clickable { returnOrderId = null })
                ReturnSheet(
                    onSubmit  = { reason, note ->
                        vm.requestReturn(ordId, reason, note)
                        returnOrderId = null
                        showToast("Return request submitted ✓")
                    },
                    onDismiss = { returnOrderId = null }
                )
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Order Card ───────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(
    order: Order,
    onCancel: () -> Unit,
    onReturn: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val liveStatus  = computeOrderStatus(order)
    val sLow        = liveStatus.lowercase()
    val isCancelled = sLow == "cancelled"
    val isRetReq    = sLow == "return requested"
    val isReturned  = sLow == "returned"
    val eligible    = isReturnEligible(order.copy(status = liveStatus))
    val canCancel   = !listOf("delivered","return requested","returned","cancelled").contains(sLow)
    val rawTotal    = order.total.replace("₹", "").trim()

    // Status badge colors — solid filled like reference photos
    val (badgeBg, badgeText) = when (sLow) {
        "delivered"        -> Pair(GreenAccent,          Color.White)
        "cancelled"        -> Pair(RedAccent,             Color.White)
        "return requested" -> Pair(Color(0xFF9B59B6),     Color.White)
        "returned"         -> Pair(Color(0xFF9B59B6),     Color.White)
        "shipped"          -> Pair(Color(0xFF2980B9),     Color.White)
        "packed"           -> Pair(OrangeAccent,          Color.White)
        else               -> Pair(OrangeAccent,          Color.White)
    }

    Surface(
        color  = SurfaceColor,
        shape  = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header row: order id + date + status badge ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Order #${order.id}", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Placed ${order.date}", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                }
                // Solid filled badge
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(liveStatus, color = badgeText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Book items ──────────────────────────────────────────────────
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookClick(item.id) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mini book cover — matching reference photos
                    val bg = remember(item.coverBg) {
                        try { Color(android.graphics.Color.parseColor(item.coverBg)) }
                        catch (e: Exception) { Color(0xFF333333) }
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 52.dp, height = 68.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(bg)
                            .padding(4.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                item.category.take(4).uppercase(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 5.sp, fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                item.title.uppercase(),
                                color = Color.White,
                                fontSize = 6.sp, fontWeight = FontWeight.ExtraBold,
                                lineHeight = 8.sp, maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                item.author.take(10).uppercase(),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 5.sp, fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 19.sp)
                        Text(item.author, color = OrangeAccent, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    Text("₹${item.price}", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // ── Tracker (not for cancelled/returned) ────────────────────────
            if (!listOf("cancelled","return requested","returned").contains(sLow)) {
                Spacer(Modifier.height(14.dp))
                OrderTracker(order = order, liveStatus = liveStatus)
            }

            // ── Footer: total + action button ───────────────────────────────
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total paid", color = MutedColor, fontSize = 12.sp)
                    Text("₹$rawTotal", color = TextColor, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
                if (!isCancelled && !isRetReq && !isReturned) {
                    if (canCancel) {
                        OutlinedButton(
                            onClick = onCancel,
                            border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) { Text("Cancel Order", fontSize = 13.sp) }
                    } else if (eligible) {
                        OutlinedButton(
                            onClick = onReturn,
                            border = BorderStroke(1.dp, Color(0xFF9B59B6).copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9B59B6)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) { Text("↩ Return", fontSize = 13.sp) }
                    }
                }
            }

            // ── Return status notes ─────────────────────────────────────────
            if (isRetReq) {
                val reqAt    = order.returnRequestedAt
                val doneAt   = if (reqAt > 0) reqAt + 2 * 60_000L else 0L
                val secsLeft = if (doneAt > 0) ((doneAt - System.currentTimeMillis()) / 1000).coerceAtLeast(0) else 0
                val eta = when {
                    secsLeft > 60 -> "Processing — refund in ~${secsLeft / 60}min"
                    secsLeft > 0  -> "Processing — refund in ~${secsLeft}s"
                    else          -> "Completing…"
                }
                Spacer(Modifier.height(10.dp))
                Surface(
                    color  = Color(0xFF9B59B6).copy(alpha = 0.08f),
                    shape  = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF9B59B6).copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("↩ Return requested on ${order.returnDate.ifBlank { "recently" }} · Reason: ${order.returnReason}",
                            color = Color(0xFF9B59B6), fontSize = 12.sp)
                        Text("$eta · ₹$rawTotal will be credited as gift points.", color = MutedColor, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            } else if (isReturned) {
                val credited = if (order.refundAmount > 0) "₹${order.refundAmount}" else "₹$rawTotal"
                Spacer(Modifier.height(10.dp))
                Surface(
                    color  = Color(0xFF9B59B6).copy(alpha = 0.08f),
                    shape  = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF9B59B6).copy(alpha = 0.2f))
                ) {
                    Text(
                        "✓ Returned on ${order.returnDate.ifBlank { "recently" }} · $credited credited as gift points to your account.",
                        color = Color(0xFF9B59B6), fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// ─── Order Tracker ────────────────────────────────────────────────────────────
@Composable
private fun OrderTracker(order: Order, liveStatus: String) {
    val curIdx     = stageIndex(liveStatus)
    val stageIcons = listOf("✓", "📦", "🚚", "📍", "🏠")

    Surface(
        color  = Color(0xFF161616),
        shape  = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {

            // EDD / Delivered line
            if (order.deliveredAt > 0) {
                val isPast  = System.currentTimeMillis() >= order.deliveredAt
                val eddDate = formatTs(order.deliveredAt)
                val prefix  = if (isPast) "Delivered on" else "Expected by"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text("$prefix  ", color = MutedColor, fontSize = 13.sp)
                    Text(eddDate, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    order.delivType.takeIf { it.isNotEmpty() }?.let { dt ->
                        Spacer(Modifier.width(8.dp))
                        val (bc, btxt) = when (dt) {
                            "fast"      -> Pair(BlueAccent,  "⚡ FAST")
                            "sameday"   -> Pair(GreenAccent, "🟢 SAME-DAY")
                            "prem_same" -> Pair(GoldAccent,  "👑 SAME-DAY")
                            "prem_next" -> Pair(GoldAccent,  "👑 NEXT-DAY")
                            else        -> return@let
                        }
                        Box(
                            modifier = Modifier
                                .background(bc.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .border(1.dp, bc.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(btxt, color = bc, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // Steps row with connecting lines
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                ORDER_STAGES.forEachIndexed { i, label ->
                    val isDone   = i < curIdx
                    val isActive = i == curIdx

                    val dotColor = when {
                        isDone   -> GreenAccent
                        isActive -> OrangeAccent
                        else     -> Color(0xFF2a2a2a)
                    }
                    val labelColor = when {
                        isDone || isActive -> TextColor
                        else               -> MutedColor
                    }
                    val ts = when (i) {
                        0 -> order.placedAt; 1 -> order.packedAt; 2 -> order.shippedAt
                        3 -> order.outAt;    4 -> order.deliveredAt; else -> 0L
                    }

                    // Step column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (isDone || isActive) dotColor else Color(0xFF1e1e1e),
                                    CircleShape
                                )
                                .border(2.dp, dotColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when {
                                    isDone   -> "✓"
                                    isActive -> stageIcons[i]
                                    else     -> ""
                                },
                                fontSize = 13.sp,
                                color = if (isDone || isActive) Color.White else Color.Transparent
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            label, color = labelColor, fontSize = 9.sp,
                            textAlign = TextAlign.Center, lineHeight = 12.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 2
                        )
                        if (ts > 0) {
                            Text(
                                formatTs(ts), color = if (isDone || isActive) GreenAccent else MutedColor,
                                fontSize = 9.sp, textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Connector line between steps
                    if (i < ORDER_STAGES.size - 1) {
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(2.dp)
                                .padding(top = 15.dp) // align with circle centre
                                .background(if (i < curIdx) GreenAccent else Color(0xFF2a2a2a))
                        )
                    }
                }
            }
        }
    }
}

private fun formatTs(ms: Long): String {
    if (ms <= 0) return ""
    val sdf = java.text.SimpleDateFormat("d MMM", java.util.Locale("en", "IN"))
    return sdf.format(java.util.Date(ms))
}

// ─── Return Bottom Sheet ──────────────────────────────────────────────────────
@Composable
private fun ReturnSheet(onSubmit: (String, String) -> Unit, onDismiss: () -> Unit) {
    var selected by remember { mutableStateOf("") }
    var note     by remember { mutableStateOf("") }

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
                Text("Return Request", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text("Select a reason for returning this order", color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp))

                RETURN_REASONS.forEach { reason ->
                    val isSel = reason == selected
                    Surface(
                        color  = if (isSel) OrangeAccent.copy(alpha = 0.1f) else SurfaceColor,
                        shape  = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSel) OrangeAccent else BorderColor),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selected = reason }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(18.dp).border(2.dp, if (isSel) OrangeAccent else BorderColor, CircleShape).padding(3.dp)
                            ) {
                                if (isSel) Box(modifier = Modifier.fillMaxSize().background(OrangeAccent, CircleShape))
                            }
                            Text(reason, color = if (isSel) TextColor else MutedColor, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Additional notes (optional)", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    placeholder = { Text("Describe the issue…", color = Color(0xFF444444), fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent, unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextColor, unfocusedTextColor = TextColor,
                        cursorColor = OrangeAccent, focusedContainerColor = Surface2Color, unfocusedContainerColor = Surface2Color
                    ),
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextColor)
                )
                Spacer(Modifier.height(16.dp))
                BwButton(
                    text = "Submit Return Request",
                    enabled = selected.isNotEmpty(),
                    onClick = { if (selected.isNotEmpty()) onSubmit(selected, note) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
