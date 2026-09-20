package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
fun WishlistScreen(vm: AppViewModel, navController: NavController) {
    val wishlistIds by vm.wishlist.collectAsState()
    val allBooks    = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }
    val books       = allBooks.filter { it.id in wishlistIds }
    var toastMsg    by remember { mutableStateOf("") }
    val scope       = rememberCoroutineScope()

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, Routes.WISHLIST, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (books.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState("🤍", "Your wishlist is empty", "Browse books and add your favourites here.",
                        "Browse Books") { navController.navigate(Routes.HOME) }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Text("Wishlist", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp))
                    }
                    items(books) { book ->
                        WishlistCard(
                            book         = book,
                            onNavigate   = { navController.navigate(Routes.bookDetail(book.id)) },
                            onAddToCart  = { vm.addToCart(book.id); showToast("Added to cart ✓") },
                            onRemove     = { vm.toggleWishlist(book.id); showToast("Removed from wishlist") }
                        )
                    }
                    item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(20.dp)) }
                }
            }

            if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
        }
    }
}

// ─── Wishlist card ────────────────────────────────────────────────────────────
@Composable
private fun WishlistCard(
    book: Book,
    onNavigate: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Cover
            BookCover(
                book = book,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { onNavigate() },
                titleFontSize = 9.sp
            )
            // Info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(book.title, color = TextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp,
                    modifier = Modifier.clickable { onNavigate() })
                Text("by ${book.author}", color = OrangeAccent, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(book.format, color = MutedColor, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                Text("₹${book.price}", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                Text("Delivery by ${book.delivery}", color = MutedColor, fontSize = 10.sp)
                Spacer(Modifier.height(8.dp))
                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) { Text("Add to Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                    Surface(
                        color = RedAccent.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.size(34.dp).clickable { onRemove() }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("🗑", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
