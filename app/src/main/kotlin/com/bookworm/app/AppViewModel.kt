package com.bookworm.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookworm.app.data.*
import com.bookworm.app.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val repo = BookWormRepository(application)

    // ── Persisted state flows ─────────────────────────────────────────────────
    val user          = repo.userFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val cart          = repo.cartFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val wishlist      = repo.wishlistFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val orders        = repo.ordersFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val giftPoints    = repo.giftPointsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val customBooks   = repo.customBooksFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val premium       = repo.premiumFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val address       = repo.addressFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val selDelivery   = repo.selDeliveryFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "standard")
    val checkoutTotal = repo.checkoutTotalFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "₹0")
    val confirmedItems= repo.confirmedItemsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val premiumPending= repo.premiumPendingFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // ── Derived helpers ───────────────────────────────────────────────────────
    fun isPremium(): Boolean {
        val p = premium.value ?: return false
        if (p.days >= 99999) return true   // lifetime
        return p.expiry > System.currentTimeMillis()
    }

    fun isAdmin(): Boolean = user.value?.role == "admin"

    /** Merges base books with admin overrides (mirrors getAllBooks() in app.js) */
    fun getAllBooks(): List<Book> {
        val custom = customBooks.value
        val base   = BASE_BOOKS.filter { b -> !custom.any { c -> c.id == b.id && c._deleted } }
        val adds   = custom.filter { !it._deleted }
        val merged = base.map { b -> adds.find { c -> c.id == b.id } ?: b }
        val extras = adds.filter { c -> BASE_BOOKS.none { b -> b.id == c.id } }
        return merged + extras
    }

    fun getBook(id: Int): Book? = getAllBooks().find { it.id == id }

    fun cartCount(): Int = cart.value.sumOf { it.qty }

    fun isInWishlist(id: Int): Boolean = wishlist.value.contains(id)

    // ── Auth ──────────────────────────────────────────────────────────────────
    fun login(email: String, password: String): LoginResult {
        return when {
            email == "admin@bookworm.com" && password == "admin123" -> {
                viewModelScope.launch {
                    repo.saveUser(User(email, "Admin", "admin"))
                }
                LoginResult.Admin
            }
            email.isNotBlank() && password.isNotBlank() -> {
                val name = email.substringBefore("@").replace(Regex("[^a-zA-Z0-9]"), " ").trim()
                    .replaceFirstChar { it.uppercase() }
                viewModelScope.launch {
                    repo.saveUser(User(email, name, "registered"))
                    // Give 250 points to new login if 0
                    if (giftPoints.value == 0) repo.saveGiftPoints(250)
                }
                LoginResult.User
            }
            else -> LoginResult.InvalidCredentials
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String): RegisterResult {
        if (password.length < 8) return RegisterResult.PasswordTooShort
        val name = "$firstName $lastName".trim()
        viewModelScope.launch {
            repo.saveUser(User(email, name, "registered"))
            repo.saveGiftPoints(100)
        }
        return RegisterResult.Success
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            repo.saveUser(User("", "Guest", "guest"))
            repo.saveGiftPoints(0)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.saveUser(null)
        }
    }

    // ── Cart ──────────────────────────────────────────────────────────────────
    fun addToCart(bookId: Int) {
        viewModelScope.launch {
            val current = cart.value.toMutableList()
            val existing = current.find { it.id == bookId }
            if (existing != null) {
                val idx = current.indexOf(existing)
                current[idx] = existing.copy(qty = existing.qty + 1)
            } else {
                current.add(CartItem(bookId, 1))
            }
            repo.saveCart(current)
        }
    }

    fun changeQty(bookId: Int, delta: Int) {
        viewModelScope.launch {
            val current = cart.value.toMutableList()
            val item = current.find { it.id == bookId } ?: return@launch
            val newQty = item.qty + delta
            if (newQty < 1) {
                current.removeAll { it.id == bookId }
            } else {
                val idx = current.indexOf(item)
                current[idx] = item.copy(qty = newQty)
            }
            repo.saveCart(current)
        }
    }

    fun removeFromCart(bookId: Int) {
        viewModelScope.launch {
            repo.saveCart(cart.value.filter { it.id != bookId })
        }
    }

    fun clearCart() {
        viewModelScope.launch { repo.saveCart(emptyList()) }
    }

    // ── Wishlist ──────────────────────────────────────────────────────────────
    fun toggleWishlist(bookId: Int): Boolean {
        val current = wishlist.value.toMutableList()
        val added: Boolean
        if (current.contains(bookId)) {
            current.remove(bookId)
            added = false
        } else {
            current.add(bookId)
            added = true
        }
        viewModelScope.launch { repo.saveWishlist(current) }
        return added
    }

    // ── Delivery selection ────────────────────────────────────────────────────
    fun selectDelivery(key: String) {
        viewModelScope.launch { repo.saveSelDelivery(key) }
    }

    // ── Address ───────────────────────────────────────────────────────────────
    fun saveAddress(addr: Address) {
        viewModelScope.launch { repo.saveAddress(addr) }
    }

    // ── Payment / order creation ──────────────────────────────────────────────
    fun proceedToPayment(totalStr: String, deliveryKey: String) {
        viewModelScope.launch {
            repo.saveCheckout(totalStr, deliveryKey)
        }
    }

    fun completePayment() {
        viewModelScope.launch {
            val cartItems  = cart.value
            val allBooks   = getAllBooks()
            val items      = cartItems.mapNotNull { ci ->
                val b = allBooks.find { it.id == ci.id }
                b?.let { OrderItem(it.id, it.title, it.author, it.category, it.price, ci.qty, it.coverBg) }
            }
            val total      = checkoutTotal.value
            val prem       = isPremium()
            val now        = System.currentTimeMillis()
            val delivType  = repo.checkoutDeliveryFlow.first().ifBlank { if (prem) "prem_same" else "standard" }

            val DAY  = 24L * 60 * 60 * 1000
            val MIN  = 60_000L
            val windowMs = when (delivType) {
                "standard"  -> 6  * DAY
                "fast"      -> 3  * DAY
                "sameday"   -> 2  * MIN
                "prem_same" -> 2  * MIN
                "prem_next" -> 1  * DAY
                else        -> 6  * DAY
            }
            val delivLabel = when (delivType) {
                "standard"  -> "Standard (4–6 days)"
                "fast"      -> "Fast (2–3 days)"
                "sameday"   -> "Same-Day"
                "prem_same" -> "👑 Same-Day (Premium)"
                "prem_next" -> "👑 Next-Day (Premium)"
                else        -> "Standard"
            }
            val delivDays = when (delivType) { "fast" -> 3; "sameday","prem_same" -> 0; "prem_next" -> 1; else -> 6 }

            val sdf = SimpleDateFormat("d MMM yyyy", Locale("en", "IN"))
            val order = Order(
                id           = java.lang.Long.toString(now, 36).uppercase(),
                date         = sdf.format(Date(now)),
                placedAt     = now,
                packedAt     = now + (windowMs * 0.10).toLong(),
                shippedAt    = now + (windowMs * 0.40).toLong(),
                outAt        = now + (windowMs * 0.80).toLong(),
                deliveredAt  = now + windowMs,
                deliveryDays = delivDays,
                delivType    = delivType,
                delivLabel   = delivLabel,
                isPremOrder  = prem,
                status       = "Confirmed",
                items        = items,
                total        = total
            )
            val existing = orders.value.toMutableList()
            existing.add(0, order)
            repo.saveOrders(existing)
            repo.saveCart(emptyList())
            repo.saveConfirmedItems(items)
            repo.clearCheckoutDelivery()
        }
    }

    // ── Orders ────────────────────────────────────────────────────────────────
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val updated = orders.value.map { o ->
                if (o.id == orderId) o.copy(status = "Cancelled") else o
            }
            repo.saveOrders(updated)
        }
    }

    fun requestReturn(orderId: String, reason: String, note: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sdf = SimpleDateFormat("d MMM yyyy", Locale("en", "IN"))
            val updated = orders.value.map { o ->
                if (o.id == orderId) o.copy(
                    status = "Return Requested",
                    returnReason = reason,
                    returnNote = note,
                    returnRequestedAt = now,
                    returnDate = sdf.format(Date(now))
                ) else o
            }
            repo.saveOrders(updated)
        }
    }

    /** Call this periodically to auto-advance statuses and credit refunds */
    fun syncOrderStatuses() {
        viewModelScope.launch {
            var changed = false
            val updated = orders.value.map { o ->
                val live = computeOrderStatus(o)
                if (live != o.status) {
                    changed = true
                    var newOrder = o.copy(status = live)
                    // Credit refund as gift points once when returned
                    if (live == "Returned" && !o.refundCredited) {
                        val refund = o.total.replace("₹", "").replace(",", "").trim().toIntOrNull() ?: 0
                        val cur = giftPoints.value
                        repo.saveGiftPoints(cur + refund)
                        newOrder = newOrder.copy(refundCredited = true, refundAmount = refund)
                    }
                    newOrder
                } else o
            }
            if (changed) repo.saveOrders(updated)
        }
    }

    // ── Admin: update order status ────────────────────────────────────────────
    fun adminUpdateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            val updated = orders.value.map { o ->
                if (o.id == orderId) o.copy(status = newStatus) else o
            }
            repo.saveOrders(updated)
        }
    }

    // ── Admin: save book (add or edit) ────────────────────────────────────────
    fun adminSaveBook(book: Book) {
        viewModelScope.launch {
            val current = customBooks.value.toMutableList()
            val idx = current.indexOfFirst { it.id == book.id }
            if (idx >= 0) current[idx] = book else current.add(book)
            repo.saveCustomBooks(current)
        }
    }

    // ── Admin: delete book ────────────────────────────────────────────────────
    fun adminDeleteBook(bookId: Int) {
        viewModelScope.launch {
            val current = customBooks.value.toMutableList()
            // If it's a custom (non-base) book, remove entirely; otherwise mark _deleted
            val isBase = BASE_BOOKS.any { it.id == bookId }
            if (isBase) {
                val existing = current.find { it.id == bookId }
                if (existing != null) {
                    val idx = current.indexOf(existing)
                    current[idx] = existing.copy(_deleted = true)
                } else {
                    // Create a tombstone
                    val base = BASE_BOOKS.find { it.id == bookId }!!
                    current.add(base.copy(_deleted = true))
                }
            } else {
                current.removeAll { it.id == bookId }
            }
            repo.saveCustomBooks(current)
        }
    }

    // ── Premium subscription ──────────────────────────────────────────────────
    fun activatePremium(pending: PremiumPending) {
        viewModelScope.launch {
            val now    = System.currentTimeMillis()
            val expiry = if (pending.days >= 99999) Long.MAX_VALUE
                         else now + pending.days.toLong() * 24 * 60 * 60 * 1000
            repo.savePremium(PremiumInfo(pending.plan, pending.label, pending.price, pending.days, expiry))
            repo.savePremiumPending(null)
        }
    }

    fun cancelPremium() {
        viewModelScope.launch { repo.savePremium(null) }
    }

    fun savePremiumPending(p: PremiumPending) {
        viewModelScope.launch { repo.savePremiumPending(p) }
    }

    // ── Gift points redemption ────────────────────────────────────────────────
    fun redeemGiftPoints(): Int {
        val pts = giftPoints.value
        if (pts <= 0) return 0
        viewModelScope.launch { repo.saveGiftPoints(0) }
        return pts
    }

    // ── Confirmed items ───────────────────────────────────────────────────────
    fun clearConfirmedItems() {
        viewModelScope.launch { repo.clearConfirmedItems() }
    }
}

// ─── Auth result types ────────────────────────────────────────────────────────
sealed class LoginResult {
    object User               : LoginResult()
    object Admin              : LoginResult()
    object InvalidCredentials : LoginResult()
}

sealed class RegisterResult {
    object Success          : RegisterResult()
    object PasswordTooShort : RegisterResult()
    object PasswordMismatch : RegisterResult()
}
