package com.bookworm.app.data

import com.bookworm.app.data.model.*
import java.text.SimpleDateFormat
import java.util.*

// ─── Delivery date helper (mirrors _edd in data.js) ──────────────────────────
private fun edd(daysAhead: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysAhead)
    val sdf = SimpleDateFormat("EEE, d MMM", Locale("en", "IN"))
    return sdf.format(cal.time)
}

// ─── All 45 base books (mirrors BOOKS in data.js) ─────────────────────────────
val BASE_BOOKS: List<Book> = listOf(
    // ── SELF-HELP ──────────────────────────────────────────────────────────────
    Book(1, "The Art of Focus", "Arjun Patel", "Self-help",
        listOf("Non-fiction", "Self-Help"), "Paperback", "English",
        399, 599, 4.5, 1240, "#c0392b",
        "THE ART OF FOCUS", "How to Master Your Life for Maximum Productivity", "ARJUN PATEL",
        "Practical guide to mastering focus & boosting productivity every day.",
        edd(4), listOf("recommended"), 50, "Focus Books", "inkwell", "Inkwell Classics"),

    Book(2, "The Art of Learning", "Raj Patel", "Self-help",
        listOf("Non-fiction", "Self-Help"), "Paperback", "English",
        259, 399, 4.3, 980, "#c0392b",
        "THE ART OF LEARNING", "", "RAJ PATEL",
        "Master the mindset and methods for effective lifelong learning.",
        edd(4), listOf("recommended"), 35, "Focus Books", "inkwell", "Inkwell Classics"),

    Book(3, "The Path to Success", "James Wright", "Self-help",
        listOf("Non-fiction", "Self-Help"), "Paperback", "English",
        359, 499, 4.4, 760, "#27ae60",
        "THE PATH TO SUCCESS", "", "JAMES WRIGHT",
        "A practical guide to achieving goals with clarity and confidence.",
        edd(4), listOf("recommended"), 40, "Inspire Press", "inkwell", "Inkwell Classics"),

    Book(7, "Joy of Minimalism", "Daniel Reed", "Self-help",
        listOf("Non-fiction", "Self-Help"), "Paperback", "English",
        149, 299, 4.1, 420, "#f39c12",
        "THE JOY OF MINIMALISM", "Declutter your life to uncover peace, clarity, and joy.", "DANIEL REED",
        "Declutter your life to uncover peace, clarity, and joy.",
        edd(4), listOf("new"), 25, "Simple Life Press", "inkwell", "Inkwell Classics"),

    // ── MYSTERY ────────────────────────────────────────────────────────────────
    Book(4, "The Midnight Hour", "James Adams", "Mystery",
        listOf("Fiction", "Thriller", "Mystery"), "Paperback", "English",
        299, 450, 4.6, 2100, "#1a1a2e",
        "THE MIDNIGHT HOUR", "Haunting tale of a man's journey into shadows", "JAMES ADAMS",
        "Haunting tale of a man's journey & the shadows of a forgotten past.",
        edd(4), listOf("bestseller"), 60, "Dark Reads", "inkwell", "Inkwell Classics"),

    Book(10, "The Silent Witness", "Priya Sharma", "Mystery",
        listOf("Fiction", "Mystery", "Thriller"), "Paperback", "English",
        329, 479, 4.5, 1650, "#2c3e50",
        "THE SILENT WITNESS", "", "PRIYA SHARMA",
        "A detective races to catch a killer before the last witness falls silent.",
        edd(5), listOf("bestseller"), 45, "Dark Reads", "inkwell", "Inkwell Classics"),

    Book(11, "Shadows in the Fog", "Marcus Lee", "Mystery",
        listOf("Fiction", "Mystery"), "eBook", "English",
        129, 249, 4.0, 540, "#34495e",
        "SHADOWS IN THE FOG", "", "MARCUS LEE",
        "A noir thriller set in 1920s Bombay where nothing is as it seems.",
        edd(5), listOf("new"), 20, "Dark Reads", "inkwell", "Inkwell Classics"),

    // ── ROMANCE ────────────────────────────────────────────────────────────────
    Book(5, "Beneath the Stars", "Jessica Martin", "Romance",
        listOf("Fiction", "Romance", "Drama"), "Hard Cover", "English",
        499, 650, 4.7, 1800, "#8e44ad",
        "BENEATH THE STARS", "", "JESSICA MARTIN",
        "A heartwarming tale where two souls discover love across continents.",
        edd(4), listOf("bestseller"), 45, "Love Reads", "inkwell", "Inkwell Classics"),

    Book(12, "A Monsoon Wedding", "Ananya Bose", "Romance",
        listOf("Fiction", "Romance"), "Paperback", "English",
        279, 399, 4.4, 1320, "#9b59b6",
        "A MONSOON WEDDING", "", "ANANYA BOSE",
        "Two families, one wedding, and a love story that rewrites everything.",
        edd(5), listOf("recommended", "bestseller"), 55, "Love Reads", "inkwell", "Inkwell Classics"),

    Book(13, "Last Letter from Paris", "Sophie Laurent", "Romance",
        listOf("Fiction", "Romance", "Historical"), "Hard Cover", "English",
        449, 599, 4.6, 990, "#c0392b",
        "LAST LETTER FROM PARIS", "", "SOPHIE LAURENT",
        "A love letter found 70 years later sets one woman on an unforgettable journey.",
        edd(6), listOf("new"), 30, "Love Reads", "inkwell", "Inkwell Classics"),

    // ── SCIENCE FICTION ────────────────────────────────────────────────────────
    Book(6, "The Final Frontier", "Laura Mitchell", "Science Fiction",
        listOf("Fiction", "Sci-Fi", "Thriller"), "Paperback", "English",
        359, 500, 4.2, 650, "#2980b9",
        "THE FINAL FRONTIER", "", "LAURA MITCHELL",
        "A mission to space uncovers secrets destined to change humanity forever.",
        edd(4), listOf("bestseller"), 30, "Cosmos Books", "inkwell", "Inkwell Classics"),

    Book(14, "Echoes of Tomorrow", "Neil Kapoor", "Science Fiction",
        listOf("Fiction", "Sci-Fi"), "eBook", "English",
        179, 299, 4.3, 720, "#1abc9c",
        "ECHOES OF TOMORROW", "", "NEIL KAPOOR",
        "An AI wakes up 200 years in the future — and humanity needs answers.",
        edd(5), listOf("new", "recommended"), 40, "Cosmos Books", "inkwell", "Inkwell Classics"),

    Book(15, "The Andromeda Drift", "Zara Singh", "Science Fiction",
        listOf("Fiction", "Sci-Fi", "Adventure"), "Hard Cover", "English",
        549, 699, 4.8, 1100, "#16a085",
        "THE ANDROMEDA DRIFT", "", "ZARA SINGH",
        "A crew of misfits drifts into the galaxy's most dangerous corridor.",
        edd(6), listOf("bestseller"), 22, "Cosmos Books", "inkwell", "Inkwell Classics"),

    // ── FANTASY ────────────────────────────────────────────────────────────────
    Book(16, "The Iron Crown", "Rohan Verma", "Fantasy",
        listOf("Fiction", "Fantasy", "Adventure"), "Hard Cover", "English",
        599, 799, 4.7, 2300, "#6c3483",
        "THE IRON CROWN", "", "ROHAN VERMA",
        "A blacksmith's apprentice discovers a crown that could end the world.",
        edd(4), listOf("bestseller"), 35, "Epic Reads", "inkwell", "Inkwell Classics"),

    Book(17, "Daughter of Storms", "Meera Iyer", "Fantasy",
        listOf("Fiction", "Fantasy"), "Paperback", "English",
        349, 499, 4.5, 1560, "#154360",
        "DAUGHTER OF STORMS", "", "MEERA IYER",
        "A storm-summoner must choose between her people and her forbidden power.",
        edd(5), listOf("recommended"), 50, "Epic Reads", "horizon", "Horizon Books"),

    // ── HISTORICAL ─────────────────────────────────────────────────────────────
    Book(18, "Empire of Dust", "Vikram Nair", "Historical",
        listOf("Non-fiction", "Historical", "Drama"), "Hard Cover", "English",
        649, 849, 4.6, 870, "#784212",
        "EMPIRE OF DUST", "", "VIKRAM NAIR",
        "A sweeping saga of the last days of the Mughal Empire.",
        edd(4), listOf("recommended"), 28, "Heritage Press", "horizon", "Horizon Books"),

    Book(19, "The Silk Road Diaries", "Lin Wei", "Historical",
        listOf("Non-fiction", "Historical", "Travel"), "Paperback", "English",
        399, 549, 4.4, 640, "#935116",
        "THE SILK ROAD DIARIES", "", "LIN WEI",
        "Trade routes, empires, and adventurers that shaped the ancient world.",
        edd(6), listOf("new"), 33, "Heritage Press", "horizon", "Horizon Books"),

    // ── BIOGRAPHY ──────────────────────────────────────────────────────────────
    Book(20, "Wings of Fire", "A.P.J. Abdul Kalam", "Biography",
        listOf("Non-fiction", "Biography"), "Paperback", "English",
        199, 299, 4.9, 5200, "#1f618d",
        "WINGS OF FIRE", "", "A.P.J. ABDUL KALAM",
        "The inspiring autobiography of India's beloved missile scientist and president.",
        edd(4), listOf("bestseller", "recommended"), 100, "Vision Books", "horizon", "Horizon Books"),

    Book(21, "My Experiments with Truth", "M.K. Gandhi", "Biography",
        listOf("Non-fiction", "Biography", "Memoir"), "Paperback", "English",
        149, 249, 4.8, 4100, "#4d6a27",
        "MY EXPERIMENTS WITH TRUTH", "", "M.K. GANDHI",
        "Gandhi's autobiography — a candid account of his search for truth and nonviolence.",
        edd(4), listOf("recommended"), 90, "Navajivan Trust", "horizon", "Horizon Books"),

    // ── MEMOIR ─────────────────────────────────────────────────────────────────
    Book(22, "Educated", "Tara Westover", "Memoir",
        listOf("Non-fiction", "Memoir"), "Paperback", "English",
        379, 499, 4.7, 3300, "#117a65",
        "EDUCATED", "", "TARA WESTOVER",
        "A memoir of a woman who grows up in survivalist family and finds her own way to knowledge.",
        edd(5), listOf("bestseller"), 60, "Random House", "horizon", "Horizon Books"),

    Book(23, "The Glass Castle", "Jeannette Walls", "Memoir",
        listOf("Non-fiction", "Memoir"), "eBook", "English",
        199, 349, 4.5, 2700, "#1a5276",
        "THE GLASS CASTLE", "", "JEANNETTE WALLS",
        "A remarkable memoir of resilience, family, and the hunger for something more.",
        edd(5), listOf("recommended"), 40, "Scribner", "horizon", "Horizon Books"),

    // ── TRAVEL ─────────────────────────────────────────────────────────────────
    Book(24, "Into the Wild", "Jon Krakauer", "Travel",
        listOf("Non-fiction", "Travel", "Adventure"), "Paperback", "English",
        299, 429, 4.4, 1900, "#1e8449",
        "INTO THE WILD", "", "JON KRAKAUER",
        "The true story of a young man who abandoned everything to live in the Alaskan wilderness.",
        edd(4), listOf("bestseller"), 48, "Anchor Books", "horizon", "Horizon Books"),

    Book(25, "On the Road Again", "Rahul Mehta", "Travel",
        listOf("Non-fiction", "Travel"), "Paperback", "English",
        249, 379, 4.2, 680, "#1d6a54",
        "ON THE ROAD AGAIN", "", "RAHUL MEHTA",
        "A solo backpacker's diary across 14 countries and 3 continents.",
        edd(6), listOf("new"), 36, "Wander Press", "horizon", "Horizon Books"),

    // ── COOKING ────────────────────────────────────────────────────────────────
    Book(26, "The Spice Route Kitchen", "Kavya Reddy", "Cooking",
        listOf("Non-fiction", "Cooking"), "Hard Cover", "English",
        599, 799, 4.7, 2100, "#d35400",
        "THE SPICE ROUTE KITCHEN", "", "KAVYA REDDY",
        "100 vibrant Indian recipes that tell the story of spice through the ages.",
        edd(4), listOf("bestseller", "recommended"), 65, "Flavour House", "horizon", "Horizon Books"),

    Book(27, "Bake from Scratch", "Preethi Nair", "Cooking",
        listOf("Non-fiction", "Cooking"), "Hard Cover", "English",
        499, 699, 4.5, 1400, "#e67e22",
        "BAKE FROM SCRATCH", "", "PREETHI NAIR",
        "Foolproof baking recipes for breads, cakes, and pastries at home.",
        edd(5), listOf("new"), 45, "Flavour House", "horizon", "Horizon Books"),

    // ── CHILDREN'S ─────────────────────────────────────────────────────────────
    Book(9, "The Lost Kitten", "Emily Parker", "Children's",
        listOf("Fiction", "Children"), "Hardcover", "English",
        339, 450, 4.8, 900, "#e67e22",
        "THE LOST KITTEN", "", "EMILY PARKER",
        "A heartwarming tale of courage, friendship, and feline adventure.",
        edd(4), listOf("new"), 80, "Kids World", "nook", "The Nook"),

    Book(28, "Tara and the Magic Forest", "Sunita Rao", "Children's",
        listOf("Fiction", "Children", "Fantasy"), "Hard Cover", "English",
        299, 399, 4.9, 1500, "#27ae60",
        "TARA AND THE MAGIC FOREST", "", "SUNITA RAO",
        "Tara follows a glowing butterfly into a forest full of talking animals and big dreams.",
        edd(4), listOf("bestseller", "recommended"), 75, "Kids World", "horizon", "Horizon Books"),

    Book(29, "Raju's First Day", "Deepa Menon", "Children's",
        listOf("Fiction", "Children"), "Hardcover", "Hindi",
        199, 299, 4.6, 860, "#f39c12",
        "RAJU'S FIRST DAY", "", "DEEPA MENON",
        "Raju is nervous about school — until he makes the best friend he never expected.",
        edd(5), listOf("new"), 60, "Kids World", "horizon", "Horizon Books"),

    // ── YOUNG ADULT ────────────────────────────────────────────────────────────
    Book(30, "The Last Summer", "Aisha Khan", "Young Adult",
        listOf("Fiction", "Young Adult", "Drama"), "Paperback", "English",
        279, 399, 4.6, 1750, "#e74c3c",
        "THE LAST SUMMER", "", "AISHA KHAN",
        "Four best friends face the summer before college — and nothing will ever be the same.",
        edd(5), listOf("bestseller", "recommended"), 55, "YA Universe", "horizon", "Horizon Books"),

    Book(31, "Neon Rebels", "Dev Khanna", "Young Adult",
        listOf("Fiction", "Young Adult", "Sci-Fi"), "eBook", "English",
        149, 249, 4.3, 920, "#8e44ad",
        "NEON REBELS", "", "DEV KHANNA",
        "In a dystopian city run by corporations, a teen hacker fights back.",
        edd(6), listOf("new"), 30, "YA Universe", "horizon", "Horizon Books"),

    // ── COMICS & GRAPHIC NOVELS ────────────────────────────────────────────────
    Book(32, "Steel City Chronicles", "Aman Tiwari", "Comics & Graphic Novels",
        listOf("Fiction", "Comics", "Superhero"), "Hard Cover", "English",
        449, 599, 4.7, 2800, "#c0392b",
        "STEEL CITY CHRONICLES", "", "AMAN TIWARI",
        "India's first superhero universe — action, politics, and myth collide.",
        edd(4), listOf("bestseller"), 70, "Panel Press", "nook", "The Nook"),

    Book(33, "Mango Summer", "Puja Singh", "Comics & Graphic Novels",
        listOf("Fiction", "Comics", "Slice of Life"), "Paperback", "English",
        349, 479, 4.5, 1100, "#f39c12",
        "MANGO SUMMER", "", "PUJA SINGH",
        "A graphic memoir about growing up in a small Indian town in the 1990s.",
        edd(5), listOf("recommended", "new"), 42, "Panel Press", "nook", "The Nook"),

    // ── POETRY ─────────────────────────────────────────────────────────────────
    Book(34, "River of Words", "Kamala Das", "Poetry",
        listOf("Poetry", "Non-fiction"), "Paperback", "English",
        199, 299, 4.8, 1350, "#2471a3",
        "RIVER OF WORDS", "", "KAMALA DAS",
        "A luminous collection of poems about longing, identity, and womanhood.",
        edd(5), listOf("recommended"), 55, "Verse Press", "nook", "The Nook"),

    Book(35, "Ink & Silence", "Rumi (Trans. Farhan)", "Poetry",
        listOf("Poetry"), "Hard Cover", "English",
        349, 499, 4.9, 3100, "#1a5276",
        "INK & SILENCE", "", "RUMI",
        "Timeless Sufi verses translated with lyrical beauty for the modern reader.",
        edd(4), listOf("bestseller"), 80, "Verse Press", "nook", "The Nook"),

    // ── DRAMA ──────────────────────────────────────────────────────────────────
    Book(36, "The Weight of Rain", "Nalini Mehta", "Drama",
        listOf("Fiction", "Drama"), "Paperback", "English",
        289, 399, 4.4, 870, "#2980b9",
        "THE WEIGHT OF RAIN", "", "NALINI MEHTA",
        "A family torn apart by secrets must decide what — and who — is worth saving.",
        edd(6), listOf("recommended"), 38, "Stage & Page", "nook", "The Nook"),

    Book(37, "Curtain Call", "Ira Dubey", "Drama",
        listOf("Fiction", "Drama"), "eBook", "English",
        179, 299, 4.3, 560, "#1a237e",
        "CURTAIN CALL", "", "IRA DUBEY",
        "Backstage at a theatre, rivalries and romances collide on opening night.",
        edd(5), listOf("new"), 25, "Stage & Page", "nook", "The Nook"),

    // ── SCIENCE ────────────────────────────────────────────────────────────────
    Book(38, "A Brief History of Time", "Stephen Hawking", "Science",
        listOf("Non-fiction", "Science"), "Paperback", "English",
        349, 499, 4.9, 8700, "#1b2631",
        "A BRIEF HISTORY OF TIME", "", "STEPHEN HAWKING",
        "From the Big Bang to black holes — the universe explained for everyone.",
        edd(4), listOf("bestseller", "recommended"), 120, "Bantam Books", "nook", "The Nook"),

    Book(39, "The Gene: An Intimate History", "Siddhartha Mukherjee", "Science",
        listOf("Non-fiction", "Science", "Biography"), "Hard Cover", "English",
        649, 849, 4.7, 2100, "#154360",
        "THE GENE", "", "SIDDHARTHA MUKHERJEE",
        "The epic story of the gene and what it means for our future.",
        edd(5), listOf("recommended"), 40, "Scribner", "nook", "The Nook"),

    // ── PHILOSOPHY ─────────────────────────────────────────────────────────────
    Book(40, "The Bhagavad Gita", "Eknath Easwaran", "Philosophy",
        listOf("Non-fiction", "Philosophy", "Religion"), "Paperback", "English",
        249, 349, 4.9, 9800, "#7d6608",
        "THE BHAGAVAD GITA", "", "EKNATH EASWARAN",
        "The classic dialogue between Arjuna and Krishna, made accessible and profound.",
        edd(4), listOf("bestseller", "recommended"), 150, "Nilgiri Press", "nook", "The Nook"),

    Book(41, "Man's Search for Meaning", "Viktor Frankl", "Philosophy",
        listOf("Non-fiction", "Philosophy", "Memoir"), "Paperback", "English",
        229, 329, 4.8, 6500, "#1c2833",
        "MAN'S SEARCH FOR MEANING", "", "VIKTOR FRANKL",
        "A Holocaust survivor's search for purpose — the foundation of logotherapy.",
        edd(4), listOf("recommended"), 85, "Beacon Press", "nook", "The Nook"),

    // ── RELIGION ───────────────────────────────────────────────────────────────
    Book(42, "The Autobiography of a Yogi", "Paramahansa Yogananda", "Religion",
        listOf("Non-fiction", "Religion", "Biography"), "Paperback", "English",
        199, 299, 4.9, 7400, "#7d3c98",
        "AUTOBIOGRAPHY OF A YOGI", "", "YOGANANDA",
        "The spiritual classic that introduced millions to yoga and Eastern philosophy.",
        edd(4), listOf("bestseller", "recommended"), 110, "Self-Realization Press", "nook", "The Nook"),

    Book(43, "The Power of Now", "Eckhart Tolle", "Religion",
        listOf("Non-fiction", "Religion", "Self-Help"), "Paperback", "English",
        299, 429, 4.7, 5100, "#1e8449",
        "THE POWER OF NOW", "", "ECKHART TOLLE",
        "A guide to spiritual enlightenment through the practice of living in the present moment.",
        edd(5), listOf("recommended"), 70, "New World Library", "nook", "The Nook"),

    // ── LANGUAGE LEARNING ──────────────────────────────────────────────────────
    Book(44, "Fluent Forever", "Gabriel Wyner", "Language Learning",
        listOf("Non-fiction", "Language Learning"), "Paperback", "English",
        349, 499, 4.5, 1600, "#2471a3",
        "FLUENT FOREVER", "", "GABRIEL WYNER",
        "A revolutionary method to learn any language fast using memory science.",
        edd(5), listOf("recommended"), 48, "Harmony Books", "nook", "The Nook"),

    Book(45, "Hindi in 30 Days", "Kavita Sharma", "Language Learning",
        listOf("Non-fiction", "Language Learning"), "Paperback", "Hindi",
        149, 249, 4.3, 1200, "#e74c3c",
        "HINDI IN 30 DAYS", "", "KAVITA SHARMA",
        "A step-by-step beginner's guide to reading, writing, and speaking Hindi.",
        edd(6), listOf("new", "bestseller"), 90, "LinguaEdge", "nook", "The Nook"),

    // ── FICTION ────────────────────────────────────────────────────────────────
    Book(8, "The Vanishing House", "Clara Nelson", "Fiction",
        listOf("Fiction", "Horror"), "eBook", "English",
        99, 199, 3.9, 310, "#2c3e50",
        "THE VANISHING HOUSE", "", "CLARA NELSON",
        "A chilling mystery unfolds within a house that disappears at dawn.",
        edd(4), listOf("new"), 15, "Dark Reads", "inkwell", "Inkwell Classics")
)

