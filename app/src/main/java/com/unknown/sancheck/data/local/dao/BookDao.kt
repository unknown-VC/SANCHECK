package com.unknown.sancheck.data.local.dao

import androidx.room.*
import com.unknown.sancheck.data.local.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE bookshelfId = :shelfId ORDER BY dateAdded DESC")
    fun getBooksByShelf(shelfId: Long): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): Book?

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookByIdFlow(bookId: Long): Flow<Book?>

    @Query("SELECT * FROM books WHERE isbn13 = :isbn LIMIT 1")
    suspend fun getBookByIsbn(isbn: String): Book?

    @Query("""
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%'
        OR author LIKE '%' || :query || '%'
        OR publisher LIKE '%' || :query || '%'
        ORDER BY dateAdded DESC
    """)
    fun searchBooks(query: String): Flow<List<Book>>

    // Sort queries
    @Query("SELECT * FROM books ORDER BY pubDate DESC")
    fun getBooksSortedByPubDate(): Flow<List<Book>>

    @Query("SELECT * FROM books ORDER BY rating DESC")
    fun getBooksSortedByRating(): Flow<List<Book>>

    @Query("SELECT * FROM books ORDER BY priceStandard DESC")
    fun getBooksSortedByPrice(): Flow<List<Book>>

    @Query("SELECT * FROM books ORDER BY pageCount DESC")
    fun getBooksSortedByPageCount(): Flow<List<Book>>

    // Stats
    @Query("SELECT COUNT(*) FROM books")
    fun getTotalBookCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT author) FROM books WHERE author != ''")
    fun getDistinctAuthorCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT translator) FROM books WHERE translator != ''")
    fun getDistinctTranslatorCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT publisher) FROM books WHERE publisher != ''")
    fun getDistinctPublisherCount(): Flow<Int>

    // This year's books
    @Query("SELECT * FROM books WHERE dateAdded >= :startOfYear ORDER BY dateAdded DESC")
    fun getBooksAddedThisYear(startOfYear: Long): Flow<List<Book>>

    // Filter by availability
    @Query("SELECT * FROM books WHERE availableOnMillie = 1 ORDER BY dateAdded DESC")
    fun getMillieBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE availableOnWelaaa = 1 ORDER BY dateAdded DESC")
    fun getWelaaaBooks(): Flow<List<Book>>

    // Grouped stats
    @Query("SELECT author, COUNT(*) as cnt FROM books WHERE author != '' GROUP BY author ORDER BY cnt DESC")
    fun getAuthorStats(): Flow<List<AuthorStat>>

    @Query("SELECT translator, COUNT(*) as cnt FROM books WHERE translator != '' GROUP BY translator ORDER BY cnt DESC")
    fun getTranslatorStats(): Flow<List<TranslatorStat>>

    @Query("SELECT publisher, COUNT(*) as cnt FROM books WHERE publisher != '' GROUP BY publisher ORDER BY cnt DESC")
    fun getPublisherStats(): Flow<List<PublisherStat>>

    // Export all
    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    suspend fun getAllBooksSync(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
}

data class AuthorStat(val author: String, val cnt: Int)
data class TranslatorStat(val translator: String, val cnt: Int)
data class PublisherStat(val publisher: String, val cnt: Int)
