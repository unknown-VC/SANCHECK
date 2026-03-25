package com.unknown.sancheck.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId")]
)
data class BookReview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
