
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

object ImagePickerUtil {

    fun pickImageFromGallery(activity: AppCompatActivity, startForProfileImageResult: ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        startForProfileImageResult.launch(galleryIntent)
    }
}
