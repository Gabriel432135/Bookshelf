package com.example.bookshelf

import com.example.bookshelf.fake.FakeAppRepository
import com.example.bookshelf.fake.FakeDataSource
import com.example.bookshelf.rules.MainDispatcherRule
import com.example.bookshelf.ui.HomeUiState
import com.example.bookshelf.ui.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun homeViewModel_getBooks_verifyHomeUiStateSuccess() = runTest {
        val homeViewModel = HomeViewModel(
            bookshelfRepository = FakeAppRepository()
        )

        // 1. O estado inicial deve ser Empty
        assertEquals(HomeUiState.Empty, homeViewModel.uistate.value)

        // 2. Simula o usuário digitando algo válido
        homeViewModel.updateQuery("kotlin")

        // 3. PULO DO GATO: Avança o tempo virtual para vencer o debounce de 500ms
        advanceUntilIdle()

        // 4. Agora o estado deve ser Success com os dados do repositório fake
        assertEquals(
            HomeUiState.Success(FakeDataSource.books, isPaginating = false),
            homeViewModel.uistate.value
        )
    }

    @Test
    fun homeViewModel_updateQueryEmpty_verifyHomeUiStateEmpty() = runTest {
        val homeViewModel = HomeViewModel(
            bookshelfRepository = FakeAppRepository()
        )
        
        // Digita algo e limpa
        homeViewModel.updateQuery("kotlin")
        advanceUntilIdle()
        homeViewModel.updateQuery("") // Limpa a busca
        
        // Deve voltar para Empty na hora
        assertEquals(HomeUiState.Empty, homeViewModel.uistate.value)
    }
}
