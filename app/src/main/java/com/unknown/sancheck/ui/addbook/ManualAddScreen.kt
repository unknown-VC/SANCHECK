package com.unknown.sancheck.ui.addbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unknown.sancheck.ui.components.BookshelfDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddScreen(
    onNavigateBack: () -> Unit,
    onBookSaved: (Long) -> Unit,
    viewModel: ManualAddViewModel = viewModel()
) {
    val bookshelves by viewModel.bookshelves.collectAsState()
    val savedBookId by viewModel.savedBookId.collectAsState()

    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var translator by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var pubDate by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }
    var selectedShelfId by remember { mutableStateOf(1L) }

    LaunchedEffect(savedBookId) {
        savedBookId?.let { onBookSaved(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("직접 입력") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveBook(
                                title, subtitle, author, translator,
                                publisher, pubDate, pageCount, price,
                                isbn, coverUrl, selectedShelfId
                            )
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("저장")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("부제") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("저자") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = translator,
                onValueChange = { translator = it },
                label = { Text("옮긴이") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = publisher,
                onValueChange = { publisher = it },
                label = { Text("출판사") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = pubDate,
                onValueChange = { pubDate = it },
                label = { Text("출간일 (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    label = { Text("쪽수") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("가격") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = coverUrl,
                onValueChange = { coverUrl = it },
                label = { Text("표지 이미지 URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Bookshelf selector
            Text("서가", style = MaterialTheme.typography.labelLarge)
            BookshelfDropdown(
                bookshelves = bookshelves,
                selectedShelfId = selectedShelfId,
                onShelfSelected = { selectedShelfId = it }
            )
        }
    }
}
