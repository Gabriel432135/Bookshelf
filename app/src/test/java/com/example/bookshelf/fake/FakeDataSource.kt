package com.example.bookshelf.fake

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.BookDetail

object FakeDataSource {
    val books = listOf(
        Book(id = "1", title = "Book 1", thumbnailUrl = "url1"),
        Book(id = "2", title = "Book 2", thumbnailUrl = "url2")
    )

    val bookDetail = BookDetail(
        id = "1",
        title = "Book 1",
        subtitle = "Subtitle 1",
        authors = listOf("Author 1"),
        description = "Description 1",
        thumbnailUrl = "url1",
        averageRating = 4.5,
        ratingsCount = 10
    )
}
