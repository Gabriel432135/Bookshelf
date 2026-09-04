package com.example.bookshelf.model

/**
 * Representação simples de um livro para o UI.
 * Depois vamos usar a classe real que vier da API.
 */
data class Book(
    val id: String,
    val title: String?,
    val thumbnailUrl: String?
)