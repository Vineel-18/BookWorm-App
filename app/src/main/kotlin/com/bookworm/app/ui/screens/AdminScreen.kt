package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.data.BASE_BOOKS
import com.bookworm.app.data.CATEGORIES
import com.bookworm.app.data.BASE_STORES
import com.bookworm.app.data.model.Book
import com.bookworm.app.data.model.Order
import com.bookworm.app.data.model.computeOrderStatus
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminScreen(vm: AppViewModel, navController: NavController) {
    val user = vm.user.collectAsState().value

    // Only admins allowed
    LaunchedEffect(user) {
        if (user != null && user.role != "admin") navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
    }

    var currentPanel by remember { mutableStateOf("dashboard") }   // dashboard|books|orders|stores
    var toastMsg     by remember { mutableStateOf("") }
    val scope        = rememberCoroutineScope()
    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    // Book sheet state
    var showBookSheet by remember { mutableStateOf(false) }
    var editingBook   by remember { mutableStateOf<Book?>(null) }

    // Order sheet state
    var showOrderSheet by remember { mutableStateOf(false) }
    var editingOrderId by remember { mutableStateOf<String?>(null) }
    var editingOrderStatus by remember { mutableStateOf("") }

    val allOrders  by vm.orders.collectAsState()
    val allBooks   = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }

    // Admin navbar (no bottom app bar — uses its own)
    Scaffold(
        topBar = {
            Surface(color = NavBarColor, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).height(52.dp).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brand
                    Text("📚 Book Worm", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = RedAccent.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f))) {
                        Text("ADMIN", color = RedAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(color = Color.Transparent, shape = RoundedCornerShape(8.dp), modifier = Modifier.clickable { vm.logout() }) {
                        Text("⎋", fontSize = 18.sp, color = MutedColor, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        },
        bottomBar = {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderColor))
                Surface(color = NavBarColor, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).height(60.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AdminNavTab("📊", "Dashboard", currentPanel == "dashboard") { currentPanel = "dashboard" }
                        AdminNavTab("📚", "Books",     currentPanel == "books")     { currentPanel = "books";     showBookSheet = false }
                        AdminNavTab("📦", "Orders",    currentPanel == "orders")    { currentPanel = "orders" }
                        AdminNavTab("🏪", "Stores",    currentPanel == "stores")    { currentPanel = "stores" }
                        AdminNavTab("👥", "Users",     currentPanel == "users")     { currentPanel = "users" }
                    }
                }
            }
        },
        containerColor = BgColor,
        floatingActionButton = {
            if (currentPanel == "books") {
                FloatingActionButton(
                    onClick = { editingBook = null; showBookSheet = true },
                    containerColor = OrangeAccent,
                    contentColor = Color.White,
                    shape = CircleShape
                ) { Text("＋", fontSize = 24.sp) }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (currentPanel) {
                "dashboard" -> AdminDashboard(vm, allBooks, allOrders,
                    greeting = "Welcome, ${user?.name ?: "Admin"}")
                "books"     -> AdminBooks(vm, allBooks, { msg -> showToast(msg) },
                    onEdit = { book -> editingBook = book; showBookSheet = true },
                    onDelete = { bookId -> vm.adminDeleteBook(bookId); showToast("Book deleted") })
                "orders"    -> AdminOrders(allOrders, vm,
                    onEditStatus = { id, status -> editingOrderId = id; editingOrderStatus = status; showOrderSheet = true })
                "stores"    -> AdminStores(vm, navController)
                "users"     -> AdminUsers(vm)
            }

            // Book sheet overlay
            if (showBookSheet) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { showBookSheet = false })
                BookEditSheet(
                    book      = editingBook,
                    onSave    = { book -> vm.adminSaveBook(book); showBookSheet = false; showToast(if (editingBook == null) "Book added" else "Book updated") },
                    onDismiss = { showBookSheet = false }
                )
            }

            // Order status sheet overlay
            if (showOrderSheet) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { showOrderSheet = false })
                OrderStatusSheet(
                    orderId       = editingOrderId ?: "",
                    currentStatus = editingOrderStatus,
                    onSave        = { id, status -> vm.adminUpdateOrderStatus(id, status); showOrderSheet = false; showToast("Status updated") },
                    onDismiss     = { showOrderSheet = false }
                )
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Admin Dashboard ──────────────────────────────────────────────────────────
@Composable
private fun AdminDashboard(vm: AppViewModel, allBooks: List<Book>, allOrders: List<Order>, greeting: String) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Dashboard", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 14.dp, top = 14.dp))
            Text(greeting, color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp, bottom = 14.dp))
        }
        item {
            // Stats grid
            val totalSales    = allOrders.sumOf { o -> o.items.sumOf { it.qty } }
            val totalRevenue  = allOrders.sumOf { o -> o.total.replace("₹","").replace(",","").trim().toIntOrNull() ?: 0 }
            val activeOrders  = allOrders.count { computeOrderStatus(it) !in listOf("Delivered","Cancelled","Returned") }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("📚", "${allBooks.size}", "Books",   Modifier.weight(1f))
                StatCard("📦", "$activeOrders",   "Active",  Modifier.weight(1f))
                StatCard("💰", "₹$totalRevenue",  "Revenue", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }
        item { Text("Recent Orders", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 14.dp, bottom = 8.dp)) }
        items(allOrders.take(5)) { order ->
            AdminOrderCard(order = order, onEditStatus = {}, isAdmin = false)
        }
        item { Text("Top Books", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 14.dp, top = 16.dp, bottom = 8.dp)) }
        items(allBooks.sortedByDescending { it.sales }.take(5)) { book ->
            AdminBookCardSmall(book = book)
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ─── Admin Books panel ────────────────────────────────────────────────────────
@Composable
private fun AdminBooks(vm: AppViewModel, allBooks: List<Book>, showToast: (String) -> Unit, onEdit: (Book) -> Unit, onDelete: (Int) -> Unit) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(allBooks, search) {
        if (search.isBlank()) allBooks
        else allBooks.filter { it.title.contains(search, true) || it.author.contains(search, true) }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Books", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 14.dp, top = 14.dp))
            Text("Manage your catalogue", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
        }
        item {
            // Search
            Surface(color = Surface2Color, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor), modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("🔍", fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = search, onValueChange = { search = it }, singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextColor, fontSize = 14.sp),
                        decorationBox = { inner -> if (search.isEmpty()) Text("Search books or authors…", color = Color(0xFF444444), fontSize = 14.sp); inner() },
                        modifier = Modifier.fillMaxWidth().height(44.dp).padding(vertical = 12.dp),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(OrangeAccent)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        items(filtered) { book ->
            AdminBookCardFull(book = book, onEdit = { onEdit(book) }, onDelete = { onDelete(book.id) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── Admin Orders panel ───────────────────────────────────────────────────────
@Composable
private fun AdminOrders(orders: List<Order>, vm: AppViewModel, onEditStatus: (String, String) -> Unit) {
    var search       by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("") }

    val statuses = listOf("", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")
    val filtered = remember(orders, search, statusFilter) {
        orders.filter { o ->
            if (search.isNotBlank() && !o.id.contains(search, true)) return@filter false
            if (statusFilter.isNotBlank() && computeOrderStatus(o) != statusFilter) return@filter false
            true
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Orders", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 14.dp, top = 14.dp))
            Text("View and update all orders", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
        }
        item {
            Surface(color = Surface2Color, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor), modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("🔍", fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                    androidx.compose.foundation.text.BasicTextField(value = search, onValueChange = { search = it }, singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextColor, fontSize = 14.sp),
                        decorationBox = { inner -> if (search.isEmpty()) Text("Search by order ID…", color = Color(0xFF444444), fontSize = 14.sp); inner() },
                        modifier = Modifier.fillMaxWidth().height(44.dp).padding(vertical = 12.dp),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(OrangeAccent))
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statuses) { s -> FilterChip(if (s.isEmpty()) "All" else s, s == statusFilter) { statusFilter = s } }
            }
            Spacer(Modifier.height(8.dp))
        }
        items(filtered) { order ->
            AdminOrderCard(order = order, onEditStatus = { onEditStatus(order.id, computeOrderStatus(order)) }, isAdmin = true)
        }
        if (filtered.isEmpty()) { item { EmptyState("📦","No orders found","Try a different filter.",null,null) } }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ─── Admin Stores panel ───────────────────────────────────────────────────────
@Composable
private fun AdminStores(vm: AppViewModel, navController: NavController) {
    val allBooks = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Stores", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 14.dp, top = 14.dp))
            Text("3 curated stores · 45 books distributed", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
        }
        items(BASE_STORES) { store ->
            Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp).clickable { navController.navigate(Routes.STORES) }) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val bg = try { Color(android.graphics.Color.parseColor(store.coverColor)) } catch (e: Exception) { BgColor }
                        Box(modifier = Modifier.size(48.dp).background(bg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Text(store.emoji, fontSize = 22.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(store.name, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(store.tagline, color = MutedColor, fontSize = 12.sp)
                            Text("${store.bookIds.size} books · ${store.location}", color = MutedColor, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        Text("★ ${store.rating}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AdmPill("${store.totalSales} sales", "pill-blue")
                            AdmPill("Est. ${store.founded}", "pill-green")
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ─── Admin Users panel ────────────────────────────────────────────────────────
@Composable
private fun AdminUsers(vm: AppViewModel) {
    val user  = vm.user.collectAsState().value
    val orders = vm.orders.collectAsState().value
    val wl     = vm.wishlist.collectAsState().value
    val gift   = vm.giftPoints.collectAsState().value
    val prem   = vm.isPremium()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Users", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 14.dp, top = 14.dp))
            Text("Current session data", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
        }
        if (user != null) {
            item {
                Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(44.dp).background(OrangeAccent, CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.name.take(1).uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(user.name, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(user.email.ifBlank { "No email" }, color = MutedColor, fontSize = 11.sp)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AdmPill(user.role, "pill-blue")
                                if (prem) AdmPill("👑 Premium", "pill-orange")
                                AdmPill("${orders.size} orders", "pill-green")
                                AdmPill("$gift pts", "pill-orange")
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ─── Admin Book Card (full) ───────────────────────────────────────────────────
@Composable
private fun AdminBookCardFull(book: Book, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp)) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            val bg = try { Color(android.graphics.Color.parseColor(book.coverBg)) } catch (e: Exception) { Color(0xFF333333) }
            Box(modifier = Modifier.size(48.dp).background(bg, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                Text(book.category.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(book.author, color = OrangeAccent, fontSize = 11.sp)
                Text("₹${book.price} · ${book.category} · ${book.format}", color = MutedColor, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    book.tags.forEach { tag ->
                        val (tc, bg2) = when(tag) {
                            "new" -> Pair(GreenAccent, GreenAccent.copy(alpha = 0.12f))
                            "recommended" -> Pair(BlueAccent, BlueAccent.copy(alpha = 0.12f))
                            "bestseller"  -> Pair(OrangeAccent, OrangeAccent.copy(alpha = 0.12f))
                            else -> Pair(MutedColor, MutedColor.copy(alpha = 0.12f))
                        }
                        Surface(color = bg2, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, tc.copy(alpha = 0.25f))) {
                            Text(tag, color = tc, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = BlueAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.3f)), modifier = Modifier.clickable { onEdit() }) {
                        Text("✏️ Edit", color = BlueAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                    Surface(color = RedAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f)), modifier = Modifier.clickable { onDelete() }) {
                        Text("🗑 Delete", color = RedAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}

// ─── Admin Book Card (small — used in dashboard) ──────────────────────────────
@Composable
private fun AdminBookCardSmall(book: Book) {
    Surface(color = SurfaceColor, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val bg = try { Color(android.graphics.Color.parseColor(book.coverBg)) } catch (e: Exception) { Color(0xFF333333) }
            Box(modifier = Modifier.size(36.dp).background(bg, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                Text(book.category.take(1), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, color = TextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(book.author, color = OrangeAccent, fontSize = 11.sp)
            }
            Text("${book.sales} sold", color = MutedColor, fontSize = 11.sp)
        }
    }
}

// ─── Admin Order Card ─────────────────────────────────────────────────────────
@Composable
private fun AdminOrderCard(order: Order, onEditStatus: () -> Unit, isAdmin: Boolean) {
    val liveStatus = computeOrderStatus(order)
    val statusColor = when (liveStatus.lowercase()) {
        "delivered" -> GreenAccent; "cancelled" -> RedAccent
        "shipped","out for delivery" -> OrangeAccent
        else -> BlueAccent
    }
    Surface(color = SurfaceColor, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("#${order.id}", color = MutedColor, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    Text(order.date, color = MutedColor, fontSize = 11.sp)
                }
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))) {
                    Text(liveStatus, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(order.items.joinToString(", ") { it.title }, color = MutedColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(order.total, color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (isAdmin) {
                    Surface(color = BlueAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.3f)), modifier = Modifier.clickable { onEditStatus() }) {
                        Text("Update Status", color = BlueAccent, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                }
            }
        }
    }
}

// ─── Admin Stat Card ──────────────────────────────────────────────────────────
@Composable
private fun StatCard(icon: String, value: String, label: String, modifier: Modifier) {
    Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor), modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, color = TextColor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 26.sp)
            Text(label, color = MutedColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp)
        }
    }
}

@Composable
private fun AdmPill(text: String, type: String) {
    val color = when (type) {
        "pill-green"  -> GreenAccent
        "pill-red"    -> RedAccent
        "pill-orange" -> OrangeAccent
        "pill-blue"   -> BlueAccent
        else          -> MutedColor
    }
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.25f))) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

@Composable
private fun AdminNavTab(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) OrangeAccent else MutedColor
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Text(label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}

// ─── Book Edit Sheet ──────────────────────────────────────────────────────────
@Composable
private fun BookEditSheet(book: Book?, onSave: (Book) -> Unit, onDismiss: () -> Unit) {
    var title       by remember { mutableStateOf(book?.title ?: "") }
    var author      by remember { mutableStateOf(book?.author ?: "") }
    var price       by remember { mutableStateOf(book?.price?.toString() ?: "") }
    var origPrice   by remember { mutableStateOf(book?.originalPrice?.toString() ?: "") }
    var category    by remember { mutableStateOf(book?.category ?: "Self-help") }
    var format      by remember { mutableStateOf(book?.format ?: "Paperback") }
    var stock       by remember { mutableStateOf(book?.stock?.toString() ?: "") }
    var rating      by remember { mutableStateOf(book?.rating?.toString() ?: "") }
    var coverBg     by remember { mutableStateOf(book?.coverBg ?: "#3b82f6") }
    var desc        by remember { mutableStateOf(book?.desc ?: "") }
    var tags        by remember { mutableStateOf(book?.tags?.joinToString(", ") ?: "") }
    var publisher   by remember { mutableStateOf(book?.publisher ?: "") }

    val cats = CATEGORIES.drop(1)   // remove "All"
    val fmts = listOf("Paperback","Hard Cover","eBook","Audiobook")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(color = Surface2Color, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF333333), RoundedCornerShape(2.dp)))
                }
                Text(if (book == null) "Add Book" else "Edit Book", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 16.dp))

                BwTextField(value = title, onValueChange = { title = it }, label = "TITLE", placeholder = "Book title")
                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BwTextField(value = author, onValueChange = { author = it }, label = "AUTHOR", placeholder = "Author name", modifier = Modifier.weight(1f))
                    BwTextField(value = price, onValueChange = { price = it }, label = "PRICE (₹)", placeholder = "299", keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CATEGORY", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 5.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(cats) { c -> FilterChip(c, c == category) { category = c } }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FORMAT", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 5.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            fmts.forEach { f -> FilterChip(f, f == format) { format = f } }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BwTextField(value = origPrice, onValueChange = { origPrice = it }, label = "ORIG. PRICE (₹)", placeholder = "499", keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, modifier = Modifier.weight(1f))
                    BwTextField(value = stock, onValueChange = { stock = it }, label = "STOCK", placeholder = "50", keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BwTextField(value = rating, onValueChange = { rating = it }, label = "RATING (0–5)", placeholder = "4.2", keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    BwTextField(value = coverBg, onValueChange = { coverBg = it }, label = "COVER COLOUR", placeholder = "#3b82f6", modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))

                Column {
                    Text("DESCRIPTION", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 5.dp))
                    OutlinedTextField(
                        value = desc, onValueChange = { desc = it }, placeholder = { Text("Short description…", color = Color(0xFF444444), fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeAccent, unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextColor, unfocusedTextColor = TextColor, cursorColor = OrangeAccent,
                            focusedContainerColor = Surface2Color, unfocusedContainerColor = Surface2Color),
                        modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextColor)
                    )
                }
                Spacer(Modifier.height(10.dp))

                BwTextField(value = tags, onValueChange = { tags = it }, label = "TAGS", placeholder = "recommended, bestseller, new")
                Spacer(Modifier.height(10.dp))
                BwTextField(value = publisher, onValueChange = { publisher = it }, label = "PUBLISHER", placeholder = "Publisher")
                Spacer(Modifier.height(18.dp))

                BwButton(text = "Save Book", onClick = {
                    if (title.isBlank() || author.isBlank() || price.isBlank()) return@BwButton
                    val sdf = SimpleDateFormat("EEE, d MMM", Locale("en","IN"))
                    val cal = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, 4) }
                    val delivery = sdf.format(cal.time)
                    val newBook = Book(
                        id             = book?.id ?: (System.currentTimeMillis().toInt().and(0xFFFFF) + 1000),
                        title          = title.trim(),
                        author         = author.trim(),
                        category       = category,
                        genres         = listOf(category),
                        format         = format,
                        language       = "English",
                        price          = price.toIntOrNull() ?: 0,
                        originalPrice  = origPrice.toIntOrNull() ?: (price.toIntOrNull() ?: 0),
                        rating         = rating.toDoubleOrNull() ?: 4.0,
                        sales          = book?.sales ?: 0,
                        coverBg        = coverBg.ifBlank { "#3b82f6" },
                        coverText      = title.uppercase(),
                        coverAuthorDisplay = author.uppercase(),
                        desc           = desc.trim(),
                        delivery       = book?.delivery ?: delivery,
                        tags           = tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        stock          = stock.toIntOrNull() ?: 50,
                        brand          = publisher.trim(),
                        publisher      = publisher.trim()
                    )
                    onSave(newBook)
                })
                Spacer(Modifier.height(10.dp))
                BwOutlineButton(text = "Cancel", onClick = onDismiss)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─── Order Status Sheet ───────────────────────────────────────────────────────
@Composable
private fun OrderStatusSheet(orderId: String, currentStatus: String, onSave: (String, String) -> Unit, onDismiss: () -> Unit) {
    var selected by remember { mutableStateOf(currentStatus) }
    val statuses = listOf("Confirmed","Processing","Shipped","Delivered","Cancelled")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(color = Surface2Color, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF333333), RoundedCornerShape(2.dp)))
                }
                Text("Update Order Status", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 4.dp))
                Text("Order #$orderId", color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))

                Text("NEW STATUS", color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statuses.forEach { s ->
                        Surface(
                            color = if (s == selected) OrangeAccent.copy(alpha = 0.1f) else SurfaceColor,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (s == selected) OrangeAccent else BorderColor),
                            modifier = Modifier.fillMaxWidth().clickable { selected = s }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.size(18.dp).border(2.dp, if (s == selected) OrangeAccent else BorderColor, CircleShape).padding(3.dp)) {
                                    if (s == selected) Box(modifier = Modifier.fillMaxSize().background(OrangeAccent, CircleShape))
                                }
                                Text(s, color = if (s == selected) TextColor else MutedColor, fontSize = 13.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                BwButton(text = "Update Status", onClick = { onSave(orderId, selected) })
                Spacer(Modifier.height(10.dp))
                BwOutlineButton(text = "Cancel", onClick = onDismiss)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
