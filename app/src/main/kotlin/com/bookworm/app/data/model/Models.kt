package com.bookworm.app.data.model

import kotlinx.serialization.Serializable

// ─── Book ─────────────────────────────────────────────────────────────────────
@Serializable
data class Book(
    val id            : Int,
    val title         : String,
    val author        : String,
    val category      : String,
    val genres        : List<String>    = emptyList(),
    val format        : String          = "Paperback",
    val language      : String          = "English",
    val price         : Int,
    val originalPrice : Int             = price,
    val rating        : Double          = 4.0,
    val sales         : Int             = 0,
    val coverBg       : String          = "#333333",
    val coverText     : String          = "",
    val coverSubtext  : String          = "",
    val coverAuthorDisplay: String      = "",
    val desc          : String          = "",
    val delivery      : String          = "",
    val tags          : List<String>    = emptyList(),
    val stock         : Int             = 50,
    val brand         : String          = "",
    val storeId       : String          = "",
    val publisher     : String          = "",
    // admin-only: mark as deleted override
    val _deleted      : Boolean         = false
)

// ─── User ─────────────────────────────────────────────────────────────────────
@Serializable
data class User(
    val email : String = "",
    val name  : String = "User",
    val role  : String = "guest"   // "guest" | "registered" | "admin"
)

// ─── CartItem ─────────────────────────────────────────────────────────────────
@Serializable
data class CartItem(
    val id  : Int,
    val qty : Int = 1
)

// ─── Delivery address ─────────────────────────────────────────────────────────
@Serializable
data class Address(
    val first   : String = "",
    val last    : String = "",
    val street  : String = "",
    val city    : String = "",
    val pin     : String = "",
    val state   : String = "",
    val country : String = "India",
    val email   : String = "",
    val phone   : String = ""
) {
    fun oneLiner(): String =
        listOf(street, city, state, pin).filter { it.isNotBlank() }.joinToString(", ")

    fun isValid(): Boolean = first.isNotBlank() && street.isNotBlank() && city.isNotBlank()
}

// ─── Order ────────────────────────────────────────────────────────────────────
@Serializable
data class OrderItem(
    val id        : Int    = 0,
    val title     : String = "",
    val author    : String = "",
    val category  : String = "",
    val price     : Int    = 0,
    val qty       : Int    = 1,
    val coverBg   : String = "#333333"
)

@Serializable
data class Order(
    val id                 : String         = "",
    val date               : String         = "",
    val placedAt           : Long           = 0L,
    val packedAt           : Long           = 0L,
    val shippedAt          : Long           = 0L,
    val outAt              : Long           = 0L,
    val deliveredAt        : Long           = 0L,
    val deliveryDays       : Int            = 6,
    val delivType          : String         = "standard",
    val delivLabel         : String         = "Standard (4–6 days)",
    val isPremOrder        : Boolean        = false,
    val status             : String         = "Confirmed",
    val items              : List<OrderItem> = emptyList(),
    val total              : String         = "₹0",
    // return fields
    val returnReason       : String         = "",
    val returnNote         : String         = "",
    val returnRequestedAt  : Long           = 0L,
    val returnDate         : String         = "",
    val refundCredited     : Boolean        = false,
    val refundAmount       : Int            = 0
)

// ─── Review ───────────────────────────────────────────────────────────────────
@Serializable
data class Review(
    val user   : String = "Anonymous",
    val text   : String = "",
    val rating : Int    = 5
)

// ─── Premium subscription ─────────────────────────────────────────────────────
@Serializable
data class PremiumInfo(
    val plan   : String = "annual",   // "monthly" | "annual" | "lifetime"
    val label  : String = "Annual",
    val price  : String = "₹999/yr",
    val days   : Int    = 365,
    val expiry : Long   = 0L           // 0 = lifetime sentinel
)

// ─── Pending premium purchase (from plan selection → payment) ─────────────────
@Serializable
data class PremiumPending(
    val plan  : String = "annual",
    val label : String = "Annual",
    val price : String = "₹999/yr",
    val days  : Int    = 365
)

// ─── Store ────────────────────────────────────────────────────────────────────
@Serializable
data class StoreSection(
    val section : String,
    val ids     : List<Int>
)

@Serializable
data class StorePolicy(
    val returns  : String = "",
    val delivery : String = "",
    val payment  : String = "",
    val warranty : String = "",
    val loyalty  : String = "",
    val contact  : String = ""
)

