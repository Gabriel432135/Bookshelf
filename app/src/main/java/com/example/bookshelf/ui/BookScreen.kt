package com.example.bookshelf.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.model.BookDetail
import com.example.bookshelf.ui.theme.AppTheme
import com.example.bookshelf.ui.theme.BookshelfTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    uiState: BookUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is BookUiState.Loading -> LoadingScreen(modifier.padding(padding))
            is BookUiState.Success -> BookDetailContent(
                book = uiState.book,
                modifier = modifier.padding(padding)
            )

            is BookUiState.Error -> ErrorScreen(
                message = uiState.errorMessage,
                modifier = modifier.padding(padding)
            )
        }
    }
}

@Composable
fun BookDetailContent(book: BookDetail, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(AppTheme.dimensions.paddingLarge)
    ) {

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(book.thumbnailUrl)
                .crossfade(true)
                .build(),
            loading = {
                // O Brilho do Shimmer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(AppTheme.shape.large)
                        .shimmerEffect()
                )
            },
            contentDescription = book.title,
            error = {
                // Caso a imagem falhe (ex: link quebrado)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(AppTheme.shape.large),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_broken_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingLarge))

        // Título: Trata Nulo E Branco
        Text(
            text = book.title?.takeUnless { it.isBlank() } ?: "Sem título",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Subtítulo: Trata Nulo E Branco
        Text(
            text = book.subtitle?.takeUnless { it.isBlank() } ?: "Sem subtítulo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )


        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingSmall))

        // Autores: Trata Nulo E Branco
        Text(
            text = book.authors?.filter { it.isNotBlank() }?.joinToString(", ")
                ?.takeUnless { it.isBlank() }
                ?: "Sem informação sobre os autores",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingMedium))

        book.averageRating?.let { average ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingStars(rating = average)
                Spacer(modifier = Modifier.width(AppTheme.dimensions.paddingSmall))
                Text(
                    text = "(${book.ratingsCount ?: 0} avaliações)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingLarge))

        Text(
            text = if (book.description.isNullOrBlank()) "Sem descrição" else "Descrição",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingSmall))

        book.description?.takeUnless { it.isBlank() }?.let { desc ->
            Text(
                text = desc.replace(Regex("<[^>]*>"), ""),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Erro: $message", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun RatingStars(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val icon = if (rating >= index + 1) Icons.Default.Star
            else if (rating >= index + 0.5) Icons.Default.StarHalf
            else Icons.Default.StarOutline
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
