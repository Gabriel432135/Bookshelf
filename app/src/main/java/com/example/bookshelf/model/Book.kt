package com.example.bookshelf.model

import androidx.compose.runtime.Immutable

/**
 * Representação simples de um livro para o UI.
 * Marcada como @Immutable para otimizar a recomposição em listas.
 */
@Immutable
data class Book(
    val id: String,
    val title: String?,
    val thumbnailUrl: String?
)
