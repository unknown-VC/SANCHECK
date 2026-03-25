package com.unknown.sancheck.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object DeepLinkHelper {

    fun openMillieSearch(context: Context, title: String) {
        val query = Uri.encode(title)
        val uri = Uri.parse("https://www.millie.co.kr/search/result?query=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "브라우저를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWelaaaSearch(context: Context, title: String) {
        val query = Uri.encode(title)
        val uri = Uri.parse("https://www.welaaa.com/search?keyword=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "브라우저를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
