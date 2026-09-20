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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*

@Composable
fun DashboardScreen(vm: AppViewModel, navController: NavController) {
    val user       by vm.user.collectAsState()
    val orders     by vm.orders.collectAsState()
    val wishlist   by vm.wishlist.collectAsState()
    val giftPoints by vm.giftPoints.collectAsState()
    val isPremium  = vm.isPremium()
    val initial    = (user?.name ?: "U").take(1).uppercase()

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, null, vm) },
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Hero card ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1a1a1a), Color(0xFF111111))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(OrangeAccent, CircleShape)
                                .border(2.dp, OrangeAccent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initial, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                user?.name ?: "User",
                                color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
                            )
                            if (user?.email?.isNotBlank() == true) {
                                Text(user!!.email, color = MutedColor, fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp))
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                when (user?.role) {
                                    "registered" -> DashBadge("Member",  Color(0xFF3498DB))
                                    "guest"      -> DashBadge("Guest",   MutedColor)
                                    "admin"      -> DashBadge("Admin",   RedAccent)
                                }
                                if (isPremium) DashBadge("👑 Premium", GoldAccent)
                            }
                        }
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashStat("Orders",   "${orders.size}",  Modifier.weight(1f))
                DashStat("Wishlist", "${wishlist.size}", Modifier.weight(1f))
                DashStat("Gift Pts", "$giftPoints",      Modifier.weight(1f))
            }

            // ── MY ACCOUNT ────────────────────────────────────────────────────
            DashSectionTitle("MY ACCOUNT")
            DashMenuGroup(
                listOf(
                    Triple("📦", "My Orders",     "Track and manage your orders")     to { navController.navigate(Routes.ORDERS) },
                    Triple("🤍", "Wishlist",      "Books you've saved for later")      to { navController.navigate(Routes.WISHLIST) },
                    Triple("🛒", "Shopping Cart", "Review items before checkout")      to { navController.navigate(Routes.CART) },
                )
            )

            // ── MEMBERSHIP ────────────────────────────────────────────────────
            DashSectionTitle("MEMBERSHIP")
            DashMenuGroup(
                listOf(
                    Triple(
                        "👑",
                        if (isPremium) "Premium Active" else "Upgrade to Premium",
                        if (isPremium) "View your perks & coupons" else "Free delivery, exclusive coupons & more"
                    ) to { navController.navigate(Routes.PREMIUM) }
                )
            )

            // ── PREFERENCES ───────────────────────────────────────────────────
            DashSectionTitle("PREFERENCES")
            DashMenuGroup(
                listOf(
                    Triple("🏠", "Browse Books", "Discover new titles")        to { navController.navigate(Routes.HOME) },
                    Triple("🏪", "Our Stores",   "Explore curated bookstores") to { navController.navigate(Routes.STORES) },
                )
            )

            Spacer(Modifier.height(20.dp))

            // ── Sign out ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(RedAccent.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
                    .border(1.dp, RedAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .clickable { vm.logout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("⎋  Sign Out", color = RedAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ─── Badge ────────────────────────────────────────────────────────────────────
@Composable
private fun DashBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Stat card ────────────────────────────────────────────────────────────────
@Composable
private fun DashStat(label: String, value: String, modifier: Modifier) {
    Surface(
        color  = SurfaceColor,
        shape  = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = OrangeAccent, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(label, color = MutedColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp)
        }
    }
}

// ─── Section title ────────────────────────────────────────────────────────────
@Composable
private fun DashSectionTitle(text: String) {
    Text(
        text, color = MutedColor, fontSize = 11.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 6.dp)
    )
}

// ─── Menu group (grouped in one card with dividers) ───────────────────────────
@Composable
private fun DashMenuGroup(items: List<Pair<Triple<String, String, String>, () -> Unit>>) {
    Surface(
        color  = SurfaceColor,
        shape  = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column {
            items.forEachIndexed { idx, (triple, onClick) ->
                val (icon, label, sub) = triple
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1e1e1e), CircleShape)
                            .border(1.dp, BorderColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(sub, color = MutedColor, fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                    }
                    Text("›", color = Color(0xFF555555), fontSize = 20.sp, fontWeight = FontWeight.Light)
                }
                // Divider between items, not after last
                if (idx < items.size - 1) {
                    HorizontalDivider(
                        color = BorderColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
