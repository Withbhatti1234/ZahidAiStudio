package com.zahid.aistudio.ai

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

object BitmapUtils {
    fun load(context: Context, uri: Uri) =
        context.contentResolver.openInputStream(uri).use { stream ->
            BitmapFactory.decodeStream(stream)
        }
}
