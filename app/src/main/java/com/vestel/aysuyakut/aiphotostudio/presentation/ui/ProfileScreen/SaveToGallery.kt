package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

fun Context.saveUriToGallery(
    source: Uri,
    displayName: String = "AIPhoto_${System.currentTimeMillis()}",
    mimeType: String = "image/jpeg",
    targetFolderName: String = "AIPhotoStudio"
): Result<Uri> = runCatching {
    val resolver = contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + targetFolderName)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Failed to create MediaStore record")

        resolver.openOutputStream(uri).use { out ->
            resolver.openInputStream(source).use { input ->
                input?.copyToOrThrow(out)
            }
        }

        // Mark not pending
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } else {
        // Legacy: WRITE_EXTERNAL_STORAGE izni gerekli (Android 9 ve altı)
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, targetFolderName).apply { if (!exists()) mkdirs() }
        val dst = File(appDir, "$displayName.jpg")

        contentResolver.openInputStream(source).use { input ->
            FileOutputStream(dst).use { out ->
                input?.copyToOrThrow(out)
            }
        }
        MediaScannerConnection.scanFile(this, arrayOf(dst.absolutePath), arrayOf(mimeType), null)
        Uri.fromFile(dst)
    }
}

private fun InputStream.copyToOrThrow(out: OutputStream?) {
    requireNotNull(out) { "OutputStream null" }
    this.copyTo(out)
}
