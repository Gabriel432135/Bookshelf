package com.example.bookshelf.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookshelfApiService {
    /**
     * Pesquisa livros com base em uma consulta, suportando paginação.
     * @param query O termo de busca.
     * @param startIndex O índice do primeiro item a ser retornado (Padrão 0).
     * @param maxResults O número máximo de resultados (Máximo 40).
     */
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int
    ): SearchResponse

    @GET("volumes/{id}")
    suspend fun getBook(@Path("id") id: String): BookDto
}
