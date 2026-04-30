package com.vestel.aysuyakut.aiphotostudio.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AiRepoFirebase @Inject constructor(
    @ApplicationContext private val ctx: Context
) : AiRepo {

    private val model: GenerativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.0-flash-preview-image-generation",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
            }
        )
    }

    override suspend fun applyEffect(effect: String, image: Uri): Uri? = withContext(Dispatchers.IO) {
        val bitmap = ctx.contentResolver.openInputStream(image)?.use { BitmapFactory.decodeStream(it) }
            ?: return@withContext null

        val prompt = when (effect) {
            "Enhance (GFPGAN + ESRGAN)" ->
                "Enhance and upscale this photo about 2x, gently improve faces while preserving identity. Output only the edited image as PNG."
            "Anime" ->
                "Edit this photo into a clean anime style while preserving the subject and background. Output only the edited image as PNG."
            "Arcane" ->
                "Restyle this photo in a painterly Arcane-like look with dramatic lighting and thick brush strokes. Keep identity. Output only the edited image as PNG."
            "Cartoon" ->
                "Convert this photo into simple, high-contrast cartoon style with smooth shading. Keep the same subject. Output only the edited image as PNG."
            "Canny Lines" ->
                "Convert this photo into clean black line-art (edge/canny-like) on white background. Output only the edited image as PNG."
            "Depth 3D" ->
                "Give a faux 3D depth-map shading look while keeping composition. Output only the edited image as PNG."
            "OpenPose" ->
                "Preserve the exact human pose; restyle with flat colors and clear contours. Output only the edited image as PNG."
            else ->
                "Apply tasteful enhancement and denoise while keeping identity. Output only the edited image as PNG."
        }
        val input = content {
            image(bitmap)
            text(prompt)
        }
        val response = model.generateContent(input)

        val outBitmap: Bitmap = response
            .candidates.firstOrNull()
            ?.content?.parts
            ?.firstNotNullOfOrNull { it.asImageOrNull() }
            ?: return@withContext null

        val safe = effect.replace("[^a-zA-Z0-9_]".toRegex(), "_")
        saveTempPngToCache(outBitmap, "AI_${safe}")
    }

    private fun saveTempPngToCache(bitmap: Bitmap, prefix: String): Uri? {
        return runCatching {
            val file = File(ctx.cacheDir, "${prefix}_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                file
            )
        }.getOrNull()
    }

    override suspend fun applyPrompt(prompt: String, image: Uri): Uri? = withContext(Dispatchers.IO) {
        val bitmap = ctx.contentResolver
            .openInputStream(image)
            ?.use { BitmapFactory.decodeStream(it) }
            ?: return@withContext null

        val cleanPrompt = prompt.trim().ifEmpty {
            return@withContext null
        }

        val input = content {
            image(bitmap)
            text(cleanPrompt)
        }

        val response = model.generateContent(input)

        val outBitmap: Bitmap = response
            .candidates.firstOrNull()
            ?.content?.parts
            ?.firstNotNullOfOrNull { it.asImageOrNull() }
            ?: return@withContext null

        val safe = cleanPrompt.replace("[^a-zA-Z0-9_]".toRegex(), "_").take(24)
        saveTempPngToCache(outBitmap, "AI_${safe}")
    }
}
