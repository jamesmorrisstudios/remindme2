package us.jamesmorrisstudios.rrm2.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

/**
 * Extension that retrieves the Uri that points to a resource from a resource id.
 */
fun Int.resourceIdToUri(context: Context): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(this))
        .appendPath(context.resources.getResourceTypeName(this))
        .appendPath(context.resources.getResourceEntryName(this))
        .build()
}