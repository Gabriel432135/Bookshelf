package com.example.bookshelf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.theme.AppTheme
import com.example.bookshelf.ui.theme.BookshelfTheme

@Composable
fun BookshelfApp(
    modifier: Modifier = Modifier
) {
    BookShelfNavHost(
        navController = rememberNavController(),
        modifier = modifier
    )
}

@Composable
fun BookshelfTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.dimensions.paddingLarge),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = AppTheme.dimensions.cardElevation,
        shape = AppTheme.shape.large
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Pesquisar livros...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBookClick: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AppTheme.dimensions.paddingSmall)
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            BooksGrid(
                books = uiState.books,
                isPaginating = uiState.isPaginating,
                onBookClick = onBookClick,
                onLoadNextPage = onLoadNextPage,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is HomeUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Erro: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(AppTheme.dimensions.paddingLarge)
                )
            }
        }
    }
}

@Composable
fun BooksGrid(
    books: List<Book>,
    isPaginating: Boolean,
    onBookClick: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val gridState = rememberLazyGridState()

    // CORREÇÃO: O vigia agora observa o gridState e o total de itens.
    // Ele dispara o gatilho quando o último item visível está entre os 5 últimos da lista.
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            // Gatilho: Se o usuário passou de 80% da lista atual, pede a próxima
            totalItems > 0 && lastVisibleItem >= totalItems - 5
        }
    }

    // Toda vez que o 'shouldLoadMore' mudar para TRUE, tentamos carregar
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isPaginating) {
            onLoadNextPage()
        }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(AppTheme.dimensions.columnCount),
        modifier = modifier.padding(horizontal = AppTheme.dimensions.paddingSmall),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(items = books, key = { index, book -> "${book.id}_$index" }) { _, book ->
            BookCard(
                book = book,
                onBookClick = onBookClick,
                modifier = Modifier
                    .padding(AppTheme.dimensions.paddingSmall)
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
            )
        }

        if (isPaginating) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimensions.paddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { book.id?.let { onBookClick(it) } },
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimensions.cardElevation),
        shape = AppTheme.shape.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(book.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize().shimmerEffect())
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.Warning),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 0f
                        )
                    )
                    .padding(AppTheme.dimensions.paddingSmall)
            ) {
                Text(
                    text = book.title?.takeUnless { it.isBlank() } ?: "Sem título",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimensions.paddingSmall)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BookshelfTheme {
        BooksGrid(
            books = listOf(
                Book("1", "Book 1", "https://example.com/book1.jpg"),
                Book("2", "Book 2", "https://example.com/book2.jpg"),
            ),
            isPaginating = true,
            onBookClick = {},
            onLoadNextPage = {}
        )
    }
}
