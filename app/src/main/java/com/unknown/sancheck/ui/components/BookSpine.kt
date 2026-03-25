package com.unknown.sancheck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.ui.theme.SpineColors

@Composable
fun BookSpine(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spineColor = SpineColors[(book.id % SpineColors.size).toInt()]
    val spineWidth = (36 + (book.pageCount / 50).coerceIn(0, 20)).dp
    val spineHeight = 160.dp

    Box(
        modifier = modifier
            .width(spineWidth)
            .height(spineHeight)
            .clip(RoundedCornerShape(2.dp))
            .background(spineColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vertical text for spine
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            book.title.take(12).forEach { char ->
                Text(
                    text = char.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}
