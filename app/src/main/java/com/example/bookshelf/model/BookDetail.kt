package com.example.bookshelf.model

/**
 * Modelo de dados completo para a tela de detalhes.
 */
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