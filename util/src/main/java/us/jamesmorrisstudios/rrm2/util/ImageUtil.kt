package us.jamesmorrisstudios.rrm2.util

import android.graphics.Bitmap

/**
 * Scale the bitmap to be bounded by the max dimensions.
 */
fun Bitmap.scaleBounded(maxWidth: Int, maxHeight: Int): Bitmap {
    return if (maxHeight > 0 && maxWidth > 0 && (maxHeight > height || maxWidth > width)) {
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    } else {
        this
    }
}