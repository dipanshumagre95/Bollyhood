package com.app.bollyhood.util

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.loader.content.CursorLoader
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class PathUtils {
    fun getPDFPath(context: Context, uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, projection, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    companion object {
        fun getRealPath(context: Context, fileUri: Uri): String? {
            val realPath: String?
            // SDK < API11
            realPath = if (Build.VERSION.SDK_INT < 11) {
                getRealPathFromURI_BelowAPI11(context, fileUri)
            } else if (Build.VERSION.SDK_INT < 19) {
                getRealPathFromURI_API11to18(context, fileUri)
            } else {
                getRealPathFromURI_API19(context, fileUri)
            }
            return realPath
        }

        fun getImagePathFromURI(context: Context, uri: Uri?): String? {
            var cursor = context.contentResolver.query(uri!!, null, null, null, null)
            var path: String? = null
            if (cursor != null) {
                cursor.moveToFirst()
                var document_id = cursor.getString(0)
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    cursor.close()
                }
            }
            Log.d("getImagePathFromURI:::", path!!)
            return path
        }

        @SuppressLint("NewApi")
        fun getRealPathFromURI_API11to18(context: Context?, contentUri: Uri?): String? {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            var result: String? = null
            val cursorLoader = CursorLoader(
                context!!, contentUri!!, proj, null, null, null
            )
            val cursor = cursorLoader.loadInBackground()
            if (cursor != null) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                result = cursor.getString(column_index)
                cursor.close()
            }
            return result
        }

        fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri?): String {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            var column_index = 0
            var result = ""
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                result = cursor.getString(column_index)
                cursor.close()
                return result
            }
            return result
        }

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         * @author paulburke
         */
        @SuppressLint("NewApi")
        fun getRealPathFromURI_API19(context: Context, uri: Uri): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {
                    /**********working fine hai ye code bhi */

                    /*  String id = DocumentsContract.getDocumentId(uri);

                if (id != null && id.startsWith("raw:")) {
                    Log.v("gallery_image_Path:::id",id.substring(4));
                    return id.substring(4);
                }
               else if (id != null && id.startsWith("msf:")) {
                    Log.v("gallery_image_Path:::id",id.substring(4));

                    id= id.substring(4);
                }
               else if (id != null && id.contains(":")) {
                    Log.v("gallery_image_Path:::id",id.substring(4));

                    id=  id.substring(id.lastIndexOf(":") + 1);
                }
                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                };

                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    assert id != null;
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null) {
                            return path;
                        }
                    } catch (Exception e) {
                    }
                }*/

//                 path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    val fileName = getFileName(context, uri)
                    val cacheDir = getDocumentCacheDir(context)
                    val file = generateFileName(fileName, cacheDir)
                    var destinationPath: String? = null
                    if (file != null) {
                        destinationPath = file.absolutePath
                        saveFileFromUri(context, uri, destinationPath)
                    }
                    Log.v("gallery_image_Path:desp", destinationPath!!)
                    return destinationPath
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    } else {
                        val fileName = getFileName(context, uri)
                        val cacheDir = getDocumentCacheDir(context)
                        val file = generateFileName(fileName, cacheDir)
                        var destinationPath: String? = null
                        if (file != null) {
                            destinationPath = file.absolutePath
                            saveFileFromUri(context, uri, destinationPath)
                        }
                        Log.v("gallery_image_Path:desp", destinationPath!!)
                        return destinationPath
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                } else if (isGoogleDriveUri(uri)) {
                    //return getDriveFilePath(uri, context);
                    try {
                        val file = saveFileIntoExternalStorageByUri(context, uri)
                        return file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // return getFilePathFromURI(context,uri);
                    copyFileToInternalStorage(context, uri, "userfiles")
                    // return getRealPathFromURI(context,uri);
                } else {
                    getDataColumn(context, uri, null, null)
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        @Throws(Exception::class)
        fun saveFileIntoExternalStorageByUri(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalSize = inputStream!!.available()
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            val fileName = getFileName(context, uri)
            val file = makeEmptyFileIntoExternalStorageWithTitle(fileName)
            bis = BufferedInputStream(inputStream)
            bos = BufferedOutputStream(
                FileOutputStream(
                    file, false
                )
            )
            val buf = ByteArray(originalSize)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) != -1)
            bos.flush()
            bos.close()
            bis.close()
            return file
        }

        fun makeEmptyFileIntoExternalStorageWithTitle(title: String?): File {
            val root = Environment.getExternalStorageDirectory().absolutePath
            return File(root, title)
        }

        private fun isGoogleDriveUri(uri: Uri): Boolean {
            return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String {
            var column_index = 0
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(uri!!, proj, null, null, null, null)
                column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(column_index)
            } finally {
                cursor?.close()
            }
        }

        /***
         * Used for Android Q+
         * @param uri
         * @param newDirName if you want to create a directory, you can set this variable
         * @return
         */
        private fun copyFileToInternalStorage(
            context: Context,
            uri: Uri,
            newDirName: String
        ): String {
            val returnCursor = context.contentResolver.query(
                uri, arrayOf(
                    OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
                ), null, null, null
            )


            /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            val size = returnCursor.getLong(sizeIndex).toString()
            val output: File
            output = if (newDirName != "") {
                val dir = File(context.filesDir.toString() + "/" + newDirName)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                File(context.filesDir.toString() + "/" + newDirName + "/" + name)
            } else {
                File(context.filesDir.toString() + "/" + name)
            }
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(output)
                var read = 0
                val bufferSize = 1024
                val buffers = ByteArray(bufferSize)
                while (inputStream!!.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            } catch (e: Exception) {
                Log.e("Exception", e.message!!)
            }
            return output.path
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean { //, Context context
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
            var `is`: InputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                `is` = context.contentResolver.openInputStream(uri)
                bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
                val buf = ByteArray(1024)
                `is`!!.read(buf)
                do {
                    bos.write(buf)
                } while (`is`.read(buf) != -1)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                    bos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        /*888888888888888888888888888888888888888888*/
        fun getName(filename: String?): String? {
            if (filename == null) {
                return null
            }
            val index = filename.lastIndexOf('/')
            return filename.substring(index + 1)
        }

        fun generateFileName(name: String?, directory: File?): File? {
            var name = name ?: return null
            var file = File(directory, name)
            if (file.exists()) {
                var fileName = name
                var extension = ""
                val dotIndex = name.lastIndexOf('.')
                if (dotIndex > 0) {
                    fileName = name.substring(0, dotIndex)
                    extension = name.substring(dotIndex)
                }
                var index = 0
                while (file.exists()) {
                    index++
                    name = "$fileName($index)$extension"
                    file = File(directory, name)
                }
            }
            try {
                if (!file.createNewFile()) {
                    return null
                }
            } catch (e: IOException) {
                //Log.w(TAG, e);
                return null
            }

            //logDir(directory);
            return file
        }

        fun getFileName(context: Context, uri: Uri): String? {
            val mimeType = context.contentResolver.getType(uri)
            var filename: String? = null
            if (mimeType == null && context != null) {
                val path = getRealPath(context, uri)
                filename = if (path == null) {
                    getName(uri.toString())
                } else {
                    val file = File(path)
                    file.name
                }
            } else {
                val returnCursor = context.contentResolver.query(uri, null, null, null, null)
                if (returnCursor != null) {
                    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    returnCursor.moveToFirst()
                    filename = returnCursor.getString(nameIndex)
                    returnCursor.close()
                }
            }
            return filename
        }

        fun getDocumentCacheDir(context: Context): File {
            val DOCUMENTS_DIR = "documents"
            val dir = File(context.cacheDir, DOCUMENTS_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        fun isGoogleFilesUri(uri: Uri): Boolean {
            return "com.google.android.apps.docs.storage" == uri.authority
        }
    }
}
