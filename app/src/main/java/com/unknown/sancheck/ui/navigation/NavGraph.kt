package com.unknown.sancheck.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unknown.sancheck.ui.addbook.ManualAddScreen
import com.unknown.sancheck.ui.bookshelf.BookshelfScreen
import com.unknown.sancheck.ui.detail.BookDetailScreen
import com.unknown.sancheck.ui.info.InfoScreen
import com.unknown.sancheck.ui.scanner.ScannerScreen
import com.unknown.sancheck.ui.search.SearchScreen
import com.unknown.sancheck.ui.settings.SettingsScreen

object Routes {
    const val BOOKSHELF = "bookshelf"
    const val SCANNER = "scanner"
    const val SEARCH = "search"
    const val MANUAL_ADD = "manual_add"
    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val INFO = "info"
    const val SETTINGS = "settings"

    fun bookDetail(bookId: Long) = "book_detail/$bookId"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.BOOKSHELF
    ) {
        composable(Routes.BOOKSHELF) {
            BookshelfScreen(
                onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                onNavigateToSearch = { navController.navigate(Routes.SEARCH) },
                onNavigateToManualAdd = { navController.navigate(Routes.MANUAL_ADD) },
                onNavigateToDetail = { bookId ->
                    navController.navigate(Routes.bookDetail(bookId))
                },
                onNavigateToInfo = { navController.navigate(Routes.INFO) }
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { bookId ->
                    navController.popBackStack()
                    navController.navigate(Routes.bookDetail(bookId))
                }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { bookId ->
                    navController.popBackStack()
                    navController.navigate(Routes.bookDetail(bookId))
                }
            )
        }

        composable(Routes.MANUAL_ADD) {
            ManualAddScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookSaved = { bookId ->
                    navController.popBackStack()
                    navController.navigate(Routes.bookDetail(bookId))
                }
            )
        }

        composable(
            Routes.BOOK_DETAIL,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: return@composable
            BookDetailScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INFO) {
            InfoScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToDetail = { bookId ->
                    navController.navigate(Routes.bookDetail(bookId))
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
