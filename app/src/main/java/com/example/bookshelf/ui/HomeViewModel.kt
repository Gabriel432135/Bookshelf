package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.AppRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

sealed interface HomeUiState {
    data class Success(
        val books: List<Book>,
        val isPaginating: Boolean = false // Novo campo para o carregamento no final da lista
    ) : HomeUiState
    data class Error(val errorMessage: String) : HomeUiState
    object Loading : HomeUiState
}

@OptIn(FlowPreview::class)
class HomeViewModel(private var bookshelfRepository: AppRepository) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookshelfApplication)
                val appRepository = application.container.repository
                HomeViewModel(bookshelfRepository = appRepository)
            }
        }
    }

    private val _uistate: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uistate: StateFlow<HomeUiState> = _uistate.asStateFlow()

    private val _query = MutableStateFlow("kotlin")
    val query = _query.asStateFlow()

    private var currentStartIndex = 0
    private val allBooks = mutableListOf<Book>()
    private var isEndReached = false

    init {
        viewModelScope.launch {
            _query
                .debounce(500.milliseconds)
                .filter { it.trim().replace(Regex("[!@#\$%^&*()?]"), "").isNotEmpty() }
                .distinctUntilChanged()
                .collect {
                    isEndReached = false
                    getBooks(it, false)
                }
        }
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    private fun getBooks(query: String, isNextPage: Boolean) {
        viewModelScope.launch {
            if (!isNextPage) {
                currentStartIndex = 0
                allBooks.clear()
                _uistate.value = HomeUiState.Loading
            } else {
                // Se for próxima página, avisa a UI que estamos paginando
                _uistate.value = HomeUiState.Success(allBooks.toList(), isPaginating = true)
            }

            try {
                val novosLivros = bookshelfRepository.getBooks(query, currentStartIndex, 20)
                
                if (novosLivros.isEmpty()) {
                    isEndReached = true
                }
                
                allBooks.addAll(novosLivros)
                _uistate.value = HomeUiState.Success(allBooks.toList(), isPaginating = false)
            } catch (e: IOException) {
                _uistate.value = HomeUiState.Error("Erro de conexão")
            } catch (e: Exception) {
                _uistate.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun fetchNextPage() {
        // Evita chamadas se já estivermos carregando, se chegamos no fim ou se deu erro
        val currentState = _uistate.value
        if (currentState is HomeUiState.Success && !currentState.isPaginating && !isEndReached) {
            currentStartIndex += 20
            getBooks(_query.value, true)
        }
    }
}
