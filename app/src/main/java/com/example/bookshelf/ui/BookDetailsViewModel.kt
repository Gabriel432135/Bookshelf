package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.AppRepository
import com.example.bookshelf.model.BookDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface BookUiState {
    data class Success(val book: BookDetail) : BookUiState
    data class Error(val errorMessage: String) : BookUiState
    object Loading : BookUiState
}

class BookDetailsViewModel(private val bookshelfRepository: AppRepository) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookshelfApplication)
                val appRepository = application.container.repository
                BookDetailsViewModel(bookshelfRepository = appRepository)
            }
        }
    }
    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()


    fun getBook(id: String) {
        if (_uiState.value is BookUiState.Success &&
            (_uiState.value as BookUiState.Success).book.id == id) {
            return
        }

        viewModelScope.launch {
            _uiState.value = BookUiState.Loading
            try {
                val book = bookshelfRepository.getBook(id)
                _uiState.value = BookUiState.Success(book)
            } catch (e: IOException) {
                _uiState.value = BookUiState.Error(e.message ?: "IO error")
            } catch (e: Exception) {
                _uiState.value = BookUiState.Error(e.message ?: "Unknown error")
            }
        }
    }


}
