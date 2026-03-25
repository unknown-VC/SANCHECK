package com.unknown.sancheck.ui.bookshelf

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.ui.components.ShelfRow

@Composable
fun SpineView(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    editMode: Boolean = false,
    onDeleteBook: ((Book) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Split books into rows of ~8 books per shelf
    val booksPerShelf = 8
    val shelves = books.chunked(booksPerShelf)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = shelves.size.coerceAtLeast(3), // Show at least 3 empty shelves
            key = { it }
        ) { index ->
            ShelfRow(
                books = shelves.getOrElse(index) { emptyList() },
                onBookClick = { book ->
                    if (editMode && onDeleteBook != null) {
                        onDeleteBook(book)
                    } else {
                        onBookClick(book)
                    }
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
