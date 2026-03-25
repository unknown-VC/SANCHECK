package com.unknown.sancheck.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = Bookshelf::class,
            parentColumns = ["id"],
            childColumns = ["bookshelfId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("bookshelfId"), Index("isbn13")]
)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isbn13: String = "",
    val title: String,
    val subtitle: String = "",
    val author: String = "",
    val translator: String = "",
    val publisher: String = "",
    val pubDate: String = "",
    val pageCount: Int = 0,
    val priceStandard: Int = 0,
    val coverUrl: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val bookshelfId: Long = 1,
    val quantity: Int = 1,
    val memo: String = "",
    val availableOnMillie: Boolean = false,
    val availableOnWelaaa: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
