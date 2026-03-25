package com.unknown.sancheck.ui.bookshelf

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unknown.sancheck.ui.components.AddBookDialog
import com.unknown.sancheck.ui.components.BookshelfDropdown
import com.unknown.sancheck.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToManualAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToInfo: () -> Unit,
    viewModel: BookshelfViewModel = viewModel()
) {
    val books by viewModel.books.collectAsState()
    val bookshelves by viewModel.bookshelves.collectAsState()
    val selectedShelfId by viewModel.selectedShelfId.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showMillieOnly by viewModel.showMillieOnly.collectAsState()
    val showWelaaaOnly by viewModel.showWelaaaOnly.collectAsState()
    val editMode by viewModel.editMode.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<com.unknown.sancheck.data.local.entity.Book?>(null) }

    Scaffold(
        topBar = {
            Column {
                // Main top bar
                TopAppBar(
                    title = {
                        BookshelfDropdown(
                            bookshelves = bookshelves,
                            selectedShelfId = selectedShelfId,
                            onShelfSelected = { viewModel.selectShelf(it) }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "책 추가")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Text(
                                if (editMode) "완료" else "편집",
                                style = MaterialTheme.typography.labelLarge,
                                color = PrimaryBlue
                            )
                        }
                        IconButton(onClick = onNavigateToInfo) {
                            Icon(Icons.Default.Info, contentDescription = "정보")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                // Sub-bar: search + view toggle + filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search toggle
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }

                    if (showSearchBar) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("제목/저자/출판사 검색") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall,
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "지우기")
                                    }
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // View mode toggle
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            if (viewMode == 0) Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "보기 모드 전환"
                        )
                    }
                }

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showMillieOnly,
                        onClick = { viewModel.toggleMillieFilter() },
                        label = { Text("밀리 보유", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = showWelaaaOnly,
                        onClick = { viewModel.toggleWelaaaFilter() },
                        label = { Text("윌라 보유", style = MaterialTheme.typography.labelSmall) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${books.size}권",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (books.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "책장이 비어있습니다",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "+ 버튼을 눌러 책을 추가해보세요",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                when (viewMode) {
                    0 -> SpineView(
                        books = books,
                        onBookClick = { onNavigateToDetail(it.id) },
                        editMode = editMode,
                        onDeleteBook = { bookToDelete = it }
                    )
                    1 -> CoverGridView(
                        books = books,
                        onBookClick = { onNavigateToDetail(it.id) },
                        editMode = editMode,
                        onDeleteBook = { bookToDelete = it }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddBookDialog(
            onDismiss = { showAddDialog = false },
            onScanBarcode = onNavigateToScanner,
            onInternetSearch = onNavigateToSearch,
            onManualInput = onNavigateToManualAdd
        )
    }

    bookToDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToDelete = null },
            title = { Text("도서 삭제") },
            text = { Text("\"${book.title}\"을(를) 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook(book)
                    bookToDelete = null
                }) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { bookToDelete = null }) { Text("취소") }
            }
        )
    }
}