val CATEGORIES = listOf(
    "All", "Romance", "Mystery", "Science Fiction", "Fantasy", "Historical",
    "Biography", "Self-help", "Memoir", "Travel", "Cooking", "Children's",
    "Young Adult", "Comics & Graphic Novels", "Poetry", "Drama", "Science",
    "Philosophy", "Religion", "Language Learning", "Fiction"
)

// ─── Stores ───────────────────────────────────────────────────────────────────
val BASE_STORES: List<Store> = listOf(
    Store(
        id = "inkwell", name = "Inkwell Classics", tagline = "Where every story begins",
        emoji = "🏛️", coverColor = "#1a1a2e", accentColor = "#e8943a",
        bookIds = listOf(1, 2, 3, 7, 4, 10, 11, 5, 12, 13, 6, 14, 15, 8, 16),
        categories = listOf("Self-help", "Mystery", "Romance", "Science Fiction", "Fiction", "Fantasy"),
        description = "Your home for bestselling fiction, gripping mysteries, and life-changing self-help books. Inkwell Classics stocks the titles everyone is talking about.",
        catalog = listOf(
            StoreSection("Personal Growth", listOf(1, 2, 3, 7)),
            StoreSection("Mystery & Thriller", listOf(4, 10, 11)),
            StoreSection("Romance", listOf(5, 12, 13)),
            StoreSection("Science Fiction", listOf(6, 14, 15)),
            StoreSection("Fiction & Fantasy", listOf(8, 16))
        ),
        policy = StorePolicy(
            returns = "15-day easy returns on all paperbacks. eBooks are non-refundable.",
            delivery = "Free delivery on orders above ₹500. Standard delivery in 2–4 business days.",
            payment = "Accepts all UPI, credit/debit cards, net banking and cash on delivery.",
            warranty = "All hardcovers come with a 30-day quality guarantee.",
            loyalty = "Earn 1 Inkwell Point per ₹10 spent. Redeem 100 points for ₹50 off.",
            contact = "support@inkwellclassics.com · Mon–Sat, 9 AM–7 PM"
        ),
        founded = "2018", location = "Mumbai, Maharashtra", rating = 4.7, totalSales = 12400
    ),
    Store(
        id = "horizon", name = "Horizon Books", tagline = "Knowledge without limits",
        emoji = "📖", coverColor = "#0d3b1e", accentColor = "#2ecc71",
        bookIds = listOf(17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31),
        categories = listOf("Fantasy", "Historical", "Biography", "Memoir", "Travel", "Cooking", "Children's", "Young Adult"),
        description = "Horizon Books celebrates the stories that expand your world — epic fantasy, real-life biographies, travel memoirs, and books for the whole family.",
        catalog = listOf(
            StoreSection("Fantasy & Adventure", listOf(17)),
            StoreSection("History & Heritage", listOf(18, 19)),
            StoreSection("Biography & Memoir", listOf(20, 21, 22, 23)),
            StoreSection("Travel & Exploration", listOf(24, 25)),
            StoreSection("Food & Cooking", listOf(26, 27)),
            StoreSection("Children's Books", listOf(28, 29)),
            StoreSection("Young Adult", listOf(30, 31))
        ),
        policy = StorePolicy(
            returns = "30-day returns on all formats. Return shipping is free for defective items.",
            delivery = "Free delivery on orders above ₹400. Express delivery available in metros.",
            payment = "UPI, cards, wallets, EMI on orders above ₹1000.",
            warranty = "All children's books are printed with child-safe non-toxic ink.",
            loyalty = "Horizon Rewards: earn 2 points per ₹10. Points never expire.",
            contact = "hello@horizonbooks.in · Daily, 8 AM–9 PM"
        ),
        founded = "2015", location = "Bangalore, Karnataka", rating = 4.8, totalSales = 18700
    ),
    Store(
        id = "nook", name = "The Nook", tagline = "Niche reads, deep thoughts",
        emoji = "✨", coverColor = "#1c0533", accentColor = "#9b59b6",
        bookIds = listOf(32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 9),
        categories = listOf("Comics & Graphic Novels", "Poetry", "Drama", "Science", "Philosophy", "Religion", "Language Learning", "Children's"),
        description = "The Nook is a specialty store for curious minds — comics, poetry, philosophy, science, spirituality, and language books that you won't find everywhere.",
        catalog = listOf(
            StoreSection("Comics & Graphic Novels", listOf(32, 33)),
            StoreSection("Poetry", listOf(34, 35)),
            StoreSection("Drama", listOf(36, 37)),
            StoreSection("Science & Discovery", listOf(38, 39)),
            StoreSection("Philosophy", listOf(40, 41)),
            StoreSection("Spirituality & Religion", listOf(42, 43)),
            StoreSection("Language Learning", listOf(44, 45)),
            StoreSection("Children's Specials", listOf(9))
        ),
        policy = StorePolicy(
            returns = "7-day returns on physical books in original condition. No returns on digital.",
            delivery = "Free delivery on orders above ₹600. Specialty items may take 3–6 days.",
            payment = "UPI and cards accepted. Gift cards available.",
            warranty = "All comic hardcovers are sealed and graded. Quality assured.",
            loyalty = "Nook Club members get 10% off every order. ₹99/month subscription.",
            contact = "thenook@reads.in · Tue–Sun, 10 AM–6 PM"
        ),
        founded = "2020", location = "Pune, Maharashtra", rating = 4.6, totalSales = 9200
    )
)
