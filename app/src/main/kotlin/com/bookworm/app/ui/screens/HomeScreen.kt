package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
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
import com.bookworm.app.data.model.Book
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(vm: AppViewModel, navController: NavController) {
    val allBooks   = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }
    val orders     by vm.orders.collectAsState()

    // Filter state
    var search     by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("All") }
    var filterLang    by remember { mutableStateOf("") }
    var filterFormat  by remember { mutableStateOf("") }
    var filterPrice   by remember { mutableStateOf("") }
    var filterSort    by remember { mutableStateOf("") }
    var showFilters   by remember { mutableStateOf(false) }

    // Temp filter state (shown in sheet before applying)
    var tempLang    by remember { mutableStateOf("") }
    var tempFormat  by remember { mutableStateOf("") }
    var tempPrice   by remember { mutableStateOf("") }
    var tempSort    by remember { mutableStateOf("") }

    var toastMsg    by remember { mutableStateOf("") }
    val scope       = rememberCoroutineScope()

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    // Compute filtered books
    val filtered = remember(allBooks, search, activeCategory, filterLang, filterFormat, filterPrice, filterSort) {
        var books = allBooks.filter { b ->
            if (activeCategory != "All" && b.category != activeCategory) return@filter false
            if (filterLang.isNotEmpty()   && b.language != filterLang)   return@filter false
            if (filterFormat.isNotEmpty() && b.format   != filterFormat) return@filter false
            if (filterPrice.isNotEmpty()) {
                val parts = filterPrice.split("-").mapNotNull { it.toIntOrNull() }
                if (parts.size == 2 && (b.price < parts[0] || b.price > parts[1])) return@filter false
            }
            if (search.isNotBlank()) {
                val q = search.lowercase()
                if (!b.title.lowercase().contains(q) && !b.author.lowercase().contains(q)) return@filter false
            }
            true
        }
        when (filterSort) {
            "price-asc"  -> books = books.sortedBy { it.price }
            "price-desc" -> books = books.sortedByDescending { it.price }
            "rating"     -> books = books.sortedByDescending { it.rating }
        }
        books
    }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.HOME, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Search bar
                item {
                    SearchBar(
                        query     = search,
                        onQuery   = { search = it },
                        onFilter  = {
                            tempLang = filterLang; tempFormat = filterFormat
                            tempPrice = filterPrice; tempSort = filterSort
                            showFilters = true
                        }
                    )
                }

                // Category chips
                item {
                    CategoryChipsRow(
                        categories    = com.bookworm.app.data.CATEGORIES,
                        activeCategory = activeCategory,
                        onSelect      = { activeCategory = it }
                    )
                }

                // Buy-Again banner
                if (orders.isNotEmpty() && activeCategory == "All" && search.isBlank()) {
                    item { BuyAgainBanner(orders = orders, onAddToCart = { vm.addToCart(it); showToast("Added to cart ✓") }, navController = navController) }
                }

                if (activeCategory != "All" || search.isNotBlank() || filterLang.isNotEmpty() || filterFormat.isNotEmpty() || filterPrice.isNotEmpty()) {
                    // Flat list
                    if (filtered.isEmpty()) {
                        item { EmptyState("🔍", "No books found", "Try a different search or filter.", null, null) }
                    } else {
                        item {
                            Text(
                                text = if (activeCategory != "All") activeCategory else "Search Results",
                                color = TextColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 14.dp, top = 14.dp, bottom = 8.dp)
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filtered) { book ->
                                    BookCard(
                                        book = book,
                                        onClick = { navController.navigate(Routes.bookDetail(book.id)) },
                                        onAuthorClick = { author ->
                                            search = author
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                } else {
                    // Curated sections
                    val reco = buildRecommended(filtered, orders)
                    val best = filtered.filter { it.tags.contains("bestseller") }
                    val newL = filtered.filter { it.tags.contains("new") }

                    if (reco.isNotEmpty()) {
                        item { HomeSection("Recommended for You", reco, navController, onAuthorSearch = { search = it }, sectionIndex = 0) }
                    }
                    if (best.isNotEmpty()) {
                        item { HomeSection("Bestsellers this Month", best, navController, onAuthorSearch = { search = it }, sectionIndex = 1) }
                    }
                    if (newL.isNotEmpty()) {
                        item { HomeSection("New Launches", newL, navController, onAuthorSearch = { search = it }, sectionIndex = 2) }
                    }
                    if (reco.isEmpty() && best.isEmpty() && newL.isEmpty()) {
                        item { EmptyState("📚", "No books found", "Check back later.", null, null) }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }
            }

            // Filter bottom sheet overlay
            if (showFilters) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showFilters = false }
                )
                FilterSheet(
                    tempLang    = tempLang,
                    tempFormat  = tempFormat,
                    tempPrice   = tempPrice,
                    tempSort    = tempSort,
                    onLang      = { tempLang = it },
                    onFormat    = { tempFormat = it },
                    onPrice     = { tempPrice = it },
                    onSort      = { tempSort = it },
                    onApply     = {
                        filterLang = tempLang; filterFormat = tempFormat
                        filterPrice = tempPrice; filterSort = tempSort
                        showFilters = false
                    },
                    onClear     = { tempLang = ""; tempFormat = ""; tempPrice = ""; tempSort = "" },
                    onDismiss   = { showFilters = false }
                )
            }

            // Toast
            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Build recommended list (matches JS personalisation logic) ────────────────
private fun buildRecommended(allFiltered: List<Book>, orders: List<com.bookworm.app.data.model.Order>): List<Book> {
    var reco = allFiltered.filter { it.tags.contains("recommended") }
    if (orders.isNotEmpty()) {
        val boughtIds  = orders.flatMap { o -> o.items.map { it.id } }.toSet()
        val boughtCats = orders.flatMap { o -> o.items.map { it.category } }.toSet()
        val priority   = allFiltered.filter { it.id in boughtIds } +
                         allFiltered.filter { it.id !in boughtIds && it.category in boughtCats }
        val merged = (priority + reco).distinctBy { it.id }
        reco = merged
    }
    return reco
}

// ─── Search Bar ───────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(query: String, onQuery: (String) -> Unit, onFilter: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = Surface2Color,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text("🔍", fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQuery,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = TextColor,
                        fontSize = 14.sp
                    ),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("Search books or authors…", color = Color(0xFF555555), fontSize = 14.sp)
                        inner()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(vertical = 12.dp),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(OrangeAccent)
                )
            }
        }
        // Filter button
        Surface(
            color = Surface2Color,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier
                .size(44.dp)
                .clickable { onFilter() }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("⚙️", fontSize = 18.sp)
            }
        }
    }
}

