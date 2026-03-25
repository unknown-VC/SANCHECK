package com.unknown.sancheck.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unknown.sancheck.data.local.entity.Bookshelf

@Composable
fun BookshelfDropdown(
    bookshelves: List<Bookshelf>,
    selectedShelfId: Long,
    onShelfSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = if (selectedShelfId == 0L) {
        "모든 책"
    } else {
        bookshelves.find { it.id == selectedShelfId }?.name ?: "모든 책"
    }

    Box(modifier = modifier) {
        TextButton(onClick = { expanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "서가 선택",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("모든 책") },
                onClick = {
                    onShelfSelected(0L)
                    expanded = false
                }
            )
            bookshelves.forEach { shelf ->
                DropdownMenuItem(
                    text = { Text(shelf.name) },
                    onClick = {
                        onShelfSelected(shelf.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
