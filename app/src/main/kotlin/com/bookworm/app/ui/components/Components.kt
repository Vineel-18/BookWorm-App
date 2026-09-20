package com.bookworm.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.data.model.Book
import com.bookworm.app.ui.theme.*

// ─── Top Navigation Bar ───────────────────────────────────────────────────────
@Composable
fun BookWormTopBar(
    vm: AppViewModel,
    navController: NavController,
    showBack: Boolean = false,
    title: String = "Book Worm"
) {
    val cartCount by vm.cart.collectAsState()
    val user      by vm.user.collectAsState()
    val count     = cartCount.sumOf { it.qty }

    Surface(
        color = NavBarColor,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .then(Modifier) // just padding below
    ) {}

    Surface(
        color = NavBarColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(52.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                Text(
                    text = "‹",
                    color = TextColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(end = 8.dp, top = 2.dp)
                )
            } else {
                // 3×3 grid icon
                Box(modifier = Modifier.padding(end = 7.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
                        repeat(3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(3.5.dp)
                                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(0.5.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = title,
                color = TextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Cart icon with badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate(Routes.CART) },
                contentAlignment = Alignment.Center
            ) {
                Text("🛒", fontSize = 20.sp)
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp, end = 2.dp)
                            .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                            .background(RedAccent, CircleShape)
                            .padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = count.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Avatar button
            if (user != null) {
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(OrangeAccent, CircleShape)
                        .clickable { navController.navigate(Routes.DASHBOARD) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.name ?: "U").take(1).uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
    // Bottom border
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderColor)
    )
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────
@Composable
fun BookWormBottomNav(
    navController: NavController,
    currentRoute: String?,
    vm: AppViewModel,
    isAdmin: Boolean = false
) {
    val cartItems by vm.cart.collectAsState()
    val cartCount = cartItems.sumOf { it.qty }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
        )
        Surface(
            color = NavBarColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAdmin) {
                    AdminBottomNavItem("📊", "Dashboard", currentRoute == Routes.ADMIN, navController, Routes.ADMIN)
                    AdminBottomNavItem("📚", "Books",     false, navController, Routes.ADMIN)
                    AdminBottomNavItem("📦", "Orders",    false, navController, Routes.ADMIN)
                    AdminBottomNavItem("🏪", "Stores",    false, navController, Routes.STORES)
                } else {
                    BottomNavItem("🏠", "Home",    currentRoute == Routes.HOME,    navController, Routes.HOME)
                    BottomNavItem("📦", "Orders",  currentRoute == Routes.ORDERS,  navController, Routes.ORDERS)
                    BottomNavItem("🤍", "Wishlist",currentRoute == Routes.WISHLIST,navController, Routes.WISHLIST)
                    Box {
                        BottomNavItem("🛒", "Cart",    currentRoute == Routes.CART,    navController, Routes.CART)
                        if (cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 6.dp)
                                    .size(16.dp)
                                    .background(RedAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(cartCount.toString(), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    BottomNavItem("👑", "Premium", currentRoute == Routes.PREMIUM, navController, Routes.PREMIUM)
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    selected: Boolean,
    navController: NavController,
    route: String
) {
    val color = if (selected) OrangeAccent else MutedColor
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    if (route == Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    } else {
                        restoreState = true
                        popUpTo(Routes.HOME) { saveState = true }
                    }
                }
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(icon, fontSize = 20.sp, color = if (selected) Color.Unspecified else Color.Unspecified)
        Text(label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}

@Composable
private fun AdminBottomNavItem(
    icon: String, label: String, selected: Boolean,
    navController: NavController, route: String
) = BottomNavItem(icon, label, selected, navController, route)

// ─── Book Cover Composable — 3-zone style matching website ───────────────────
@Composable
fun BookCover(
    book: Book,
    modifier: Modifier = Modifier,
    titleFontSize: TextUnit = 12.sp
) {
    val bg = remember(book.coverBg) {
        try { Color(android.graphics.Color.parseColor(book.coverBg)) }
        catch (e: Exception) { Color(0xFF333333) }
    }
    Box(
        modifier = modifier.background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top zone: category — readable but secondary
            Text(
                text = book.category.uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Middle zone: title — large and bold, clear distinction
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = book.title.uppercase(),
                    color = Color.White,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Left,
                    lineHeight = (titleFontSize.value * 1.3).sp,
                    letterSpacing = 0.4.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }
            // Bottom zone: author — readable but secondary
            Text(
                text = book.author.uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Book Card (used in Home, Search results) ─────────────────────────────────
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onAuthorClick: ((String) -> Unit)? = null
) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Cover
            BookCover(
                book = book,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                titleFontSize = 18.sp
            )
            // Meta
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = book.title,
                    color = TextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "by ${book.author}",
                    color = OrangeAccent,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (onAuthorClick != null) Modifier.clickable { onAuthorClick(book.author) } else Modifier
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = book.desc,
                    color = MutedColor,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = book.format,
                        color = MutedColor,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "₹${book.price}",
                        color = TextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// ─── Toast ────────────────────────────────────────────────────────────────────
@Composable
fun BwToast(message: String) {
    if (message.isEmpty()) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = Color(0xFF1a1a1a),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor),
            shadowElevation = 8.dp
        ) {
            Text(
                text = message,
                color = TextColor,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }
    }
}

// ─── Toast host state ─────────────────────────────────────────────────────────
@Composable
fun rememberToastState(): ToastState = remember { ToastState() }

class ToastState {
    var message by mutableStateOf("")
        private set
    private var job: kotlinx.coroutines.Job? = null

    fun show(msg: String, scope: kotlinx.coroutines.CoroutineScope) {
        job?.cancel()
        message = msg
        job = scope.launch {
            kotlinx.coroutines.delay(2300)
            message = ""
        }
    }
}

// ─── Chip ─────────────────────────────────────────────────────────────────────
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg     = if (selected) OrangeAccent else SurfaceColor
    val border = if (selected) OrangeAccent else BorderColor
    val tc     = if (selected) Color.White  else MutedColor

    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, border),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = tc,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

// ─── Section title ────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = TextColor,
        fontSize = 15.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier.padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

// ─── Input field ──────────────────────────────────────────────────────────────
@Composable
fun BwTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
    imeAction: androidx.compose.ui.text.input.ImeAction = androidx.compose.ui.text.input.ImeAction.Next
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = MutedColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        val visual = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation()
                     else            androidx.compose.ui.text.input.VisualTransformation.None
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF444444), fontSize = 13.sp) },
            visualTransformation = visual,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                imeAction    = imeAction
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = OrangeAccent,
                unfocusedBorderColor = BorderColor,
                focusedTextColor     = TextColor,
                unfocusedTextColor   = TextColor,
                cursorColor          = OrangeAccent,
                focusedContainerColor   = Surface2Color,
                unfocusedContainerColor = Surface2Color
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextColor)
        )
    }
}

