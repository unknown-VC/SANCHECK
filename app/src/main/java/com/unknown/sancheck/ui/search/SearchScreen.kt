package com.unknown.sancheck.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.unknown.sancheck.data.remote.AladinItem
import com.unknown.sancheck.ui.components.DuplicateDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    LaunchedEffect(uiState.savedBookId) {
        uiState.savedBookId?.let { onNavigateToDetail(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("인터넷 검색") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                placeholder = { Text("제목, 저자, ISBN 검색") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.search(searchText) }) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search(searchText) })
            )

            // Results
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.results) { item ->
                            SearchResultItem(
                                item = item,
                                onClick = { viewModel.selectItem(item) }
                            )
                        }
                    }
                }
            }
        }

        // Selected item confirmation
        uiState.selectedItem?.let { item ->
            if (!uiState.showDuplicate) {
                AlertDialog(
                    onDismissRequest = { viewModel.clearSelection() },
                    title = { Text("도서 추가") },
                    text = {
                        Column {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.author, style = MaterialTheme.typography.bodyMedium)
                            Text(item.publisher, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.saveSelectedItem() }) {
                            Text("저장")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.clearSelection() }) {
                            Text("취소")
                        }
                    }
                )
            }
        }

        // Duplicate dialog
        if (uiState.showDuplicate && uiState.existingBook != null) {
            DuplicateDialog(
                bookTitle = uiState.existingBook!!.title,
                currentQuantity = uiState.existingBook!!.quantity,
                onDismiss = { viewModel.dismissDuplicate() },
                onIncrementQuantity = { viewModel.incrementQuantity() },
                onCancel = { viewModel.dismissDuplicate() }
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    item: AladinItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.cover,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.author,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    item.publisher,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.priceStandard > 0) {
                    Text(
                        "${item.priceStandard}원",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
