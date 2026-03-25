package com.unknown.sancheck.data.local.dao

import androidx.room.*
import com.unknown.sancheck.data.local.entity.Bookshelf
import kotlinx.coroutines.flow.Flow

@Dao
interface BookshelfDao {

    @Query("SELECT * FROM bookshelves ORDER BY id ASC")
    fun getAllBookshelves(): Flow<List<Bookshelf>>

    @Query("SELECT * FROM bookshelves WHERE id = :id")
    suspend fun getBookshelfById(id: Long): Bookshelf?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookshelf(bookshelf: Bookshelf): Long

    @Update
    suspend fun updateBookshelf(bookshelf: Bookshelf)

    @Delete
    suspend fun deleteBookshelf(bookshelf: Bookshelf)

    @Query("SELECT COUNT(*) FROM bookshelves")
    suspend fun getCount(): Int
}
