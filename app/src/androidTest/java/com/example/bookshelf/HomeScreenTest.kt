package com.example.bookshelf

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.HomeScreen
import com.example.bookshelf.ui.HomeUiState
import com.example.bookshelf.ui.theme.BookshelfTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeScreen_verifySuccessStateDisplaysBooks() {
        val fakeBooks = listOf(
            Book(id = "1", title = "Kotlin Book", thumbnailUrl = "url1"),
            Book(id = "2", title = "Compose Book", thumbnailUrl = "url2")
        )

        composeTestRule.setContent {
            BookshelfTheme {
                HomeScreen(
                    uiState = HomeUiState.Success(fakeBooks),
                    onBookClick = {},
                    onLoadNextPage = {}
                )
            }
        }

        // Verifica se os títulos dos livros estão na tela
        composeTestRule.onNodeWithText("Kotlin Book").assertIsDisplayed()
        composeTestRule.onNodeWithText("Compose Book").assertIsDisplayed()
    }
}
