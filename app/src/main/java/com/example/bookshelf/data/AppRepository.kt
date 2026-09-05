package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.BookDetail
import com.example.bookshelf.network.BookshelfApiService

interface AppRepository {
    suspend fun getBooks(query: String, startIndex: Int, maxResults: Int): List<Book>
    suspend fun getBook(id: String): BookDetail
}

class DefaultAppRepository(
    private val bookshelfApiService: BookshelfApiService
) : AppRepository {

    override suspend fun getBooks(query: String, startIndex: Int, maxResults: Int): List<Book> {
        val sanitizedQuery = sanitizeQuery(query)
        val response = bookshelfApiService.searchBooks(
            query = sanitizedQuery,
            startIndex = startIndex,
            maxResults = maxResults
        )
        return response.items?.map { dto ->
            Book(
                id = dto.id,
                title = dto.volumeInfo.title,
                thumbnailUrl = dto.volumeInfo.imageLinks?.thumbnail?.toHttps()?.toHighRes() ?: ""
            )
        } ?: emptyList()
    }

    override suspend fun getBook(id: String): BookDetail {
        val dto = bookshelfApiService.getBook(id)
        val imageLinks = dto.volumeInfo.imageLinks
        
        val bestImage = imageLinks?.extraLarge 
            ?: imageLinks?.large 
            ?: imageLinks?.medium 
            ?: imageLinks?.small 
            ?: imageLinks?.thumbnail?.toHighRes() 
            ?: ""


        return BookDetail(
            id = dto.id,
            title = dto.volumeInfo.title,
            subtitle = dto.volumeInfo.subtitle,
            authors = dto.volumeInfo.authors,
            description = dto.volumeInfo.description,
            thumbnailUrl = bestImage.toHttps(),
            averageRating = dto.volumeInfo.averageRating,
            ratingsCount = dto.volumeInfo.ratingsCount
        )
    }

    private fun String.toHighRes(): String {
        return this.replace("zoom=1", "zoom=2")
    }

    private fun String.toHttps(): String {
        return this.replace("http:", "https:")
    }

    private fun sanitizeQuery(query: String): String {
        return java.text.Normalizer.normalize(query, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace(Regex("[^a-zA-Z0-9 ]"), "")
            .trim()
    }
}
