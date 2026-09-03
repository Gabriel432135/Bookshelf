package com.example.bookshelf.network

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val items: List<BookDto>? = null
)

@Serializable
data class BookDto(
    val id: String,
    val volumeInfo: VolumeInfo
)

@Serializable
data class VolumeInfo(
    val title: String,
    val subtitle: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null
)

@Serializable
data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
)
