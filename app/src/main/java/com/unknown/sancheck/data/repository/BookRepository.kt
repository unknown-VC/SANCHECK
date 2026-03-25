package com.unknown.sancheck.data.repository

import com.unknown.sancheck.data.local.dao.AnnotationDao
import com.unknown.sancheck.data.local.dao.BookDao
import com.unknown.sancheck.data.local.dao.BookshelfDao
import com.unknown.sancheck.data.local.entity.*
import com.unknown.sancheck.data.remote.AladinApi
import com.unknown.sancheck.data.remote.AladinItem
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val bookshelfDao: BookshelfDao,
    private val annotationDao: AnnotationDao,
    private val aladinApi: AladinApi
) {
    // Books
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
    fun getBooksByShelf(shelfId: Long): Flow<List<Book>> = bookDao.getBooksByShelf(shelfId)
    suspend fun getBookById(bookId: Long): Book? = bookDao.getBookById(bookId)
    suspend fun getBookByIsbn(isbn: String): Book? = bookDao.getBookByIsbn(isbn)
    fun searchBooks(query: String): Flow<List<Book>> = bookDao.searchBooks(query)

    fun getBooksSortedByPubDate() = bookDao.getBooksSortedByPubDate()
    fun getBooksSortedByRating() = bookDao.getBooksSortedByRating()
    fun getBooksSortedByPrice() = bookDao.getBooksSortedByPrice()
    fun getBooksSortedByPageCount() = bookDao.getBooksSortedByPageCount()

    fun getTotalBookCount() = bookDao.getTotalBookCount()
    fun getDistinctAuthorCount() = bookDao.getDistinctAuthorCount()
    fun getDistinctTranslatorCount() = bookDao.getDistinctTranslatorCount()
    fun getDistinctPublisherCount() = bookDao.getDistinctPublisherCount()
    fun getBooksAddedThisYear(startOfYear: Long) = bookDao.getBooksAddedThisYear(startOfYear)
    fun getMillieBooks() = bookDao.getMillieBooks()
    fun getWelaaaBooks() = bookDao.getWelaaaBooks()

    fun getAuthorStats() = bookDao.getAuthorStats()
    fun getTranslatorStats() = bookDao.getTranslatorStats()
    fun getPublisherStats() = bookDao.getPublisherStats()

    suspend fun getAllBooksSync() = bookDao.getAllBooksSync()
    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    suspend fun deleteBookById(bookId: Long) = bookDao.deleteBookById(bookId)

    // Bookshelves
    fun getAllBookshelves(): Flow<List<Bookshelf>> = bookshelfDao.getAllBookshelves()
    suspend fun getBookshelfById(id: Long) = bookshelfDao.getBookshelfById(id)
    suspend fun insertBookshelf(bookshelf: Bookshelf) = bookshelfDao.insertBookshelf(bookshelf)
    suspend fun updateBookshelf(bookshelf: Bookshelf) = bookshelfDao.updateBookshelf(bookshelf)
    suspend fun deleteBookshelf(bookshelf: Bookshelf) = bookshelfDao.deleteBookshelf(bookshelf)

    // Annotations - PostIt
    fun getPostItsByBook(bookId: Long) = annotationDao.getPostItsByBook(bookId)
    fun getAllPostIts() = annotationDao.getAllPostIts()
    fun getTotalPostItCount() = annotationDao.getTotalPostItCount()
    suspend fun insertPostIt(postIt: PostIt) = annotationDao.insertPostIt(postIt)
    suspend fun updatePostIt(postIt: PostIt) = annotationDao.updatePostIt(postIt)
    suspend fun deletePostIt(postIt: PostIt) = annotationDao.deletePostIt(postIt)

    // Annotations - Review
    fun getReviewsByBook(bookId: Long) = annotationDao.getReviewsByBook(bookId)
    fun getAllReviews() = annotationDao.getAllReviews()
    fun getTotalReviewCount() = annotationDao.getTotalReviewCount()
    suspend fun insertReview(review: BookReview) = annotationDao.insertReview(review)
    suspend fun updateReview(review: BookReview) = annotationDao.updateReview(review)
    suspend fun deleteReview(review: BookReview) = annotationDao.deleteReview(review)

    // Annotations - Quote
    fun getQuotesByBook(bookId: Long) = annotationDao.getQuotesByBook(bookId)
    fun getAllQuotes() = annotationDao.getAllQuotes()
    fun getTotalQuoteCount() = annotationDao.getTotalQuoteCount()
    suspend fun insertQuote(quote: Quote) = annotationDao.insertQuote(quote)
    suspend fun updateQuote(quote: Quote) = annotationDao.updateQuote(quote)
    suspend fun deleteQuote(quote: Quote) = annotationDao.deleteQuote(quote)

    // Annotations - HashTag
    fun getHashTagsByBook(bookId: Long) = annotationDao.getHashTagsByBook(bookId)
    fun getAllHashTags() = annotationDao.getAllHashTags()
    fun getDistinctTagCount() = annotationDao.getDistinctTagCount()
    suspend fun insertHashTag(hashTag: HashTag) = annotationDao.insertHashTag(hashTag)
    suspend fun deleteHashTag(hashTag: HashTag) = annotationDao.deleteHashTag(hashTag)

    // Aladin API
    suspend fun lookUpBookByIsbn(ttbKey: String, isbn: String): AladinItem? {
        return try {
            val response = aladinApi.itemLookUp(ttbKey = ttbKey, itemId = isbn)
            response.items.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchBooksOnline(ttbKey: String, query: String): List<AladinItem> {
        return try {
            val response = aladinApi.itemSearch(ttbKey = ttbKey, query = query)
            response.items
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Convert AladinItem to Book entity
    fun aladinItemToBook(item: AladinItem, bookshelfId: Long = 1): Book {
        // Parse author and translator from author field
        val authorParts = item.author.split(" (지은이)").first().trim()
        val translatorMatch = Regex("\\(옮긴이\\)").find(item.author)
        val translator = if (translatorMatch != null) {
            item.author.substringAfter("(지은이)").substringBefore("(옮긴이)").trim().trimStart(',').trim()
        } else ""

        return Book(
            isbn13 = item.isbn13.ifEmpty { item.isbn },
            title = item.title,
            subtitle = item.subInfo?.subTitle ?: "",
            author = authorParts,
            translator = translator,
            publisher = item.publisher,
            pubDate = item.pubDate,
            pageCount = item.subInfo?.itemPage ?: 0,
            priceStandard = item.priceStandard,
            coverUrl = item.cover,
            description = item.description,
            bookshelfId = bookshelfId
        )
    }
}
