package com.unknown.sancheck.ui.bookshelf

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.data.local.entity.Bookshelf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository
    val prefsManager = container.prefsManager

    private val _selectedShelfId = MutableStateFlow(container.prefsManager.currentShelfId)
    val selectedShelfId: StateFlow<Long> = _selectedShelfId

    private val _viewMode = MutableStateFlow(container.prefsManager.viewMode)
    val viewMode: StateFlow<Int> = _viewMode

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showMillieOnly = MutableStateFlow(false)
    val showMillieOnly: StateFlow<Boolean> = _showMillieOnly

    private val _showWelaaaOnly = MutableStateFlow(false)
    val showWelaaaOnly: StateFlow<Boolean> = _showWelaaaOnly

    private val _editMode = MutableStateFlow(false)
    val editMode: StateFlow<Boolean> = _editMode

    val bookshelves: StateFlow<List<Bookshelf>> = repository.getAllBookshelves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val books: StateFlow<List<Book>> = combine(
        _selectedShelfId,
        _searchQuery,
        _showMillieOnly,
        _showWelaaaOnly
    ) { shelfId, query, millie, welaaa ->
        Triple(shelfId, query, Pair(millie, welaaa))
    }.flatMapLatest { (shelfId, query, filters) ->
        val baseFlow = if (query.isNotEmpty()) {
            repository.searchBooks(query)
        } else if (shelfId == 0L) {
            repository.getAllBooks()
        } else {
            repository.getBooksByShelf(shelfId)
        }
        baseFlow.map { books ->
            var filtered = books
            if (filters.first) filtered = filtered.filter { it.availableOnMillie }
            if (filters.second) filtered = filtered.filter { it.availableOnWelaaa }
            filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectShelf(shelfId: Long) {
        _selectedShelfId.value = shelfId
        prefsManager.currentShelfId = shelfId
    }

    fun toggleViewMode() {
        val newMode = if (_viewMode.value == 0) 1 else 0
        _viewMode.value = newMode
        prefsManager.viewMode = newMode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleMillieFilter() {
        _showMillieOnly.value = !_showMillieOnly.value
    }

    fun toggleWelaaaFilter() {
        _showWelaaaOnly.value = !_showWelaaaOnly.value
    }

    fun toggleEditMode() {
        _editMode.value = !_editMode.value
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    fun moveBookToShelf(book: Book, shelfId: Long) {
        viewModelScope.launch {
            repository.updateBook(book.copy(bookshelfId = shelfId))
        }
    }
}
