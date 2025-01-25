
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.regex.Pattern

object ImagePickerUtil {

    var youtubePlayers: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer? = null

    fun pickImageFromGallery(activity: Context, startForProfileImageResult: ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        startForProfileImageResult.launch(galleryIntent)
    }

    private fun extractVideoIdFromUrl(url: String): String? {
        val pattern = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11}).*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)

        return if (matcher.matches()) {
            matcher.group(1) // Extract the video ID
        } else {
            null
        }
    }

     fun playVideo(videoUrl: String, youtubePlayerView: YouTubePlayerView) {
        val options = IFramePlayerOptions.Builder()
            .controls(0)
            .modestBranding(0)
            .autoplay(0)
            .rel(0)
            .build()


        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                val videoId = extractVideoIdFromUrl(videoUrl) ?: ""
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.cueVideo(videoId, 0f)
                youtubePlayers = youTubePlayer
            }

            override fun onStateChange(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, state: PlayerConstants.PlayerState) {
            }
        }

        youtubePlayerView.addYouTubePlayerListener(listener)
        youtubePlayerView.enableAutomaticInitialization = false
        youtubePlayerView.initialize(listener, options)
    }

    fun stopVideo() {
        youtubePlayers?.pause() // Pause the video
        youtubePlayers = null // Clear the reference
    }
}
