# BookWorm 📚

A fully functional Android bookstore app built with **100% Kotlin + Jetpack Compose**. No HTML, CSS, or JavaScript — pure native Android.

---

## What is BookWorm?

BookWorm is a mobile bookstore where users can browse books, add them to cart, and place orders. It has a full shopping flow, order tracking, wishlist, premium membership, and an admin panel — all built natively in Kotlin.

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Navigation | Navigation Compose |
| Data Storage | Jetpack DataStore |
| Build Tool | Gradle (Kotlin DSL) |
| Min Android | API 24 (Android 7.0) |

---

## Features

- Login, Register, Guest access and Admin login
- Browse books by category, search and filter
- Book detail page with cover, price, description and reviews
- Cart with coupon codes, gift points and delivery options
- Full payment and order confirmation flow
- Order tracking from Confirmed to Delivered
- Wishlist, User Dashboard, Premium Membership
- Admin panel to manage books and orders
- Dark theme throughout

---

## Project Structure

```
BookWormApp/
├── data/
│   ├── BookData.kt          — All book data
│   ├── BookWormRepository.kt — Saves and loads user data
│   └── model/               — Data models (Book, User, Order etc.)
├── ui/
│   ├── theme/               — App colors and typography
│   ├── components/          — Reusable UI pieces (cards, navbar etc.)
│   └── screens/             — One file per screen (Login, Home, Cart etc.)
├── AppViewModel.kt          — App-wide state (cart, user, orders)
├── BookWormApp.kt           — Navigation between screens
└── MainActivity.kt          — App entry point
```

---

## Screens

| Screen | What it does |
|---|---|
| Login | Sign in, register or continue as guest |
| Home | Browse books by category, search and filter |
| Book Detail | View book info, add to cart or wishlist |
| Cart | Review items, apply coupons, choose delivery |
| Payment | Enter payment details and place order |
| Orders | Track all orders and raise returns |
| Wishlist | Saved books |
| Dashboard | User profile and account settings |
| Premium | Upgrade for free delivery and perks |
| Admin | Add, edit and manage books and orders |
| Stores | Find nearby bookstores |

---

## Login Credentials

| Role | Email | Password |
|---|---|---|
| Admin | admin@bookworm.com | admin123 |
| User | any email | any password |
| Guest | — | tap Continue as Guest |

---

## How to Run

1. Clone the repo
2. Open the `BookWormApp` folder in Android Studio
3. Let Gradle sync finish
4. Hit **Run ▶** on any device or emulator (Android 7+)

---

## License

MIT
