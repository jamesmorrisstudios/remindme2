package us.jamesmorrisstudios.rrm2.util

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @receiver dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun Int.dpToPx(context: Context): Int {
    val value = this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return value.roundToInt()
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @receiver px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun Int.pxToD(context: Context): Int {
    val value = this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return value.roundToInt()
}