@Serializable
data class Store(
    val id          : String,
    val name        : String,
    val tagline     : String,
    val emoji       : String,
    val coverColor  : String,
    val accentColor : String,
    val bookIds     : List<Int>,
    val categories  : List<String>,
    val description : String,
    val catalog     : List<StoreSection>,
    val policy      : StorePolicy,
    val founded     : String,
    val location    : String,
    val rating      : Double,
    val totalSales  : Int
)

// ─── Delivery options ─────────────────────────────────────────────────────────
data class DeliveryOption(
    val key        : String,
    val label      : String,
    val desc       : String,
    val fee        : Int?,    // null = dynamic (standard — free over ₹400 else ₹40)
    val badge      : String?, // "fast" | "same" | "prem" | null
    val days       : Int
)

val DELIVERY_OPTS = mapOf(
    "standard"  to DeliveryOption("standard",  "Standard Delivery", "4–6 business days",   null, null,   6),
    "fast"      to DeliveryOption("fast",      "Fast Delivery",     "2–3 business days",   79,   "fast", 3),
    "sameday"   to DeliveryOption("sameday",   "Same-Day Delivery", "Delivered by tonight", 149, "same", 0),
    "prem_same" to DeliveryOption("prem_same", "Same-Day Delivery", "Delivered by tonight", 0,   "prem", 0),
    "prem_next" to DeliveryOption("prem_next", "Next-Day Delivery", "Delivered tomorrow",   0,   "prem", 1)
)

fun resolvedDelivFee(key: String, isPremium: Boolean, subtotal: Int): Int {
    if (isPremium) return 0
    return when (key) {
        "standard" -> if (subtotal > 400) 0 else 40
        "fast"     -> 79
        "sameday"  -> 149
        else       -> 0
    }
}

// ─── ORDER_STAGES ─────────────────────────────────────────────────────────────
val ORDER_STAGES = listOf("Confirmed", "Packed", "Shipped", "Out for Delivery", "Delivered")

fun stageIndex(status: String): Int {
    val idx = ORDER_STAGES.indexOf(status)
    return if (idx == -1) 0 else idx
}

fun computeOrderStatus(order: Order): String {
    val s = order.status.lowercase()
    val now = System.currentTimeMillis()
    val minMs = 60_000L

    if (s == "return requested") {
        val returnedAt = if (order.returnRequestedAt > 0) order.returnRequestedAt + 2 * minMs else 0L
        if (returnedAt > 0 && now >= returnedAt) return "Returned"
        return order.status
    }
    if (s == "cancelled" || s == "returned") return order.status

    if (order.placedAt > 0 && order.deliveredAt > 0) {
        return when {
            now >= order.deliveredAt -> "Delivered"
            now >= order.outAt       -> "Out for Delivery"
            now >= order.shippedAt   -> "Shipped"
            now >= order.packedAt    -> "Packed"
            else                     -> "Confirmed"
        }
    }
    return order.status.ifBlank { "Confirmed" }
}

fun isReturnEligible(order: Order): Boolean {
    val s = order.status.lowercase()
    if (s != "delivered") return false
    val base = order.deliveredAt
    if (base > 0) {
        val diff = System.currentTimeMillis() - base
        if (diff > 7L * 24 * 60 * 60 * 1000) return false
    }
    return true
}

val RETURN_REASONS = listOf(
    "Wrong book delivered",
    "Damaged / torn pages",
    "Duplicate order placed",
    "Book quality not as expected",
    "Changed my mind",
    "Other"
)

// ─── Cover icons ──────────────────────────────────────────────────────────────
val COVER_ICONS: Map<Int, String> = mapOf(
    1 to "🎯", 2 to "💡", 3 to "🌟", 7 to "🏠",
    4 to "🌙", 10 to "🔍", 11 to "🌫️",
    5 to "⭐", 12 to "💐", 13 to "💌",
    6 to "🚀", 14 to "🤖", 15 to "🛸",
    16 to "👑", 17 to "⚡",
    18 to "🏛️", 19 to "🗺️",
    20 to "✈️", 21 to "🕊️",
    22 to "📓", 23 to "🏚️",
    24 to "🌲", 25 to "🎒",
    26 to "🌶️", 27 to "🍰",
    9 to "🐱", 28 to "🦋", 29 to "🎒",
    30 to "☀️", 31 to "💻",
    32 to "🦸", 33 to "🥭",
    34 to "🌊", 35 to "✒️",
    36 to "🌧️", 37 to "🎭",
    38 to "🔭", 39 to "🧬",
    40 to "🪷", 41 to "🕯️",
    42 to "🧘", 43 to "🌅",
    44 to "🗣️", 45 to "📝",
    8 to "👁️"
)
