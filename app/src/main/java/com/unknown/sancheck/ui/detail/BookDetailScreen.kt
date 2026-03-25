package com.unknown.sancheck.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.unknown.sancheck.ui.components.StarRating
import com.unknown.sancheck.ui.theme.*
import com.unknown.sancheck.util.DeepLinkHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onNavigateBack: () -> Unit,
    viewModel: BookDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val book by viewModel.book.collectAsState()
    val postIts by viewModel.postIts.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val hashTags by viewModel.hashTags.collectAsState()
    val bookshelves by viewModel.bookshelves.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddPostIt by remember { mutableStateOf(false) }
    var showAddReview by remember { mutableStateOf(false) }
    var showAddQuote by remember { mutableStateOf(false) }
    var showAddTag by remember { mutableStateOf(false) }
    var memoText by remember { mutableStateOf("") }
    var memoInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    LaunchedEffect(book) {
        if (!memoInitialized && book != null) {
            memoText = book!!.memo
            memoInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제")
                    }
                }
            )
        }
    ) { padding ->
        val currentBook = book
        if (currentBook == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cover + basic info
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (currentBook.coverUrl.isNotEmpty()) {
                    AsyncImage(
                        model = currentBook.coverUrl,
                        contentDescription = currentBook.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(currentBook.title, style = MaterialTheme.typography.titleLarge)
                    if (currentBook.subtitle.isNotEmpty()) {
                        Text(currentBook.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currentBook.author, style = MaterialTheme.typography.bodyMedium)
                    if (currentBook.translator.isNotEmpty()) {
                        Text("옮긴이: ${currentBook.translator}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(currentBook.publisher, style = MaterialTheme.typography.bodySmall)
                    if (currentBook.pubDate.isNotEmpty()) {
                        Text(currentBook.pubDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (currentBook.pageCount > 0) Text("${currentBook.pageCount}쪽", style = MaterialTheme.typography.bodySmall)
                    if (currentBook.priceStandard > 0) Text("${currentBook.priceStandard}원", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    if (currentBook.isbn13.isNotEmpty()) Text("ISBN: ${currentBook.isbn13}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Star rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("별점: ", style = MaterialTheme.typography.labelLarge)
                StarRating(
                    rating = currentBook.rating,
                    onRatingChanged = { viewModel.updateRating(it) }
                )
            }

            // Bookshelf
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("서가: ", style = MaterialTheme.typography.labelLarge)
                com.unknown.sancheck.ui.components.BookshelfDropdown(
                    bookshelves = bookshelves,
                    selectedShelfId = currentBook.bookshelfId,
                    onShelfSelected = { viewModel.moveToShelf(it) }
                )
            }

            // Quantity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("수량: ", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = { viewModel.updateQuantity(-1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "감소")
                }
                Text("${currentBook.quantity}권", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { viewModel.updateQuantity(1) }) {
                    Icon(Icons.Default.Add, contentDescription = "증가")
                }
            }

            // Millie / Welaaa
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = currentBook.availableOnMillie,
                    onClick = { viewModel.toggleMillie() },
                    label = { Text("밀리에 있음") }
                )
                FilterChip(
                    selected = currentBook.availableOnWelaaa,
                    onClick = { viewModel.toggleWelaaa() },
                    label = { Text("윌라에 있음") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { DeepLinkHelper.openMillieSearch(context, currentBook.title) }) {
                    Text("밀리의 서재에서 찾기")
                }
                OutlinedButton(onClick = { DeepLinkHelper.openWelaaaSearch(context, currentBook.title) }) {
                    Text("윌라에서 찾기")
                }
            }

            // Description
            if (currentBook.description.isNotEmpty()) {
                Text("설명", style = MaterialTheme.typography.titleMedium)
                Text(currentBook.description, style = MaterialTheme.typography.bodySmall)
            }

            // Memo
            Text("메모", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = memoText,
                onValueChange = { memoText = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("메모를 입력하세요...") }
            )
            Button(
                onClick = { viewModel.updateMemo(memoText) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("메모 저장")
            }

            Divider()

            // Annotations section
            // PostIts
            AnnotationSection(
                title = "포스트잇",
                count = postIts.size,
                onAdd = { showAddPostIt = true }
            ) {
                postIts.forEach { postIt ->
                    val color = PostItColors[postIt.colorIndex % PostItColors.size]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                postIt.content,
                                modifier = Modifier.weight(1f),
                                color = DarkBackground
                            )
                            IconButton(
                                onClick = { viewModel.deletePostIt(postIt) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "삭제", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Reviews
            AnnotationSection(
                title = "서평",
                count = reviews.size,
                onAdd = { showAddReview = true }
            ) {
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(review.content, style = MaterialTheme.typography.bodyMedium)
                                val dateStr = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(Date(review.createdAt))
                                Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = { viewModel.deleteReview(review) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "삭제", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Quotes
            AnnotationSection(
                title = "문장",
                count = quotes.size,
                onAdd = { showAddQuote = true }
            ) {
                quotes.forEach { quote ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("\"${quote.content}\"", style = MaterialTheme.typography.bodyMedium)
                                if (quote.page > 0) {
                                    Text("p.${quote.page}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            IconButton(
                                onClick = { viewModel.deleteQuote(quote) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "삭제", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // HashTags
            AnnotationSection(
                title = "해시태그",
                count = hashTags.size,
                onAdd = { showAddTag = true }
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(hashTags) { tag ->
                        AssistChip(
                            onClick = { viewModel.deleteHashTag(tag) },
                            label = { Text("#${tag.tag}") },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "삭제", modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }
            }

            // Date added
            val dateStr = SimpleDateFormat("yyyy년 MM월 dd일 등록", Locale.KOREA).format(Date(currentBook.dateAdded))
            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialogs
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("도서 삭제") },
            text = { Text("\"${book?.title}\"을(를) 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook { onNavigateBack() }
                    showDeleteDialog = false
                }) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    if (showAddPostIt) {
        TextInputDialog(
            title = "포스트잇 추가",
            onDismiss = { showAddPostIt = false },
            onConfirm = { text ->
                viewModel.addPostIt(text)
                showAddPostIt = false
            }
        )
    }

    if (showAddReview) {
        TextInputDialog(
            title = "서평 추가",
            onDismiss = { showAddReview = false },
            onConfirm = { text ->
                viewModel.addReview(text)
                showAddReview = false
            }
        )
    }

    if (showAddQuote) {
        TextInputDialog(
            title = "문장 추가",
            onDismiss = { showAddQuote = false },
            onConfirm = { text ->
                viewModel.addQuote(text)
                showAddQuote = false
            }
        )
    }

    if (showAddTag) {
        TextInputDialog(
            title = "해시태그 추가",
            placeholder = "#태그명",
            onDismiss = { showAddTag = false },
            onConfirm = { text ->
                viewModel.addHashTag(text)
                showAddTag = false
            }
        )
    }
}

@Composable
private fun AnnotationSection(
    title: String,
    count: Int,
    onAdd: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$title ($count)", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "$title 추가")
        }
    }
    content()
}

@Composable
private fun TextInputDialog(
    title: String,
    placeholder: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(placeholder.ifEmpty { "내용을 입력하세요..." }) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("추가") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}
