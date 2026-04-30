package com.vestel.aysuyakut.aiphotostudio.presentation.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vestel.aysuyakut.aiphotostudio.data.repository.AiRepo
import com.vestel.aysuyakut.aiphotostudio.data.repository.ImageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AiUiState(
    val selectedImage: Uri? = null,
    val resultImage: Uri? = null,
    val selectedEffect: String? = null,
    val isLoading: Boolean = false
) { val hasResult: Boolean get() = resultImage != null }

data class EffectPreset(val title: String)

@HiltViewModel
class AiViewModel @Inject constructor(
    app: Application,
    private val aiRepo: AiRepo,
    private val repo: ImageRepo,
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState

    val effects: List<EffectPreset> = listOf(
        EffectPreset("Enhance (GFPGAN + ESRGAN)"),
        EffectPreset("Anime"),
        EffectPreset("Arcane"),
        EffectPreset("Cartoon"),
        EffectPreset("Canny Lines"),
        EffectPreset("Depth 3D"),
        EffectPreset("OpenPose")
    )

    fun selectEffect(title: String) {
        _uiState.update { it.copy(selectedEffect = title) }
    }

    fun onGalleryPicked(uri: Uri) {
        _uiState.update { it.copy(selectedImage = uri, resultImage = null) }
    }

    fun saveSelectedImage(
        ctx: Context,
        uri: Uri,
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                try { repo.saveUri(uri) > 0 } catch (_: Throwable) { false }
            }
            onDone(ok)
        }
    }

    fun saveResultImage(
        ctx: Context,
        onDone: (Boolean) -> Unit = {}
    ) {
        val uri = uiState.value.resultImage ?: return onDone(false)
        saveSelectedImage(ctx, uri, onDone)
    }

    fun shareResult(context: Context) {
        val uri = _uiState.value.resultImage ?: _uiState.value.selectedImage ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share image"))
    }


    fun applySelectedModel(
        title: String,
        source: Uri,
        context: Context,
        onDone: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // AiRepo tarafı kendi içinde model/prompt ayarlıyor
                val result: Uri? = aiRepo.applyEffect(effect = title, image = source)
                _uiState.update { it.copy(resultImage = result, isLoading = false) }
                onDone(result != null)
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onDone(false)
            }
        }
    }

    fun applyPromptGemini(
        prompt: String,
        source: Uri,
        context: Context,
        onDone: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val out = aiRepo.applyPrompt(prompt, source)
                _uiState.update { it.copy(resultImage = out, isLoading = false) }
                onDone(out != null)
            } catch (_: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onDone(false)
            }
        }
    }
}
