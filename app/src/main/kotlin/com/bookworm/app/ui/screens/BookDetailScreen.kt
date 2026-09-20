package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.data.model.Book
import com.bookworm.app.data.model.Review
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookDetailScreen(vm: AppViewModel, bookId: Int, navController: NavController) {
    val allBooks = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }
    val book     = allBooks.find { it.id == bookId }

    if (book == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val wishlist    by vm.wishlist.collectAsState()
    val inWishlist  = wishlist.contains(bookId)
    val reviews     by vm.repo.reviewsFlow(bookId).collectAsState(emptyList())

    var toastMsg    by remember { mutableStateOf("") }
    var reviewText  by remember { mutableStateOf("") }
    var selectedStars by remember { mutableIntStateOf(5) }
    val scope       = rememberCoroutineScope()

    fun showToast(msg: String) { scope.launch { toastMsg = msg; delay(2300); toastMsg = "" } }

    Scaffold(
        topBar = { BookWormTopBar(vm = vm, navController = navController, showBack = true) },
        bottomBar = {
            Column {
                // Sticky Add-to-Cart footer (sits above bottom nav)
                Surface(color = NavBarColor, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { vm.addToCart(bookId); showToast("Added to cart ✓") },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("🛒 Add to Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        // Wishlist toggle
                        Surface(
                            color = Surface2Color,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (inWishlist) Color(0xFFE74C3C) else BorderColor),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    val added = vm.toggleWishlist(bookId)
                                    showToast(if (added) "Added to wishlist ♥" else "Removed from wishlist")
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(if (inWishlist) "❤️" else "🤍", fontSize = 20.sp)
                            }
                        }
                    }
                }
                BookWormBottomNav(navController, Routes.HOME, vm)
            }
        },
        containerColor = BgColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Breadcrumb
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Home", color = OrangeAccent, fontSize = 12.sp, modifier = Modifier.clickable { navController.navigate(Routes.HOME) { launchSingleTop = true } })
                    Text(" / ", color = MutedColor, fontSize = 12.sp)
                    Text(book.category, color = OrangeAccent, fontSize = 12.sp)
                    Text(" / ", color = MutedColor, fontSize = 12.sp)
                    Text(book.title, color = MutedColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // Book cover — portrait ratio (2:3), centred, fixed width like a real book
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BookCover(
                        book = book,
                        modifier = Modifier
                            .width(160.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(10.dp)),
                        titleFontSize = 14.sp
                    )
                }
            }

            // Info block
            item {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(book.title, color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 26.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("by ${book.author}", color = OrangeAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(book.desc, color = MutedColor, fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Sold by: ${book.publisher.ifBlank { "Book Worm Store" }}", color = MutedColor, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(book.format, color = MutedColor, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        book.genres.forEach { g ->
                            Surface(
                                color = Surface2Color,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Text(g, color = OrangeAccent, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("₹${book.price}", color = TextColor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    if (book.originalPrice > book.price) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "₹${book.originalPrice}",
                                color = MutedColor,
                                fontSize = 13.sp,
                                style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough),
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            val disc = ((book.originalPrice - book.price) * 100 / book.originalPrice)
                            Text("$disc% off", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Delivery by ${book.delivery}", color = MutedColor, fontSize = 12.sp)
                }
            }

            // Meta row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .background(Surface2Color, RoundedCornerShape(10.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetaItem("Language", book.language, OrangeAccent)
                    VerticalDivider(modifier = Modifier.height(36.dp), color = BorderColor)
                    MetaItem("Rating", "${book.rating} ★", GoldAccent)
                    VerticalDivider(modifier = Modifier.height(36.dp), color = BorderColor)
                    MetaItem("Sold", "${book.sales.formatWithCommas()} copies", TextColor)
                }
            }

            // About author
            item {
                Surface(
                    color = SurfaceColor,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About the writer", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 12.dp))
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(OrangeAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(book.author.take(1), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(book.author, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${book.author} is an acclaimed author and storyteller. With a passion for transforming lives, they have dedicated their career to helping readers reach their fullest potential. They are the author of ${book.title}.",
                                    color = MutedColor, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Reviews
            item {
                Surface(
                    color = SurfaceColor,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reviews", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 12.dp))

                        // Review form
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Leave Your Review", color = MutedColor, fontSize = 12.sp)
                            Text("${reviewText.length}/500", color = MutedColor, fontSize = 11.sp)
                        }

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { if (it.length <= 500) reviewText = it },
                            placeholder = { Text("Write your review here…", color = Color(0xFF444444), fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeAccent, unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextColor, unfocusedTextColor = TextColor,
                                cursorColor = OrangeAccent, focusedContainerColor = Surface2Color, unfocusedContainerColor = Surface2Color
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextColor)
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Star selector
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                (1..5).forEach { i ->
                                    Text(
                                        "★",
                                        fontSize = 26.sp,
                                        color = if (i <= selectedStars) GoldAccent else Color(0xFF444444),
                                        modifier = Modifier.clickable { selectedStars = i }
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    if (reviewText.isBlank()) { showToast("Please write a review first"); return@Button }
                                    val user = vm.user.value
                                    val newReview = Review(user?.name ?: "Anonymous", reviewText, selectedStars)
                                    val updatedReviews = reviews.toMutableList().also { it.add(newReview) }
                                    scope.launch { vm.repo.saveReviews(bookId, updatedReviews) }
                                    reviewText = ""; selectedStars = 5
                                    showToast("Review submitted ✓")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Submit →", fontSize = 13.sp)
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Displayed reviews (static + user-submitted)
                        val allReviews = listOf(Review("John Smith", "A wonderful read — highly recommended for anyone looking for fresh perspective and growth.", 5)) + reviews
                        allReviews.forEach { r ->
                            ReviewCard(r)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

            // Related reads
            item {
                val related = buildRelated(book, allBooks)
                if (related.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("Related Reads", color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(related) { r ->
                                RelatedCard(r, navController)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
    }
}

// ─── Meta item ────────────────────────────────────────────────────────────────
@Composable
private fun MetaItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = MutedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Review card ──────────────────────────────────────────────────────────────
@Composable
private fun ReviewCard(review: Review) {
    Surface(
        color = Surface2Color,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(review.user, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("★".repeat(review.rating.coerceIn(0, 5)) + "☆".repeat(5 - review.rating.coerceIn(0, 5)),
                    color = GoldAccent, fontSize = 12.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(review.text, color = MutedColor, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

// ─── Related card ─────────────────────────────────────────────────────────────
@Composable
private fun RelatedCard(book: Book, navController: NavController) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { navController.navigate(Routes.bookDetail(book.id)) }
    ) {
        BookCover(
            book = book,
            modifier = Modifier.fillMaxWidth().height(165.dp),
            titleFontSize = 9.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(book.title, color = TextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
        Text("by ${book.author}", color = OrangeAccent, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(book.desc, color = MutedColor, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 14.sp)
        Text("₹${book.price}", color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
private fun buildRelated(book: Book, allBooks: List<Book>): List<Book> {
    val same  = allBooks.filter { r -> r.id != book.id && (r.category == book.category || r.genres.any { g -> book.genres.contains(g) }) }
    val rest  = allBooks.filter { r -> r.id != book.id && !same.any { it.id == r.id } }
    return (same + rest).take(6)
}

private fun Int.formatWithCommas(): String {
    return "%,d".format(this)
}
