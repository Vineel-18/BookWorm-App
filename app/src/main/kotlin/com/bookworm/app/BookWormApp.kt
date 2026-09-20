package com.bookworm.app

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.bookworm.app.ui.screens.*

// ─── Route constants ──────────────────────────────────────────────────────────
object Routes {
    const val LOGIN        = "login"
    const val HOME         = "home"
    const val BOOK_DETAIL  = "book/{bookId}"
    const val CART         = "cart"
    const val PAYMENT      = "payment"
    const val CONFIRMATION = "confirmation"
    const val ORDERS       = "orders"
    const val WISHLIST     = "wishlist"
    const val DASHBOARD    = "dashboard"
    const val PREMIUM      = "premium"
    const val PREM_PAYMENT = "prem_payment"
    const val PREM_CONFIRM = "prem_confirm"
    const val ADMIN        = "admin"
    const val STORES       = "stores"
    const val SECTION_LIST = "section/{sectionKey}"

    fun bookDetail(bookId: Int) = "book/$bookId"
    fun sectionList(key: String) = "section/${java.net.URLEncoder.encode(key, "UTF-8")}"
}

@Composable
fun BookWormApp(vm: AppViewModel) {
    val navController = rememberNavController()
    val user by vm.user.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(vm = vm, navController = navController)
        }
        composable(Routes.HOME) {
            HomeScreen(vm = vm, navController = navController)
        }
        composable(
            route = Routes.BOOK_DETAIL,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { back ->
            val bookId = back.arguments?.getInt("bookId") ?: return@composable
            BookDetailScreen(vm = vm, bookId = bookId, navController = navController)
        }
        composable(Routes.CART) {
            CartScreen(vm = vm, navController = navController)
        }
        composable(Routes.PAYMENT) {
            PaymentScreen(vm = vm, navController = navController)
        }
        composable(Routes.CONFIRMATION) {
            ConfirmationScreen(vm = vm, navController = navController)
        }
        composable(Routes.ORDERS) {
            OrdersScreen(vm = vm, navController = navController)
        }
        composable(Routes.WISHLIST) {
            WishlistScreen(vm = vm, navController = navController)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(vm = vm, navController = navController)
        }
        composable(Routes.PREMIUM) {
            PremiumScreen(vm = vm, navController = navController)
        }
        composable(Routes.PREM_PAYMENT) {
            PremiumPaymentScreen(vm = vm, navController = navController)
        }
        composable(Routes.PREM_CONFIRM) {
            PremiumConfirmationScreen(vm = vm, navController = navController)
        }
        composable(Routes.ADMIN) {
            AdminScreen(vm = vm, navController = navController)
        }
        composable(Routes.STORES) {
            StoresScreen(vm = vm, navController = navController)
        }
        composable(
            route = Routes.SECTION_LIST,
            arguments = listOf(navArgument("sectionKey") { type = NavType.StringType })
        ) { back ->
            val key = back.arguments?.getString("sectionKey")
                ?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: return@composable
            SectionListScreen(vm = vm, sectionKey = key, navController = navController)
        }
    }

    // Navigate to correct start once user state is loaded
    LaunchedEffect(user) {
        val current = navController.currentDestination?.route
        if (user != null && current == Routes.LOGIN) {
            val dest = if (user?.role == "admin") Routes.ADMIN else Routes.HOME
            navController.navigate(dest) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
        if (user == null && current != null && current != Routes.LOGIN) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
