package com.example.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.data.AppRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private var bookshelfRepository: AppRepository) : ViewModel(){

    companion object{
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookshelfApplication)
                val appRepository = application.container.repository
                HomeViewModel(bookshelfRepository = appRepository)
            }
        }
    }

    sealed interface HomeUiState{
        data class Success(val books: List<Book>) : HomeUiState
        object Error : HomeUiState
        object Loading : HomeUiState
    }

    private val mutable_uistate: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uistate: StateFlow<HomeUiState> = mutable_uistate.asStateFlow()

    init{
        getBooks("kotlin")
    }

    fun getBooks(query: String){
        viewModelScope.launch {
            mutable_uistate.value = HomeUiState.Loading
            try{
                val booksList = bookshelfRepository.getBooks(query)
                mutable_uistate.value = HomeUiState.Success(booksList)
            }catch (e: Exception){
                mutable_uistate.value = HomeUiState.Error
            }
        }
    }


}