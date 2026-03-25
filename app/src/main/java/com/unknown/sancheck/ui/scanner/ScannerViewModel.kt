package com.unknown.sancheck.ui.scanner

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

data class ScannerUiState(
    val isLoading: Boolean = false,
    val scannedIsbn: String = "",
    val foundBook: AladinItem? = null,
    val existingBook: Book? = null,
    val showDuplicate: Boolean = false,
    val savedBookId: Long? = null,
    val error: String? = null
)

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as SanCheckApp).container
    private val repository = container.bookRepository
    private val prefsManager = container.prefsManager

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState

    private var lastScannedIsbn = ""

    fun onBarcodeDetected(isbn: String) {
        if (isbn == lastScannedIsbn || _uiState.value.isLoading) return
        lastScannedIsbn = isbn

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, scannedIsbn = isbn, error = null)

            // Check for duplicate
            val existing = repository.getBookByIsbn(isbn)
            if (existing != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    existingBook = existing,
                    showDuplicate = true
                )
                return@launch
            }

            // Look up on Aladin
            val ttbKey = prefsManager.ttbKey.ifEmpty { BuildConfig.ALADIN_TTB_KEY }
            if (ttbKey.isEmpty()) {
                lastScannedIsbn = ""
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "API 키가 설정되지 않았습니다. 설정에서 TTBKey를 입력해주세요."
                )
                return@launch
            }

            val item = repository.lookUpBookByIsbn(ttbKey, isbn)
            if (item != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    foundBook = item
                )
            } else {
                lastScannedIsbn = ""
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "도서 정보를 찾을 수 없습니다."
                )
            }
        }
    }

    fun saveFoundBook() {
        val item = _uiState.value.foundBook ?: return
        viewModelScope.launch {
            val book = repository.aladinItemToBook(item, prefsManager.currentShelfId.takeIf { it > 0 } ?: 1)
            val id = repository.insertBook(book)
            _uiState.value = _uiState.value.copy(savedBookId = id)
        }
    }

    fun incrementQuantity() {
        val existing = _uiState.value.existingBook ?: return
        viewModelScope.launch {
            repository.updateBook(existing.copy(quantity = existing.quantity + 1))
            _uiState.value = _uiState.value.copy(
                showDuplicate = false,
                savedBookId = existing.id
            )
        }
    }

    fun dismissDuplicate() {
        _uiState.value = _uiState.value.copy(showDuplicate = false)
        lastScannedIsbn = ""
    }

    fun resetState() {
        _uiState.value = ScannerUiState()
        lastScannedIsbn = ""
    }
}
