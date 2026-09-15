package com.zahid.aistudio.export

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore

object ImageExporter {
    fun saveToGallery(context: Context, bitmap: Bitmap, displayName: String): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ZahidAIStudio")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } == true
        } catch (_: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }
}
