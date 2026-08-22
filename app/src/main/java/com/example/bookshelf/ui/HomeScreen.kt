package com.example.bookshelf.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.ui.theme.AppTheme
import com.example.bookshelf.ui.theme.BookshelfTheme

/**
 * Representação simples de um livro para o UI. 
 * Depois vamos usar a classe real que vier da API.
 */
data class Book(
    val id: String,
    val title: String,
    val thumbnailUrl: String
)

@Composable
fun BookshelfApp(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val mockData = remember {
        List(10) { Book("$it", "Livro de Teste $it", "") }
    }

    Scaffold(
        topBar = {
            BookshelfTopAppBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }
    ) { p ->
        HomeScreen(
            books = mockData,
            contentPadding = p,
            modifier = modifier.fillMaxSize()
        )
    }
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
            .padding(AppTheme.dimensions.paddingMedium),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = AppTheme.dimensions.cardElevation,
        shape = AppTheme.shape.extraLarge
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
    books: List<Book>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AppTheme.dimensions.paddingSmall)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(AppTheme.dimensions.columnCount),
        modifier = modifier.padding(horizontal = AppTheme.dimensions.paddingSmall),
        contentPadding = contentPadding,
    ) {
        items(items = books, key = { book -> book.id }) { book ->
            BookCard(
                book = book,
                modifier = Modifier
                    .padding(AppTheme.dimensions.paddingSmall)
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
            )
        }
    }
}

@Composable
fun BookCard(book: Book, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimensions.cardElevation),
        shape = AppTheme.shape.medium
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(book.thumbnailUrl.replace("http", "https"))
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.Warning),
                placeholder = rememberVectorPainter(Icons.Default.Info),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(AppTheme.dimensions.paddingMedium),
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BookshelfTheme {
        BookshelfApp()
    }
}
