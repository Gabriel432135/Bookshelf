package com.example.bookshelf.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Home : Routes

    @Serializable
    data class Detail(val bookId: String) : Routes
}

@Composable
fun BookShelfNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        // Tela 1: Home
        composable<Routes.Home> {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            val uiState by homeViewModel.uistate.collectAsState()

            Scaffold(
                topBar = {
                    BookshelfTopAppBar(
                        query = "kotlin",
                        onQueryChange = {query ->
                            homeViewModel.getBooks(query)
                        }
                    )
                }
            ) { padding ->
                HomeScreen(
                    onBookClick = { id ->
                        navController.navigate(Routes.Detail(bookId = id))
                    },
                    contentPadding = padding,
                    uiState = uiState,
                )
            }
        }

        // Tela 2: Detalhes
        composable<Routes.Detail> { backStackEntry ->
            val detailRoute: Routes.Detail = backStackEntry.toRoute()
            
            val detailViewModel: BookDetailsViewModel = viewModel(factory = BookDetailsViewModel.Factory)
            val uiState by detailViewModel.uiState.collectAsState()

            LaunchedEffect(detailRoute.bookId) {
                detailViewModel.getBook(detailRoute.bookId)
            }

            BookScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
