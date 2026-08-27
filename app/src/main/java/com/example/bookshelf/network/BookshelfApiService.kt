package com.example.bookshelf.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookshelfApiService {
    /**
     * Pesquisa livros com base em uma consulta (ex: "jazz").
     */
    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): SearchResponse

    /**
     * Obtém os detalhes de um livro específico pelo ID.
     */
    @GET("volumes/{id}")
    suspend fun getBook(@Path("id") id: String): BookDto
}
