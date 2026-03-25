package com.unknown.sancheck.di

import android.content.Context
import com.unknown.sancheck.data.local.AppDatabase
import com.unknown.sancheck.data.remote.RetrofitClient
import com.unknown.sancheck.data.repository.BookRepository
import com.unknown.sancheck.util.ExcelManager
import com.unknown.sancheck.util.PrefsManager

class AppContainer(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val aladinApi = RetrofitClient.aladinApi

    val bookRepository = BookRepository(
        bookDao = database.bookDao(),
        bookshelfDao = database.bookshelfDao(),
        annotationDao = database.annotationDao(),
        aladinApi = aladinApi
    )

    val prefsManager = PrefsManager(context)
    val excelManager = ExcelManager()
}
