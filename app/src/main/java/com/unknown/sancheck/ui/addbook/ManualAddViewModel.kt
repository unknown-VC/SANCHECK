package com.unknown.sancheck.ui.addbook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.data.local.entity.Bookshelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManualAddViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository
    private val prefsManager = container.prefsManager

    val bookshelves: StateFlow<List<Bookshelf>> = repository.getAllBookshelves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _savedBookId = MutableStateFlow<Long?>(null)
    val savedBookId: StateFlow<Long?> = _savedBookId

    fun saveBook(
        title: String,
        subtitle: String,
        author: String,
        translator: String,
        publisher: String,
        pubDate: String,
        pageCount: String,
        price: String,
        isbn: String,
        coverUrl: String,
        bookshelfId: Long
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val book = Book(
                title = title.trim(),
                subtitle = subtitle.trim(),
                author = author.trim(),
                translator = translator.trim(),
                publisher = publisher.trim(),
                pubDate = pubDate.trim(),
                pageCount = pageCount.toIntOrNull() ?: 0,
                priceStandard = price.toIntOrNull() ?: 0,
                isbn13 = isbn.trim(),
                coverUrl = coverUrl.trim(),
                bookshelfId = if (bookshelfId > 0) bookshelfId else prefsManager.currentShelfId.takeIf { it > 0 } ?: 1
            )
            val id = repository.insertBook(book)
            _savedBookId.value = id
        }
    }
}
