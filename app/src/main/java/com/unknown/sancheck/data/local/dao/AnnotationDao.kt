package com.unknown.sancheck.data.local.dao

import androidx.room.*
import com.unknown.sancheck.data.local.entity.BookReview
import com.unknown.sancheck.data.local.entity.HashTag
import com.unknown.sancheck.data.local.entity.PostIt
import com.unknown.sancheck.data.local.entity.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnotationDao {

    // PostIt
    @Query("SELECT * FROM post_its WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getPostItsByBook(bookId: Long): Flow<List<PostIt>>

    @Query("SELECT * FROM post_its ORDER BY createdAt DESC")
    fun getAllPostIts(): Flow<List<PostIt>>

    @Query("SELECT COUNT(*) FROM post_its")
    fun getTotalPostItCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostIt(postIt: PostIt): Long

    @Update
    suspend fun updatePostIt(postIt: PostIt)

    @Delete
    suspend fun deletePostIt(postIt: PostIt)

    // BookReview
    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getReviewsByBook(bookId: Long): Flow<List<BookReview>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<BookReview>>

    @Query("SELECT COUNT(*) FROM reviews")
    fun getTotalReviewCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: BookReview): Long

    @Update
    suspend fun updateReview(review: BookReview)

    @Delete
    suspend fun deleteReview(review: BookReview)

    // Quote
    @Query("SELECT * FROM quotes WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getQuotesByBook(bookId: Long): Flow<List<Quote>>

    @Query("SELECT * FROM quotes ORDER BY createdAt DESC")
    fun getAllQuotes(): Flow<List<Quote>>

    @Query("SELECT COUNT(*) FROM quotes")
    fun getTotalQuoteCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: Quote): Long

    @Update
    suspend fun updateQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    // HashTag
    @Query("SELECT * FROM hash_tags WHERE bookId = :bookId ORDER BY tag ASC")
    fun getHashTagsByBook(bookId: Long): Flow<List<HashTag>>

    @Query("SELECT * FROM hash_tags ORDER BY tag ASC")
    fun getAllHashTags(): Flow<List<HashTag>>

    @Query("SELECT COUNT(DISTINCT tag) FROM hash_tags")
    fun getDistinctTagCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashTag(hashTag: HashTag): Long

    @Delete
    suspend fun deleteHashTag(hashTag: HashTag)
}
