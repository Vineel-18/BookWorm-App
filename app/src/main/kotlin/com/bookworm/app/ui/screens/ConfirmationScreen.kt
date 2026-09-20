package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*

@Composable
fun ConfirmationScreen(vm: AppViewModel, navController: NavController) {
    val items      by vm.confirmedItems.collectAsState()
    val giftPoints by vm.giftPoints.collectAsState()

    // Clear confirmed items on first composition
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        vm.clearConfirmedItems()
    }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController) },
        bottomBar = { BookWormBottomNav(navController, null, vm) },
        containerColor = BgColor
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            FloatingBooksDecorConfirm()

            Surface(
                color = Surface2Color,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", fontSize = 52.sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Your purchase of the\nfollowing reads is successful",
                        color = TextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    // Confirmed items list
                    if (items.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceColor, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val bg = remember(item.coverBg) {
                                        try { Color(android.graphics.Color.parseColor(item.coverBg)) }
                                        catch (e: Exception) { Color(0xFF333333) }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(bg, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📖", fontSize = 16.sp)
                                    }
                                    Column {
                                        Text(item.title, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("by ${item.author}", color = MutedColor, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    } else {
                        Text("No items found.", color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                        Spacer(Modifier.height(14.dp))
                    }

                    // Gift points earned banner
                    if (giftPoints > 0) {
                        Surface(
                            color = GoldAccent.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                        ) {
                            Text(
                                "🎁 You earned $giftPoints Gift Points on this purchase!",
                                color = GoldAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    BwButton(
                        text = "Continue Shopping →",
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate(Routes.ORDERS) }) {
                        Text("View My Orders", color = OrangeAccent, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingBooksDecorConfirm() {
    Box(modifier = Modifier.fillMaxSize()) {
        listOf("📗" to (0.05f to 0.05f), "📘" to (0.88f to 0.1f), "📙" to (0.02f to 0.7f),
               "📕" to (0.85f to 0.72f), "📚" to (0.45f to 0.02f), "📖" to (0.7f to 0.85f)).forEach { (e, pos) ->
            Box(modifier = Modifier.fillMaxSize().padding(start = (pos.first * 340).dp, top = (pos.second * 620).dp)) {
                Text(e, fontSize = 22.sp, color = Color.White.copy(alpha = 0.07f))
            }
        }
    }
}
