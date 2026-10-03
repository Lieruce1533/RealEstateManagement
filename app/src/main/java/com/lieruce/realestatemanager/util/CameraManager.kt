package com.lieruce.realestatemanager.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CameraManager provides secure temporary file creation and Content URIs
 * via FileProvider for capturing photos with the device camera.
 */
object CameraManager {

    /**
     * Creates a secure temporary image file in the app cache directory and returns its content URI.
     */
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}
