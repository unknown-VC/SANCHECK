package com.unknown.sancheck.ui.info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.data.local.dao.AuthorStat
import com.unknown.sancheck.data.local.dao.PublisherStat
import com.unknown.sancheck.data.local.dao.TranslatorStat
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.data.local.entity.Bookshelf
import kotlinx.coroutines.flow.*
import java.util.*

class InfoViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository

    val totalBookCount: StateFlow<Int> = repository.getTotalBookCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val authorCount: StateFlow<Int> = repository.getDistinctAuthorCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val translatorCount: StateFlow<Int> = repository.getDistinctTranslatorCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val publisherCount: StateFlow<Int> = repository.getDistinctPublisherCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // This year's books
    private val startOfYear: Long = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val booksThisYear: StateFlow<List<Book>> = repository.getBooksAddedThisYear(startOfYear)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sorted views
    val booksByPubDate: StateFlow<List<Book>> = repository.getBooksSortedByPubDate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val booksByRating: StateFlow<List<Book>> = repository.getBooksSortedByRating()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val booksByPrice: StateFlow<List<Book>> = repository.getBooksSortedByPrice()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val booksByPageCount: StateFlow<List<Book>> = repository.getBooksSortedByPageCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stats
    val authorStats: StateFlow<List<AuthorStat>> = repository.getAuthorStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val translatorStats: StateFlow<List<TranslatorStat>> = repository.getTranslatorStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publisherStats: StateFlow<List<PublisherStat>> = repository.getPublisherStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Annotation counts
    val postItCount: StateFlow<Int> = repository.getTotalPostItCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val reviewCount: StateFlow<Int> = repository.getTotalReviewCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val quoteCount: StateFlow<Int> = repository.getTotalQuoteCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val tagCount: StateFlow<Int> = repository.getDistinctTagCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bookshelves: StateFlow<List<Bookshelf>> = repository.getAllBookshelves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Manage bookshelves
    suspend fun addBookshelf(name: String) {
        repository.insertBookshelf(Bookshelf(name = name))
    }

    suspend fun deleteBookshelf(bookshelf: Bookshelf) {
        repository.deleteBookshelf(bookshelf)
    }

    suspend fun updateBookshelf(bookshelf: Bookshelf) {
        repository.updateBookshelf(bookshelf)
    }
}
