package com.unknown.sancheck.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sancheck_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TTB_KEY = "ttb_key"
        private const val KEY_VIEW_MODE = "view_mode"
        private const val KEY_CURRENT_SHELF = "current_shelf"
    }

    var ttbKey: String
        get() = prefs.getString(KEY_TTB_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TTB_KEY, value).apply()

    // 0 = spine view, 1 = cover grid view
    var viewMode: Int
        get() = prefs.getInt(KEY_VIEW_MODE, 0)
        set(value) = prefs.edit().putInt(KEY_VIEW_MODE, value).apply()

    var currentShelfId: Long
        get() = prefs.getLong(KEY_CURRENT_SHELF, 0L) // 0 = all books
        set(value) = prefs.edit().putLong(KEY_CURRENT_SHELF, value).apply()
}
