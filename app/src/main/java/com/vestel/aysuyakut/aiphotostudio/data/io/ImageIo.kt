package com.vestel.aysuyakut.aiphotostudio.data.io

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.google.gson.Gson
import dagger.hilt.android.internal.Contexts.getApplication

object ImageIo {

    fun readUriBytes(ctx: Context, uri: Uri): ByteArray =
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)

    fun encodeAsDataUrlPng(bytes: ByteArray): String {
        val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:image/png;base64,$b64"
    }

    fun encodeAsBase64(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    fun decodeBase64ToBytes(b64: String): ByteArray {
        val pure = b64.substringAfter("base64,", b64)
        return Base64.decode(pure, Base64.DEFAULT)
    }

    fun savePng(ctx: Context, png: ByteArray, name: String): Uri? {
        val resolver = ctx.contentResolver
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv) ?: return null
        resolver.openOutputStream(uri)?.use { it.write(png) }
        return uri
    }

    fun toJson(obj: Any): String = Gson().toJson(obj)

    private fun uriToDataUrl(context: Context, uri: Uri): String {
        val bytes = ImageIo.readUriBytes(context, uri)
        return ImageIo.encodeAsDataUrlPng(bytes)
    }
}
