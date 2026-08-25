package com.example.bookshelf.ui

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
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.model.BookDetail
import com.example.bookshelf.ui.theme.AppTheme
import com.example.bookshelf.ui.theme.BookshelfTheme


@Composable
fun BookScreen(
    book: BookDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(AppTheme.dimensions.paddingLarge)
    ) {
        // 1. Capa do Livro
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(book.thumbnailUrl.replace("http", "https"))
                .crossfade(true)
                .build(),
            contentDescription = book.title,
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(AppTheme.shape.large),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingLarge))

        // 2. Título e Autores
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        book.subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingSmall))

        Text(
            text = book.authors.joinToString(", "),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingMedium))

        // 3. Avaliação (Estrelas)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RatingStars(rating = book.averageRating)
            Spacer(modifier = Modifier.width(AppTheme.dimensions.paddingSmall))
            Text(
                text = "(${book.ratingsCount} avaliações)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingLarge))

        // 4. Resumo/Descrição
        Text(
            text = "Resumo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(AppTheme.dimensions.paddingSmall))

        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Justify
        )
    }
}

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            val starIndex = index + 1
            val icon = when {
                rating >= starIndex -> Icons.Default.Star
                rating >= starIndex - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC107), // Cor dourada para as estrelas
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookScreenPreview() {
    val mockBook = BookDetail(
        id = "1",
        title = "Kotlin in Action",
        subtitle = "Segunda Edição",
        authors = listOf("Dmitry Jemerov", "Svetlana Isakova"),
        description = "Kotlin in Action ensina a usar a linguagem Kotlin para desenvolvimento de qualidade profissional. Escrito pelos próprios desenvolvedores da linguagem na JetBrains, este livro vai além da sintaxe e ensina como as características do Kotlin permitem que você escreva código limpo, seguro e expressivo.",
        thumbnailUrl = "",
        averageRating = 4.5,
        ratingsCount = 120
    )
    
    BookshelfTheme {
        Scaffold { padding ->
            BookScreen(book = mockBook, modifier = Modifier.padding(padding))
        }
    }
}
