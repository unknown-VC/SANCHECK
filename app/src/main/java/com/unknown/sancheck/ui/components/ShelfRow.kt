package com.unknown.sancheck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.ui.theme.ShelfWood
import com.unknown.sancheck.ui.theme.ShelfWoodDark
import com.unknown.sancheck.ui.theme.ShelfWoodLight

@Composable
fun ShelfRow(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Books on the shelf
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            items(books, key = { it.id }) { book ->
                BookSpine(
                    book = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }

        // Wooden shelf bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ShelfWoodLight, ShelfWood, ShelfWoodDark)
                    )
                )
        )

        // Shadow beneath shelf
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ShelfWoodDark.copy(alpha = 0.5f),
                            ShelfWoodDark.copy(alpha = 0f)
                        )
                    )
                )
        )
    }
}
