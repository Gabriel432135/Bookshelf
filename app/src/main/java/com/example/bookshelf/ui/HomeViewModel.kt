package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.AppRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HomeUiState{
    data class Success(val books: List<Book>) : HomeUiState
    data class Error(val errorMessage: String) : HomeUiState
    object Loading : HomeUiState
}
class HomeViewModel(private var bookshelfRepository: AppRepository) : ViewModel(){

    companion object{
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

    init{
        getBooks("kotlin")
    }

    fun getBooks(query: String){
        viewModelScope.launch {
            _uistate.value = HomeUiState.Loading
            try{
                val booksList = bookshelfRepository.getBooks(query)
                _uistate.value = HomeUiState.Success(booksList)
            }catch (e: IOException){
                _uistate.value = HomeUiState.Error(e.message ?: "IO error")
            }catch (e: Exception){
                _uistate.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }


}