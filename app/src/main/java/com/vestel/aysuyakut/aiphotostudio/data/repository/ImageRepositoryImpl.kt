package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import com.vestel.aysuyakut.aiphotostudio.data.db.dao.ImageDao
import com.vestel.aysuyakut.aiphotostudio.data.db.entity.ImageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ImageDao,
    private val auth: FirebaseAuth
) : ImageRepo {

    private val currentOwnerId: String?
        get() = auth.currentUser?.uid

    override fun getAll(): Flow<List<ImageEntity>> = dao.getAll(currentOwnerId)

    fun getAll(ownerId: String?): Flow<List<ImageEntity>> = dao.getAll(ownerId)

    override suspend fun saveUri(picked: Uri?): Long {
        if (picked == null) return -1
        val final = copyToUserPictures(picked, currentOwnerId)
        return dao.insert(ImageEntity(uri = final.toString(), ownerId = currentOwnerId))
    }

    private suspend fun copyToUserPictures(src: Uri, ownerId: String?): Uri = withContext(Dispatchers.IO) {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        val bucket = ownerId ?: "guest"
        val userDir = File(base, bucket).apply { if (!exists()) mkdirs() }

        File(userDir, ".nomedia").apply { if (!exists()) createNewFile() }

        val name = "img_${System.currentTimeMillis()}.jpg"
        val dst = File(userDir, name)

        context.contentResolver.openInputStream(src).use { input ->
            FileOutputStream(dst).use { out ->
                requireNotNull(input) { "InputStream null döndü." }.copyTo(out)
            }
        }
        Uri.fromFile(dst)
    }

    override suspend fun delete(id: Long, uri: Uri): Boolean {
        val deletedFromFs: Boolean = try {
            when (uri.scheme?.lowercase()) {
                "content" -> context.contentResolver.delete(uri, null, null) > 0
                "file" -> uri.path?.let { File(it).delete() } ?: false
                else -> {
                    val crRows = try { context.contentResolver.delete(uri, null, null) } catch (_: Throwable) { 0 }
                    val fileOk = uri.path?.let { File(it).delete() } ?: false
                    crRows > 0 || fileOk
                }
            }
        } catch (_: Throwable) { false }

        val rows = dao.deleteById(id, currentOwnerId)
        return rows > 0 && deletedFromFs
    }
}
