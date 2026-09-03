package com.example.bookshelf.data

import com.example.bookshelf.BuildConfig
import com.example.bookshelf.network.BookshelfApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer {
    val repository: AppRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://www.googleapis.com/books/v1/"
    val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY

    //Cliente HTTP para adicionar a chave de API ao cabeçalho da solicitação
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", apiKey)
                .build()
            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
        .build()

    private val json = Json {
        ignoreUnknownKeys = true // Ignora campos do JSON que não foram mapeados no DTO
        coerceInputValues = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }

    override val repository: AppRepository by lazy {
        DefaultAppRepository(retrofitService)
    }
}
