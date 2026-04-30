package com.vestel.aysuyakut.aiphotostudio.data.ai


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiEffects {

    private val model: GenerativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.0-flash-preview-image-generation",
            generationConfig = generationConfig {
                responseModalities = listOf(
                    ResponseModality.TEXT,
                    ResponseModality.IMAGE
                )
            }
        )
    }

    enum class Effect(val prompt: String) {
        CARTOON("Edit this photo into a clean cartoon/anime style. Preserve the subject's identity and background. Output only the edited image as PNG."),
        OIL_PAINT("Stylize this photo as a high-detail oil painting with visible brush strokes. Keep faces recognizable. Output only the edited image as PNG."),
        CYBERPUNK("Restyle this photo in neon cyberpunk night palette (teal/purple glow, rim lighting). Keep the same subject and pose. Output only the edited image as PNG."),
        WATER_COLOR("Convert this image into soft watercolor pastel style with paper texture, preserving composition. Output only the edited image as PNG."),
        VINTAGE90S("Transform into retro 90s magazine aesthetic (grain, halftone). Keep subject unchanged. Output only the edited image as PNG.")
    }

    suspend fun applyEffect(
        ctx: Context,
        imageUri: Uri,
        effect: Effect
    ): Uri? = withContext(Dispatchers.IO) {
        val source = loadBitmap(ctx, imageUri) ?: return@withContext null

        val prompt = content {
            image(source)
            text(effect.prompt)
        }

        val response = model.generateContent(prompt)
        val editedBitmap: Bitmap = response
            .candidates.firstOrNull()
            ?.content?.parts
            ?.firstNotNullOfOrNull { it.asImageOrNull() }
            ?: return@withContext null

        saveBitmapPng(ctx, editedBitmap, "AI_${effect.name.lowercase()}_${System.currentTimeMillis()}")
    }

    private fun loadBitmap(ctx: Context, uri: Uri): Bitmap? =
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

    private fun saveBitmapPng(ctx: Context, bmp: Bitmap, name: String): Uri? {
        val resolver = ctx.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= 29) put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        resolver.openOutputStream(uri)?.use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out) }
        if (Build.VERSION.SDK_INT >= 29) {
            values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        return uri
    }
}
