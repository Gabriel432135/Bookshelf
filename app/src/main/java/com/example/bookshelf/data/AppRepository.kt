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
        val sanitizedQuery = sanitizeQuery(query)
        val response = bookshelfApiService.searchBooks(sanitizedQuery)
        return response.items?.map { dto ->
            Book(
                id = dto.id,
                title = dto.volumeInfo.title,
                // Truque: Força o HTTPS e tenta aumentar o zoom de 1 para 2 na lista
                thumbnailUrl = dto.volumeInfo.imageLinks?.thumbnail?.toHttps()?.toHighRes()
                    ?: "" //apenas a imagem é tratada no rep, para garantir a tela de erro do coil
            )
        } ?: emptyList()
    }

    override suspend fun getBook(id: String): BookDetail {
        val dto = bookshelfApiService.getBook(id)
        val imageLinks = dto.volumeInfo.imageLinks
        
        // Tenta pegar a maior imagem disponível em ordem decrescente
        val bestImage = imageLinks?.extraLarge 
            ?: imageLinks?.large 
            ?: imageLinks?.medium 
            ?: imageLinks?.small 
            ?: imageLinks?.thumbnail?.toHighRes() // Se não tiver nenhuma, faz o "zoom" na pequena
            ?: "" //apenas a imagem é tratada no rep, para garantir a tela de erro do coil


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

    /**
     * Função de extensão interna para melhorar a qualidade da imagem do Google Books.
     */
    private fun String.toHighRes(): String {
        return this.replace("zoom=1", "zoom=2") // Aumenta a resolução
    }

    private fun String.toHttps(): String {
        return this.replace("http:", "https:")
    }

    private fun sanitizeQuery(query: String): String {
        return java.text.Normalizer.normalize(query, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "") // Remove os acentos
            .replace(Regex("[^a-zA-Z0-9 ]"), "") // Remove símbolos especiais (deixa só letras, números e espaços)
            .trim() // Remove espaços inúteis no início e fim
    }

}
