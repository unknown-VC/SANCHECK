package com.unknown.sancheck.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unknown.sancheck.data.local.entity.Book
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: InfoViewModel = viewModel()
) {
    val totalBookCount by viewModel.totalBookCount.collectAsState()
    val authorCount by viewModel.authorCount.collectAsState()
    val translatorCount by viewModel.translatorCount.collectAsState()
    val publisherCount by viewModel.publisherCount.collectAsState()
    val booksThisYear by viewModel.booksThisYear.collectAsState()
    val postItCount by viewModel.postItCount.collectAsState()
    val reviewCount by viewModel.reviewCount.collectAsState()
    val quoteCount by viewModel.quoteCount.collectAsState()
    val tagCount by viewModel.tagCount.collectAsState()
    val bookshelves by viewModel.bookshelves.collectAsState()
    val booksByPubDate by viewModel.booksByPubDate.collectAsState()
    val booksByRating by viewModel.booksByRating.collectAsState()
    val booksByPrice by viewModel.booksByPrice.collectAsState()
    val booksByPageCount by viewModel.booksByPageCount.collectAsState()
    val authorStats by viewModel.authorStats.collectAsState()

    var expandedSection by remember { mutableStateOf<String?>(null) }
    var showAddShelf by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("정보") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // This year's books
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedSection = if (expandedSection == "year") null else "year" }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("올해의 책", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${booksThisYear.size}권", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (expandedSection == "year") {
                items(booksThisYear) { book ->
                    BookListItem(book = book, onClick = { onNavigateToDetail(book.id) })
                }
            }

            // Stats section 1: Counts
            item {
                Text("통계", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                StatItem("총 권수", totalBookCount.toString(), onClick = { expandedSection = if (expandedSection == "total") null else "total" })
            }
            if (expandedSection == "total") {
                items(booksByPubDate) { book ->
                    BookListItem(book = book, onClick = { onNavigateToDetail(book.id) })
                }
            }

            item { StatItem("지은이", authorCount.toString(), onClick = { expandedSection = if (expandedSection == "author") null else "author" }) }
            if (expandedSection == "author") {
                items(authorStats) { stat ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Text(stat.author, modifier = Modifier.weight(1f))
                            Text("${stat.cnt}권")
                        }
                    }
                }
            }

            item { StatItem("옮긴이", translatorCount.toString(), onClick = { expandedSection = if (expandedSection == "translator") null else "translator" }) }
            item { StatItem("출판사", publisherCount.toString(), onClick = { expandedSection = if (expandedSection == "publisher") null else "publisher" }) }

            // Stats section 2: Sort views
            item {
                Text("정렬 보기", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }

            item { StatItem("출간일순", "", onClick = { expandedSection = if (expandedSection == "pubdate") null else "pubdate" }) }
            if (expandedSection == "pubdate") {
                items(booksByPubDate) { book ->
                    BookListItem(book = book, subtitle = book.pubDate, onClick = { onNavigateToDetail(book.id) })
                }
            }

            item { StatItem("별점순", "", onClick = { expandedSection = if (expandedSection == "rating") null else "rating" }) }
            if (expandedSection == "rating") {
                items(booksByRating) { book ->
                    BookListItem(book = book, subtitle = "★ ${book.rating}", onClick = { onNavigateToDetail(book.id) })
                }
            }

            item { StatItem("가격순", "", onClick = { expandedSection = if (expandedSection == "price") null else "price" }) }
            if (expandedSection == "price") {
                items(booksByPrice) { book ->
                    BookListItem(book = book, subtitle = "${book.priceStandard}원", onClick = { onNavigateToDetail(book.id) })
                }
            }

            item { StatItem("쪽수순", "", onClick = { expandedSection = if (expandedSection == "pages") null else "pages" }) }
            if (expandedSection == "pages") {
                items(booksByPageCount) { book ->
                    BookListItem(book = book, subtitle = "${book.pageCount}쪽", onClick = { onNavigateToDetail(book.id) })
                }
            }

            // Stats section 3: Annotations
            item {
                Text("기록", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("서가", style = MaterialTheme.typography.titleMedium)
                    Text("${bookshelves.size}개", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { showAddShelf = true }) {
                        Icon(Icons.Default.Add, contentDescription = "서가 추가")
                    }
                }
            }
            items(bookshelves) { shelf ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text(shelf.name, modifier = Modifier.weight(1f))
                        if (shelf.id != 1L) {
                            IconButton(
                                onClick = { coroutineScope.launch { viewModel.deleteBookshelf(shelf) } },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item { StatItem("포스트잇", postItCount.toString()) }
            item { StatItem("서평", reviewCount.toString()) }
            item { StatItem("문장", quoteCount.toString()) }
            item { StatItem("해시 태그", tagCount.toString()) }
        }
    }

    if (showAddShelf) {
        var shelfName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddShelf = false },
            title = { Text("새 서가 추가") },
            text = {
                OutlinedTextField(
                    value = shelfName,
                    onValueChange = { shelfName = it },
                    placeholder = { Text("서가 이름") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (shelfName.isNotBlank()) {
                        coroutineScope.launch { viewModel.addBookshelf(shelfName) }
                        showAddShelf = false
                    }
                }) { Text("추가") }
            },
            dismissButton = {
                TextButton(onClick = { showAddShelf = false }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            if (value.isNotEmpty()) {
                Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            if (onClick != null) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun BookListItem(
    book: Book,
    subtitle: String = book.author,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
