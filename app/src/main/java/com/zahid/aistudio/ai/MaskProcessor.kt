package com.zahid.aistudio.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

object MaskProcessor {
    fun eraseCircle(source: Bitmap, cx: Float, cy: Float, radius: Float): Bitmap {
        val out = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        canvas.drawCircle(cx, cy, radius, paint)
        paint.xfermode = null
        return out
    }

    fun removeBackgroundPreview(source: Bitmap): Bitmap {
        // UI-safe preview fallback: make near-white border/background transparent.
        // A production version should use a person/object segmentation model.
        val out = source.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(out.width * out.height)
        out.getPixels(pixels, 0, out.width, 0, 0, out.width, out.height)
        for (i in pixels.indices) {
            val c = pixels[i]
            val r = android.graphics.Color.red(c)
            val g = android.graphics.Color.green(c)
            val b = android.graphics.Color.blue(c)
            if (r > 238 && g > 238 && b > 238) pixels[i] = 0x00FFFFFF
        }
        out.setPixels(pixels, 0, out.width, 0, 0, out.width, out.height)
        return out
    }
}
