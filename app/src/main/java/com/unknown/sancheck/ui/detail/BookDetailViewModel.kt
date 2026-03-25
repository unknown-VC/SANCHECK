package com.unknown.sancheck.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.data.local.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository

    private val _bookId = MutableStateFlow(0L)

    val book: StateFlow<Book?> = _bookId.flatMapLatest { id ->
        if (id > 0) repository.getBookByIdFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val postIts: StateFlow<List<PostIt>> = _bookId.flatMapLatest { id ->
        if (id > 0) repository.getPostItsByBook(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reviews: StateFlow<List<BookReview>> = _bookId.flatMapLatest { id ->
        if (id > 0) repository.getReviewsByBook(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quotes: StateFlow<List<Quote>> = _bookId.flatMapLatest { id ->
        if (id > 0) repository.getQuotesByBook(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hashTags: StateFlow<List<HashTag>> = _bookId.flatMapLatest { id ->
        if (id > 0) repository.getHashTagsByBook(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookshelves: StateFlow<List<Bookshelf>> = repository.getAllBookshelves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadBook(bookId: Long) {
        _bookId.value = bookId
    }

    fun updateRating(rating: Float) {
        viewModelScope.launch {
            book.value?.let { repository.updateBook(it.copy(rating = rating)) }

        }
    }

    fun updateMemo(memo: String) {
        viewModelScope.launch {
            book.value?.let { repository.updateBook(it.copy(memo = memo)) }

        }
    }

    fun updateQuantity(delta: Int) {
        viewModelScope.launch {
            book.value?.let {
                val newQty = (it.quantity + delta).coerceAtLeast(1)
                repository.updateBook(it.copy(quantity = newQty))
            }

        }
    }

    fun toggleMillie() {
        viewModelScope.launch {
            book.value?.let { repository.updateBook(it.copy(availableOnMillie = !it.availableOnMillie)) }

        }
    }

    fun toggleWelaaa() {
        viewModelScope.launch {
            book.value?.let { repository.updateBook(it.copy(availableOnWelaaa = !it.availableOnWelaaa)) }

        }
    }

    fun moveToShelf(shelfId: Long) {
        viewModelScope.launch {
            book.value?.let { repository.updateBook(it.copy(bookshelfId = shelfId)) }

        }
    }

    fun deleteBook(onDeleted: () -> Unit) {
        viewModelScope.launch {
            book.value?.let { repository.deleteBook(it) }
            onDeleted()
        }
    }

    // PostIt
    fun addPostIt(content: String, colorIndex: Int = 0) {
        viewModelScope.launch {
            repository.insertPostIt(PostIt(bookId = _bookId.value, content = content, colorIndex = colorIndex))
        }
    }

    fun deletePostIt(postIt: PostIt) {
        viewModelScope.launch { repository.deletePostIt(postIt) }
    }

    // Review
    fun addReview(content: String) {
        viewModelScope.launch {
            repository.insertReview(BookReview(bookId = _bookId.value, content = content))
        }
    }

    fun deleteReview(review: BookReview) {
        viewModelScope.launch { repository.deleteReview(review) }
    }

    // Quote
    fun addQuote(content: String, page: Int = 0) {
        viewModelScope.launch {
            repository.insertQuote(Quote(bookId = _bookId.value, content = content, page = page))
        }
    }

    fun deleteQuote(quote: Quote) {
        viewModelScope.launch { repository.deleteQuote(quote) }
    }

    // HashTag
    fun addHashTag(tag: String) {
        viewModelScope.launch {
            repository.insertHashTag(HashTag(bookId = _bookId.value, tag = tag.removePrefix("#")))
        }
    }

    fun deleteHashTag(hashTag: HashTag) {
        viewModelScope.launch { repository.deleteHashTag(hashTag) }
    }
}
