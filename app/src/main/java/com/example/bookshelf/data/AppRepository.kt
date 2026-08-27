package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.BookDetail
import com.example.bookshelf.network.BookshelfApiService

interface AppRepository {
    suspend fun getBooks(query: String = "jazz"): List<Book>
    suspend fun getBook(id: String): BookDetail
}

class DefaultAppRepository(
    private val bookshelfApiService: BookshelfApiService
) : AppRepository {

    override suspend fun getBooks(query: String): List<Book> {
        val response = bookshelfApiService.searchBooks(query)
        // Mapeia o DTO da API para o modelo simples da UI
        return response.items?.map { dto ->
            Book(
                id = dto.id,
                title = dto.volumeInfo.title,
                thumbnailUrl = dto.volumeInfo.imageLinks?.thumbnail ?: ""
            )
        } ?: emptyList()
    }

    override suspend fun getBook(id: String): BookDetail {
        val dto = bookshelfApiService.getBook(id)
        // Mapeia o DTO completo para o modelo de detalhes da UI
        return BookDetail(
            id = dto.id,
            title = dto.volumeInfo.title,
            subtitle = dto.volumeInfo.subtitle,
            authors = dto.volumeInfo.authors ?: emptyList(),
            description = dto.volumeInfo.description ?: "",
            thumbnailUrl = dto.volumeInfo.imageLinks?.thumbnail ?: "",
            averageRating = dto.volumeInfo.averageRating ?: 0.0,
            ratingsCount = dto.volumeInfo.ratingsCount ?: 0
        )
    }
}
