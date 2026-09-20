package com.bookworm.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bookworm.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// ─── DataStore singleton ──────────────────────────────────────────────────────
private val Context.dataStore by preferencesDataStore(name = "bookworm_prefs")

// ─── Keys (mirror localStorage keys from app.js) ─────────────────────────────
private object Keys {
    val USER         = stringPreferencesKey("bw_user")
    val CART         = stringPreferencesKey("bw_cart")
    val WISHLIST     = stringPreferencesKey("bw_wl")
    val ORDERS       = stringPreferencesKey("bw_orders")
    val GIFT_POINTS  = intPreferencesKey   ("bw_gift_points")
    val CUSTOM_BOOKS = stringPreferencesKey("bw_custom_books")
    val PREMIUM      = stringPreferencesKey("bw_premium")
    val ADDRESS      = stringPreferencesKey("bw_addr")
    val CHECKOUT_TOTAL    = stringPreferencesKey("bw_checkout_total")
    val CHECKOUT_DELIVERY = stringPreferencesKey("bw_checkout_delivery")
    val CONFIRMED_ITEMS   = stringPreferencesKey("bw_confirmed")
    val PREM_PENDING      = stringPreferencesKey("bw_prem_pending")
    val SEL_DELIVERY      = stringPreferencesKey("bw_sel_delivery")
    fun reviewKey(bookId: Int) = stringPreferencesKey("bw_rev_$bookId")
}

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

class BookWormRepository(private val context: Context) {

    // ── User ─────────────────────────────────────────────────────────────────
    val userFlow: Flow<User?> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER]?.let { runCatching { json.decodeFromString<User>(it) }.getOrNull() }
    }

    suspend fun saveUser(user: User?) {
        context.dataStore.edit { prefs ->
            if (user == null) prefs.remove(Keys.USER)
            else prefs[Keys.USER] = json.encodeToString(user)
        }
    }

    // ── Cart ─────────────────────────────────────────────────────────────────
    val cartFlow: Flow<List<CartItem>> = context.dataStore.data.map { prefs ->
        prefs[Keys.CART]?.let { runCatching { json.decodeFromString<List<CartItem>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveCart(cart: List<CartItem>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CART] = json.encodeToString(cart)
        }
    }

    // ── Wishlist ──────────────────────────────────────────────────────────────
    val wishlistFlow: Flow<List<Int>> = context.dataStore.data.map { prefs ->
        prefs[Keys.WISHLIST]?.let { runCatching { json.decodeFromString<List<Int>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveWishlist(ids: List<Int>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WISHLIST] = json.encodeToString(ids)
        }
    }

    // ── Orders ────────────────────────────────────────────────────────────────
    val ordersFlow: Flow<List<Order>> = context.dataStore.data.map { prefs ->
        prefs[Keys.ORDERS]?.let { runCatching { json.decodeFromString<List<Order>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveOrders(orders: List<Order>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ORDERS] = json.encodeToString(orders)
        }
    }

    // ── Gift points ───────────────────────────────────────────────────────────
    val giftPointsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.GIFT_POINTS] ?: 0
    }

    suspend fun saveGiftPoints(pts: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GIFT_POINTS] = pts
        }
    }

    // ── Custom books (admin-added) ────────────────────────────────────────────
    val customBooksFlow: Flow<List<Book>> = context.dataStore.data.map { prefs ->
        prefs[Keys.CUSTOM_BOOKS]?.let { runCatching { json.decodeFromString<List<Book>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveCustomBooks(books: List<Book>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CUSTOM_BOOKS] = json.encodeToString(books)
        }
    }

    // ── Premium ───────────────────────────────────────────────────────────────
    val premiumFlow: Flow<PremiumInfo?> = context.dataStore.data.map { prefs ->
        prefs[Keys.PREMIUM]?.let { runCatching { json.decodeFromString<PremiumInfo>(it) }.getOrNull() }
    }

    suspend fun savePremium(info: PremiumInfo?) {
        context.dataStore.edit { prefs ->
            if (info == null) prefs.remove(Keys.PREMIUM)
            else prefs[Keys.PREMIUM] = json.encodeToString(info)
        }
    }

    // ── Address ───────────────────────────────────────────────────────────────
    val addressFlow: Flow<Address?> = context.dataStore.data.map { prefs ->
        prefs[Keys.ADDRESS]?.let { runCatching { json.decodeFromString<Address>(it) }.getOrNull() }
    }

    suspend fun saveAddress(addr: Address?) {
        context.dataStore.edit { prefs ->
            if (addr == null) prefs.remove(Keys.ADDRESS)
            else prefs[Keys.ADDRESS] = json.encodeToString(addr)
        }
    }

    // ── Checkout state ────────────────────────────────────────────────────────
    val checkoutTotalFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.CHECKOUT_TOTAL] ?: "₹0"
    }
    val checkoutDeliveryFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.CHECKOUT_DELIVERY] ?: "standard"
    }

    suspend fun saveCheckout(total: String, delivery: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CHECKOUT_TOTAL]    = total
            prefs[Keys.CHECKOUT_DELIVERY] = delivery
        }
    }

    suspend fun clearCheckoutDelivery() {
        context.dataStore.edit { prefs -> prefs.remove(Keys.CHECKOUT_DELIVERY) }
    }

    // ── Confirmed items (post-payment) ────────────────────────────────────────
    val confirmedItemsFlow: Flow<List<OrderItem>> = context.dataStore.data.map { prefs ->
        prefs[Keys.CONFIRMED_ITEMS]?.let { runCatching { json.decodeFromString<List<OrderItem>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveConfirmedItems(items: List<OrderItem>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CONFIRMED_ITEMS] = json.encodeToString(items)
        }
    }

    suspend fun clearConfirmedItems() {
        context.dataStore.edit { prefs -> prefs.remove(Keys.CONFIRMED_ITEMS) }
    }

    // ── Premium pending (plan → payment) ─────────────────────────────────────
    val premiumPendingFlow: Flow<PremiumPending?> = context.dataStore.data.map { prefs ->
        prefs[Keys.PREM_PENDING]?.let { runCatching { json.decodeFromString<PremiumPending>(it) }.getOrNull() }
    }

    suspend fun savePremiumPending(p: PremiumPending?) {
        context.dataStore.edit { prefs ->
            if (p == null) prefs.remove(Keys.PREM_PENDING)
            else prefs[Keys.PREM_PENDING] = json.encodeToString(p)
        }
    }

    // ── Selected delivery (cart) ──────────────────────────────────────────────
    val selDeliveryFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SEL_DELIVERY] ?: "standard"
    }

    suspend fun saveSelDelivery(key: String) {
        context.dataStore.edit { prefs -> prefs[Keys.SEL_DELIVERY] = key }
    }

    // ── Reviews ───────────────────────────────────────────────────────────────
    fun reviewsFlow(bookId: Int): Flow<List<Review>> = context.dataStore.data.map { prefs ->
        prefs[Keys.reviewKey(bookId)]?.let { runCatching { json.decodeFromString<List<Review>>(it) }.getOrElse { emptyList() } }
            ?: emptyList()
    }

    suspend fun saveReviews(bookId: Int, reviews: List<Review>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.reviewKey(bookId)] = json.encodeToString(reviews)
        }
    }
}
