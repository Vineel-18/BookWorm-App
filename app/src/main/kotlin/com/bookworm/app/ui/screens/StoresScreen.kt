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
import com.bookworm.app.data.BASE_STORES
import com.bookworm.app.data.model.Store
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*

@Composable
fun StoresScreen(vm: AppViewModel, navController: NavController) {
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    val allBooks = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController, showBack = selectedStore != null) },
        bottomBar = { BookWormBottomNav(navController, null, vm) },
        containerColor = BgColor
    ) { padding ->
        if (selectedStore == null) {
            // Store list
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                item {
                    Text("Our Stores", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 14.dp, top = 14.dp, bottom = 4.dp))
                    Text("3 curated bookstores · 45 books", color = MutedColor, fontSize = 13.sp,
                        modifier = Modifier.padding(start = 14.dp, bottom = 14.dp))
                }
                items(BASE_STORES) { store ->
                    StoreCard(store = store, onClick = { selectedStore = store })
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        } else {
            // Store detail
            val store = selectedStore!!
            StoreDetail(
                store      = store,
                allBooks   = allBooks,
                navController = navController,
                vm         = vm,
                onBack     = { selectedStore = null }
            )
        }
    }
}

// ─── Store list card ──────────────────────────────────────────────────────────
@Composable
private fun StoreCard(store: Store, onClick: () -> Unit) {
    val bg = remember(store.coverColor) {
        try { Color(android.graphics.Color.parseColor(store.coverColor)) }
        catch (e: Exception) { BgColor }
    }
    val ac = remember(store.accentColor) {
        try { Color(android.graphics.Color.parseColor(store.accentColor)) }
        catch (e: Exception) { OrangeAccent }
    }

    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 7.dp).clickable { onClick() }
    ) {
        Column {
            // Cover header
            Box(
                modifier = Modifier.fillMaxWidth().height(90.dp).background(bg),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(store.emoji, fontSize = 28.sp)
                }
                // Rating badge
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                ) {
                    Text("★ ${store.rating}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            // Info
            Column(modifier = Modifier.padding(14.dp)) {
                Text(store.name, color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(store.tagline, color = ac, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                Text(store.description, color = MutedColor, fontSize = 12.sp, lineHeight = 17.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StorePill("${store.bookIds.size} books", ac)
                    StorePill("Est. ${store.founded}", ac)
                    StorePill(store.location.substringBefore(","), MutedColor)
                }
                Spacer(Modifier.height(8.dp))
                // Category chips preview
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(store.categories.take(4)) { cat ->
                        Surface(color = ac.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, ac.copy(alpha = 0.3f))) {
                            Text(cat, color = ac, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─── Store detail ─────────────────────────────────────────────────────────────
@Composable
private fun StoreDetail(
    store: Store,
    allBooks: List<com.bookworm.app.data.model.Book>,
    navController: NavController,
    vm: AppViewModel,
    onBack: () -> Unit
) {
    val bg = remember(store.coverColor) {
        try { Color(android.graphics.Color.parseColor(store.coverColor)) }
        catch (e: Exception) { BgColor }
    }
    val ac = remember(store.accentColor) {
        try { Color(android.graphics.Color.parseColor(store.accentColor)) }
        catch (e: Exception) { OrangeAccent }
    }

    var activeTab by remember { mutableStateOf("catalog") }  // catalog | policy

    Column(modifier = Modifier.fillMaxSize()) {
        // Store header
        Box(
            modifier = Modifier.fillMaxWidth().height(130.dp).background(bg),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(store.emoji, fontSize = 32.sp)
                Text(store.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(store.tagline, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
            Text("★ ${store.rating}", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd).padding(14.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp))
        }

        // Tab row
        Surface(color = NavBarColor, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                StoreTab("Catalog", activeTab == "catalog", ac) { activeTab = "catalog" }
                StoreTab("Store Policy", activeTab == "policy", ac) { activeTab = "policy" }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderColor))

        // Content
        when (activeTab) {
            "catalog" -> StoreCatalog(store, allBooks, ac, navController, vm)
            "policy"  -> StorePolicy(store)
        }
    }
}

@Composable
private fun StoreTab(label: String, selected: Boolean, ac: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = if (selected) ac else MutedColor, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        if (selected) {
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier.width(24.dp).height(2.dp).background(ac, RoundedCornerShape(1.dp)))
        }
    }
}

@Composable
private fun StoreCatalog(store: Store, allBooks: List<com.bookworm.app.data.model.Book>, ac: Color, navController: NavController, vm: AppViewModel) {
    var toastMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(store.description, color = MutedColor, fontSize = 13.sp, lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
        }
        items(store.catalog) { section ->
            val books = section.ids.mapNotNull { id -> allBooks.find { it.id == id } }
            if (books.isNotEmpty()) {
                Text(section.section, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 14.dp, top = 14.dp, bottom = 8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books) { book ->
                        BookCard(
                            book = book,
                            onClick = { navController.navigate(Routes.bookDetail(book.id)) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun StorePolicy(store: Store) {
    val policy = store.policy
    val items = listOf(
        Triple("↩", "Returns & Refunds", policy.returns),
        Triple("🚚", "Delivery", policy.delivery),
        Triple("💳", "Payment", policy.payment),
        Triple("🛡️", "Warranty", policy.warranty),
        Triple("🎁", "Loyalty Program", policy.loyalty),
        Triple("📞", "Contact", policy.contact)
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Spacer(Modifier.height(8.dp)) }
        items(items) { (icon, title, text) ->
            if (text.isNotBlank()) {
                Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp)) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(icon, fontSize = 22.sp)
                        Column {
                            Text(title, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(text, color = MutedColor, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
        item {
            Surface(color = SurfaceColor, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp)) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📍", fontSize = 22.sp)
                    Column {
                        Text("Location", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(store.location, color = MutedColor, fontSize = 12.sp)
                        Text("Founded ${store.founded}", color = MutedColor, fontSize = 12.sp)
                        Text("${store.totalSales.formatWithCommas2()} total sales", color = MutedColor, fontSize = 12.sp)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun StorePill(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.3f))) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

private fun Int.formatWithCommas2(): String = "%,d".format(this)
