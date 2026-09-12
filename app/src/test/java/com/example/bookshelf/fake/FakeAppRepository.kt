package com.example.bookshelf.fake

import com.example.bookshelf.data.AppRepository
import com.example.bookshelf.model.Book
import com.example.bookshelf.model.BookDetail

class FakeAppRepository : AppRepository {
    override suspend fun getBooks(query: String, startIndex: Int, maxResults: Int): List<Book> {
        return FakeDataSource.books
    }

    override suspend fun getBook(id: String): BookDetail {
        return FakeDataSource.bookDetail
    }
}
