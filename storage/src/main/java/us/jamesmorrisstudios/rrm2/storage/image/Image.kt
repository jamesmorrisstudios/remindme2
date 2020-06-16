package us.jamesmorrisstudios.rrm2.storage.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import us.jamesmorrisstudios.rrm2.storage.BuildConfig
import us.jamesmorrisstudios.rrm2.util.scaleBounded
import java.io.File
import java.io.FileOutputStream

/**
 * File provider for images.
 */
class ImageProvider : FileProvider()

/**
 * Image Manager.
 */
interface Image {

    /**
     * Write a image bitmap out into a file and return the provider uri for it.
     */
    suspend fun write(name: String, image: Bitmap): Uri?

    /**
     * Write a image from the given uri into a file and return the provider uri for it.
     */
    suspend fun write(name: String, uri: Uri): Uri?

    /**
     * Returns the image bitmap for the given image if it exists.
     *
     * Typically this should be done with a proper caching system using the Coil library directly but this exists for completeness.
     */
    suspend fun read(name: String): Bitmap?

    /**
     * Delete the given image if it exists.
     */
    suspend fun delete(name: String): Boolean

    /**
     * Returns the provider uri for the given image if it exists.
     */
    suspend fun getUri(name: String): Uri?

}

/**
 * Implementation of the Image Manager.
 */
internal class ImageImpl(private val context: Context) : Image {
    private val MAX_IMAGE_WIDTH = 1024
    private val MAX_IMAGE_HEIGHT = 1024

    /**
     * {inherited}
     */
    override suspend fun write(name: String, image: Bitmap): Uri? = withContext(Dispatchers.IO) {
        val result = runCatching {
            val imageScaled = image.scaleBounded(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT)
            val file = File(getImageDir(), name)
            FileOutputStream(file).use {
                imageScaled.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.flush()
                FileProvider.getUriForFile(context, getAuthority(), file)
            }
        }
        result.getOrNull()
    }

    /**
     * {inherited}
     */
    override suspend fun write(name: String, uri: Uri): Uri? {
        val result = runCatching {
            val image = Coil.get(uri).toBitmap()
            write(name, image)
        }
        return result.getOrNull()
    }

    /**
     * {inherited}
     */
    override suspend fun read(name: String): Bitmap? {
        val result = runCatching {
            val file = File(getImageDir(), name)
            Coil.get(file).toBitmap()
        }
        return result.getOrNull()
    }

    /**
     * {inherited}
     */
    override suspend fun delete(name: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(getImageDir(), name)
            if(file.exists()) {
                file.delete()
            }
        }.isSuccess
    }

    /**
     * {inherited}
     */
    override suspend fun getUri(name: String): Uri? {
        val file = File(getImageDir(), name)
        if(!file.exists()) {
            return null
        }
        return FileProvider.getUriForFile(context, getAuthority(), file)
    }

    /**
     * Returns the authority of the image provider.
     */
    private fun getAuthority(): String = context.packageName + BuildConfig.IMAGE_AUTHORITY

    /**
     * Returns the image directory file.
     */
    private suspend fun getImageDir(): File = withContext(Dispatchers.IO) {
        File(context.filesDir, "images").apply {
            if(!exists()) {
                mkdirs()
            }
        }
    }

}