// ─── Category chips ───────────────────────────────────────────────────────────
@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    activeCategory: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { cat ->
            FilterChip(text = cat, selected = cat == activeCategory, onClick = { onSelect(cat) })
        }
    }
    Spacer(Modifier.height(4.dp))
}

// ─── Buy Again Banner ─────────────────────────────────────────────────────────
@Composable
private fun BuyAgainBanner(
    orders: List<com.bookworm.app.data.model.Order>,
    onAddToCart: (Int) -> Unit,
    navController: NavController
) {
    val lastOrder = orders.firstOrNull() ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .background(Surface2Color, RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🔄 Buy Again:", color = MutedColor, fontSize = 12.sp)
        lastOrder.items.take(3).forEach { item ->
            Surface(
                color = Color(0xFF1e1e1e),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.clickable { onAddToCart(item.id) }
            ) {
                Text(item.title, color = OrangeAccent, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            "View All →",
            color = OrangeAccent,
            fontSize = 11.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.ORDERS) }
        )
    }
}

// 4 distinct cover-color palettes — one per slot in a section preview
private val SECTION_SLOT_COLORS = listOf(
    listOf("#c0392b", "#27ae60", "#2980b9", "#8e44ad"),   // Recommended
    listOf("#e67e22", "#16a085", "#2c3e50", "#c0392b"),   // Bestsellers
    listOf("#8e44ad", "#27ae60", "#e67e22", "#2980b9"),   // New Launches
    listOf("#16a085", "#c0392b", "#8e44ad", "#27ae60"),   // fallback
)

// ─── Home section (title + 4 cards with distinct colors + See More page) ───────
@Composable
private fun HomeSection(
    title: String,
    books: List<Book>,
    navController: NavController,
    onAuthorSearch: (String) -> Unit,
    sectionIndex: Int = 0
) {
    val preview = books.take(4)
    val palette = SECTION_SLOT_COLORS.getOrElse(sectionIndex) { SECTION_SLOT_COLORS[0] }

    Column {
        // Title row with See More
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            if (books.size > 4) {
                Text(
                    text = "See More →",
                    color = OrangeAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.sectionList(title))
                    }
                )
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(preview) { idx, book ->
                // Override coverBg with the slot color for this section
                val coloredBook = book.copy(coverBg = palette[idx % palette.size])
                BookCard(
                    book = coloredBook,
                    onClick = { navController.navigate(Routes.bookDetail(book.id)) },
                    onAuthorClick = onAuthorSearch
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ─── Filter Bottom Sheet ──────────────────────────────────────────────────────
@Composable
private fun FilterSheet(
    tempLang: String, tempFormat: String, tempPrice: String, tempSort: String,
    onLang: (String) -> Unit, onFormat: (String) -> Unit,
    onPrice: (String) -> Unit, onSort: (String) -> Unit,
    onApply: () -> Unit, onClear: () -> Unit, onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            color = Surface2Color,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Handle
                Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF333333), RoundedCornerShape(2.dp)))
                }
                // Title row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filters", color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Clear All", color = OrangeAccent, fontSize = 13.sp, modifier = Modifier.clickable { onClear() })
                }

                FilterSection("Language", listOf("" to "All", "English" to "English", "Hindi" to "Hindi", "Tamil" to "Tamil"), tempLang, onLang)
                FilterSection("Format", listOf("" to "All", "Paperback" to "Paperback", "Hard Cover" to "Hard Cover", "eBook" to "eBook", "Audiobook" to "Audiobook"), tempFormat, onFormat)
                FilterSection("Price Range", listOf("" to "All", "0-100" to "Under ₹100", "0-200" to "Under ₹200", "200-400" to "₹200–₹400", "400-600" to "₹400–₹600", "600-9999" to "Above ₹600"), tempPrice, onPrice)
                FilterSection("Sort By", listOf("" to "Relevance", "price-asc" to "Price ↑", "price-desc" to "Price ↓", "rating" to "Top Rated"), tempSort, onSort)

                Spacer(Modifier.height(16.dp))
                BwButton(
                    text = "Apply Filters",
                    onClick = onApply,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Text(title, color = MutedColor, fontSize = 12.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { (value, label) ->
            FilterChip(text = label, selected = value == selected, onClick = { onSelect(value) })
        }
    }
}
