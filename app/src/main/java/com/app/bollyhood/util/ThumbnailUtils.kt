package com.app.bollyhood.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

object ThumbnailUtils {

    fun getVideoThumbnail(uri: Uri, context: Context): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            // Set the data source for the video URI
            retriever.setDataSource(context, uri)

            // Extract a frame at 1 second (1000000 microseconds)
            retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}