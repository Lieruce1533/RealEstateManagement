package com.lieruce.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.lieruce.realestatemanager.data.AppDatabase
import androidx.core.net.toUri

class PropertyProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.lieruce.realestatemanager.provider"
        const val TABLE_NAME = "real_estate_items"
        
        // URIs addresses
        val URI_ITEM: Uri = "content://$AUTHORITY/$TABLE_NAME".toUri()

        // URI Matcher codes
        private const val PROPERTIES = 1
        private const val PROPERTY_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, PROPERTIES)
            addURI(AUTHORITY, "$TABLE_NAME/#", PROPERTY_ID)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val context = context ?: return null
        val database = AppDatabase.getDatabase(context)
        
        val code = uriMatcher.match(uri)
        return if (code == PROPERTIES || code == PROPERTY_ID) {
            val cursor: Cursor = if (code == PROPERTIES) {
                database.propertyDao().getAllPropertiesCursor()
            } else {
                val id = ContentUris.parseId(uri)
                database.propertyDao().getPropertyByIdCursor(id)
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    // Required methods, but we only implement reading for now as per requirement
    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
