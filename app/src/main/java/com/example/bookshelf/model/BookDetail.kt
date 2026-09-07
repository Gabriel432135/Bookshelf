package com.example.bookshelf.model

import androidx.compose.runtime.Immutable

/**
 * Modelo de dados completo para a tela de detalhes.
 * Marcada como @Immutable para otimizar a recomposição.
 */
@Immutable
data class BookDetail(
    val id: String,
    val title: String?,
    val subtitle: String?,
    val authors: List<String>?,
    val description: String?,
    val thumbnailUrl: String?,
    val averageRating: Double?,
    val ratingsCount: Int?
)
