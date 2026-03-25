package com.unknown.sancheck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unknown.sancheck.data.local.dao.AnnotationDao
import com.unknown.sancheck.data.local.dao.BookDao
import com.unknown.sancheck.data.local.dao.BookshelfDao
import com.unknown.sancheck.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Book::class,
        Bookshelf::class,
        PostIt::class,
        BookReview::class,
        Quote::class,
        HashTag::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun bookshelfDao(): BookshelfDao
    abstract fun annotationDao(): AnnotationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sancheck_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insert default bookshelf "모든 책"
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.bookshelfDao()?.insertBookshelf(
                                    Bookshelf(id = 1, name = "모든 책", colorIndex = 0)
                                )
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
