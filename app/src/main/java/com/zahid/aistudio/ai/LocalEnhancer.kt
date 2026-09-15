package com.zahid.aistudio.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

object LocalEnhancer {
    /**
     * Fast on-device enhancement: brightness, contrast and mild unsharp-mask style sharpening.
     * This is deterministic image processing, not a generative AI model.
     */
    fun enhance(source: Bitmap): Bitmap {
        val input = source.copy(Bitmap.Config.ARGB_8888, false)
        val output = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
        val w = input.width
        val h = input.height

        fun clamp(v: Int) = max(0, min(255, v))
        for (y in 0 until h) {
            for (x in 0 until w) {
                val c = input.getPixel(x, y)
                val center = Color.red(c) + Color.green(c) + Color.blue(c)
                var r = Color.red(c)
                var g = Color.green(c)
                var b = Color.blue(c)

                // Slight local sharpening using the pixel above/below/left/right.
                if (x > 0 && x < w - 1 && y > 0 && y < h - 1) {
                    val l = input.getPixel(x - 1, y)
                    val rr = input.getPixel(x + 1, y)
                    val u = input.getPixel(x, y - 1)
                    val d = input.getPixel(x, y + 1)
                    val avgR = (Color.red(l) + Color.red(rr) + Color.red(u) + Color.red(d)) / 4
                    val avgG = (Color.green(l) + Color.green(rr) + Color.green(u) + Color.green(d)) / 4
                    val avgB = (Color.blue(l) + Color.blue(rr) + Color.blue(u) + Color.blue(d)) / 4
                    r += ((r - avgR) * 0.18f).toInt()
                    g += ((g - avgG) * 0.18f).toInt()
                    b += ((b - avgB) * 0.18f).toInt()
                }

                // Gentle contrast lift around the midpoint.
                fun enhanceChannel(v: Int): Int {
                    val contrast = 1.08f
                    return clamp(((v - 128) * contrast + 128).toInt())
                }
                r = enhanceChannel(r)
                g = enhanceChannel(g)
                b = enhanceChannel(b)

                output.setPixel(x, y, Color.argb(Color.alpha(c), clamp(r), clamp(g), clamp(b)))
            }
        }
        return output
    }
}
