package com.unknown.sancheck.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object DeepLinkHelper {

    fun openMillieSearch(context: Context, title: String) {
        val query = Uri.encode(title)
        val uri = Uri.parse("https://www.millie.co.kr/search/result?query=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun openWelaaaSearch(context: Context, title: String) {
        val query = Uri.encode(title)
        val uri = Uri.parse("https://www.welaaa.com/search?keyword=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}
