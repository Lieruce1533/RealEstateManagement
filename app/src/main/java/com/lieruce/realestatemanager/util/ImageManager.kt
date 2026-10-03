package com.lieruce.realestatemanager.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.graphics.scale

/**
 * ImageManager handles copying external image URIs (from camera or gallery)
 * into the app's secure private internal storage, compressing and resizing them
 * to ensure optimal file size and memory performance (offline-first).
 */
object ImageManager {

    private const val IMAGE_DIRECTORY = "property_images"
    private const val MAX_IMAGE_DIMENSION = 1024 // Max width/height in pixels for stored property photos
    private const val JPEG_QUALITY = 80 // 80% JPEG compression quality for a great balance of size and visual clarity

    /**
     * Decodes, resizes, compresses, and saves an image from a source Uri into the app's internal storage,
     * returning the resulting local file path string for database persistence.
     */
    fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            // Open input stream to inspect image dimensions without loading full bitmap into RAM
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            var sampleSize = 1

            // Calculate downsampling sample size to prevent OutOfMemory errors on huge camera photos
            if (originalWidth > MAX_IMAGE_DIMENSION || originalHeight > MAX_IMAGE_DIMENSION) {
                val halfWidth = originalWidth / 2
                val halfHeight = originalHeight / 2
                while ((halfWidth / sampleSize) >= MAX_IMAGE_DIMENSION && (halfHeight / sampleSize) >= MAX_IMAGE_DIMENSION) {
                    sampleSize *= 2
                }
            }

            // Decode downsampled bitmap using calculated sample size
            val secondInputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val sampledBitmap = BitmapFactory.decodeStream(secondInputStream, null, decodeOptions)
            secondInputStream.close()

            if (sampledBitmap == null) return null

            // Fine-tune scale so dimensions strictly fit within MAX_IMAGE_DIMENSION
            val finalBitmap = scaleBitmapToMaxDimension(sampledBitmap, MAX_IMAGE_DIMENSION)

            // Ensure internal storage directory exists
            val imagesDir = File(context.filesDir, IMAGE_DIRECTORY).apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            // Generate a unique filename and write compressed JPEG bytes
            val fileName = "property_img_${UUID.randomUUID()}.jpg"
            val destinationFile = File(imagesDir, fileName)

            val outputStream = FileOutputStream(destinationFile)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()

            // Recycle bitmaps to free native memory
            if (sampledBitmap != finalBitmap) {
                sampledBitmap.recycle()
            }
            finalBitmap.recycle()

            // Return absolute path string to be saved in Room database
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Helper function to scale a bitmap proportionally so neither width nor height exceeds maxDimension.
     */
    private fun scaleBitmapToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1f) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }

        return bitmap.scale(newWidth, newHeight)
    }
}
