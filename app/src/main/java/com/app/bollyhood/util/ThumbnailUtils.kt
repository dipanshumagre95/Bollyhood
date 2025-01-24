package com.app.bollyhood.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object ThumbnailUtils {

    fun getYouTubeVideoThumbnail(videoUrl: String, context: Context, callback: (Bitmap?) -> Unit) {
        val videoId = extractYouTubeVideoId(videoUrl)
        if (videoId != null) {
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

            // Use Glide to fetch the thumbnail as a Bitmap
            Glide.with(context)
                .asBitmap()
                .load(thumbnailUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        callback(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        callback(null)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        callback(null)
                    }
                })
        } else {
            callback(null)
        }
    }

    fun extractYouTubeVideoId(videoUrl: String): String? {
        val regex = "(?:https?://)?(?:www\\.|m\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
        val matchResult = Regex(regex).find(videoUrl)
        return matchResult?.groups?.get(1)?.value
    }

}