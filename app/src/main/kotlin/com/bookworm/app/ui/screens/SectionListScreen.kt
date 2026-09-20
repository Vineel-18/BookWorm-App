package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.Routes
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*

@Composable
fun SectionListScreen(
    vm: AppViewModel,
    sectionKey: String,
    navController: NavController
) {
    val allBooks = remember(vm.customBooks.collectAsState().value) { vm.getAllBooks() }

    val books = remember(sectionKey, allBooks) {
        when (sectionKey) {
            "Recommended for You"    -> allBooks.filter { it.tags.contains("recommended") }
            "Bestsellers this Month" -> allBooks.filter { it.tags.contains("bestseller") }
            "New Launches"           -> allBooks.filter { it.tags.contains("new") }
            else                     -> allBooks.filter { it.category == sectionKey }
        }
    }

    Scaffold(
        topBar    = { BookWormTopBar(vm = vm, navController = navController, showBack = true, title = sectionKey) },
        bottomBar = { BookWormBottomNav(navController, Routes.HOME, vm) },
        containerColor = BgColor
    ) { padding ->
        if (books.isEmpty()) {
            EmptyState("📚", "No books found", "Check back later.", null, null)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp, end = 12.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    bottom = padding.calculateBottomPadding() + 12.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onClick = { navController.navigate(Routes.bookDetail(book.id)) },
                        onAuthorClick = null
                    )
                }
            }
        }
    }
}
