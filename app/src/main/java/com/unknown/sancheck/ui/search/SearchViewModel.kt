package com.unknown.sancheck.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.sancheck.BuildConfig
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.data.local.entity.Book
import com.unknown.sancheck.data.remote.AladinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<AladinItem> = emptyList(),
    val selectedItem: AladinItem? = null,
    val existingBook: Book? = null,
    val showDuplicate: Boolean = false,
    val savedBookId: Long? = null,
    val error: String? = null
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository
    private val prefsManager = container.prefsManager

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun search(query: String) {
        if (query.isBlank()) return
        _uiState.value = _uiState.value.copy(query = query, isLoading = true, error = null)

        viewModelScope.launch {
            val ttbKey = prefsManager.ttbKey.ifEmpty { BuildConfig.ALADIN_TTB_KEY }
            if (ttbKey.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "API 키가 설정되지 않았습니다."
                )
                return@launch
            }

            val results = repository.searchBooksOnline(ttbKey, query)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                results = results,
                error = if (results.isEmpty()) "검색 결과가 없습니다." else null
            )
        }
    }

    fun selectItem(item: AladinItem) {
        viewModelScope.launch {
            val isbn = item.isbn13.ifEmpty { item.isbn }
            if (isbn.isNotEmpty()) {
                val existing = repository.getBookByIsbn(isbn)
                if (existing != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedItem = item,
                        existingBook = existing,
                        showDuplicate = true
                    )
                    return@launch
                }
            }
            _uiState.value = _uiState.value.copy(selectedItem = item)
        }
    }

    fun saveSelectedItem() {
        val item = _uiState.value.selectedItem ?: return
        viewModelScope.launch {
            val book = repository.aladinItemToBook(
                item,
                prefsManager.currentShelfId.takeIf { it > 0 } ?: 1
            )
            val id = repository.insertBook(book)
            _uiState.value = _uiState.value.copy(savedBookId = id, selectedItem = null)
        }
    }

    fun incrementQuantity() {
        val existing = _uiState.value.existingBook ?: return
        viewModelScope.launch {
            repository.updateBook(existing.copy(quantity = existing.quantity + 1))
            _uiState.value = _uiState.value.copy(
                showDuplicate = false,
                savedBookId = existing.id,
                selectedItem = null
            )
        }
    }

    fun dismissDuplicate() {
        _uiState.value = _uiState.value.copy(showDuplicate = false, selectedItem = null)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedItem = null)
    }
}
