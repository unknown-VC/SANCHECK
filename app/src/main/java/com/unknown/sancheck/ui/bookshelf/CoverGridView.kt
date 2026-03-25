package com.unknown.sancheck.ui.bookshelf

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.ui.components.BookCoverCard

@Composable
fun CoverGridView(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    editMode: Boolean = false,
    onDeleteBook: ((Book) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books, key = { it.id }) { book ->
            Box {
                BookCoverCard(
                    book = book,
                    onClick = {
                        if (editMode && onDeleteBook != null) {
                            onDeleteBook(book)
                        } else {
                            onBookClick(book)
                        }
                    }
                )
                if (editMode) {
                    IconButton(
                        onClick = { onDeleteBook?.invoke(book) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