// ─── Primary Button ───────────────────────────────────────────────────────────
@Composable
fun BwButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = OrangeAccent,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor         = color,
            contentColor           = Color.White,
            disabledContainerColor = color.copy(alpha = 0.4f),
            disabledContentColor   = Color.White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth().height(50.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Outline Button ───────────────────────────────────────────────────────────
@Composable
fun BwOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = OrangeAccent
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth().height(50.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    icon: String,
    title: String,
    subtitle: String,
    buttonText: String? = null,
    onButton: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(title, color = TextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = MutedColor, fontSize = 13.sp, textAlign = TextAlign.Center)
        if (buttonText != null && onButton != null) {
            Spacer(Modifier.height(20.dp))
            BwButton(text = buttonText, onClick = onButton, modifier = Modifier.width(160.dp))
        }
    }
}

// ─── Star rating display ──────────────────────────────────────────────────────
@Composable
fun StarRating(rating: Double, fontSize: TextUnit = 14.sp) {
    val full  = rating.toInt().coerceIn(0, 5)
    val empty = 5 - full
    Text(
        text = "★".repeat(full) + "☆".repeat(empty),
        color = GoldAccent,
        fontSize = fontSize,
        letterSpacing = 1.sp
    )
}

// ─── Tag badge ────────────────────────────────────────────────────────────────
@Composable
fun TagBadge(tag: String) {
    val (bg, tc) = when (tag) {
        "bestseller" -> Pair(Color(0x1FE8943A), OrangeAccent)
        "recommended"-> Pair(Color(0x1F3498DB), BlueAccent)
        "new"        -> Pair(Color(0x1F2ECC71), GreenAccent)
        else         -> Pair(Color(0x1F888888), MutedColor)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, tc.copy(alpha = 0.3f)),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = tag,
            color = tc,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}
