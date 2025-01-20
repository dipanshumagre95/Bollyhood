
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

object ImagePickerUtil {

    fun pickImageFromGallery(activity: Context, startForProfileImageResult: ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        startForProfileImageResult.launch(galleryIntent)
    }
}
