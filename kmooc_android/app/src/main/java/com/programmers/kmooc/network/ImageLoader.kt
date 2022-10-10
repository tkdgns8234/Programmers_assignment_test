package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

object ImageLoader {
    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        //TODO: String -> Bitmap 을 구현하세요
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val toUrl: URL
                toUrl = if (url.startsWith("http").not()) {
                    URL(BASE_URL + url)
                } else {
                    URL(url)
                }

                val bmp = BitmapFactory.decodeStream(toUrl.openConnection().getInputStream())
                withContext(Dispatchers.Main) {
                    completed(bmp)
                }
            } catch (e: Exception) {
                Log.e("loadImage", e.toString())
                withContext(Dispatchers.Main) {
                    completed(null)
                }
            }
        }
    }

    const val BASE_URL = "http://www.kmooc.kr"
}