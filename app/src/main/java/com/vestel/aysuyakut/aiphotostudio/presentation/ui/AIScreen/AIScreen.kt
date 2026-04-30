@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AiViewModel
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.vestel.aysuyakut.aiphotostudio.SystemBarsPerScreen
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

private enum class Provider { Gemini }

val Bg = Color(0xFF0F0F12)
val SurfaceC = Color(0xFF1A1A1F)
val Surface2C = Color(0xFF141417)
val Accent = Color(0xFF6E56D7)
val AccentAlt = Color(0xFF21D4FD)
val OnDark = Color(0xFFECECEC)

private enum class InputMode { List, Prompt }

@Composable
fun AIScreen(vm: AiViewModel = hiltViewModel()) {
    SystemBarsPerScreen(
        statusBar = Bg,
        navBar = Bg,
        fitsSystemWindows = true
    )
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDeniedDialog by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    // NEW: which input mode?
    var mode by rememberSaveable { mutableStateOf(InputMode.List) }
    var promptText by rememberSaveable { mutableStateOf("") }

    // Gallery
    val getContent = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) vm.onGalleryPicked(uri) }

    val requestGalleryPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) getContent.launch("image/*") else showDeniedDialog = true }

    val galleryPermission = remember {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Manifest.permission.READ_MEDIA_IMAGES
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    val openGallery = remember {
        {
            val granted = ContextCompat.checkSelfPermission(
                context, galleryPermission
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) getContent.launch("image/*")
            else requestGalleryPermission.launch(galleryPermission)
        }
    }

    // Camera
    var provider by rememberSaveable { mutableStateOf(Provider.Gemini) } // sadece Gemini

    var lastCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && lastCameraUri != null) {
            vm.onGalleryPicked(lastCameraUri!!)
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val newFile = File.createTempFile("camera_", ".jpg", context.cacheDir)
            val newUri = FileProvider.getUriForFile(
                context,
                "com.vestel.aysuyakut.aiphotostudio.fileprovider",
                newFile
            )
            lastCameraUri = newUri
            cameraLauncher.launch(newUri)
        } else showDeniedDialog = true
    }

    val openCamera = remember {
        {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                val newFile = File.createTempFile("camera_", ".jpg", context.cacheDir)
                val newUri = FileProvider.getUriForFile(
                    context,
                    "com.vestel.aysuyakut.aiphotostudio.fileprovider",
                    newFile
                )
                lastCameraUri = newUri
                cameraLauncher.launch(newUri)
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    GradientTitle(
                        "AI Studio",
                        listOf(AccentAlt, Accent),
                        MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                },

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Bg
    ) { padding ->
        Box(Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Preview
                item {
                    androidx.compose.material3.Surface(
                        color = SurfaceC, contentColor = OnDark, shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (state.selectedImage == null) {
                                Text(
                                    "Select an image to start",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnDark.copy(alpha = .6f)
                                )
                            } else {
                                AsyncImage(
                                    model = state.selectedImage,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Surface2C),
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .align(Alignment.TopCenter),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OverlayFab(
                                    icon = Icons.Default.Image,
                                    contentDescription = "Open gallery",
                                    onClick = openGallery
                                )
                                OverlayFab(
                                    icon = Icons.Default.PhotoCamera,
                                    contentDescription = "Open camera",
                                    onClick = openCamera
                                )
                            }
                        }
                    }
                }

                item {
                    TabRow(
                        selectedTabIndex = if (mode == InputMode.List) 0 else 1,
                        containerColor = Color.Transparent,
                        contentColor = Accent,
                        divider = {}
                    ) {
                        Tab(
                            selected = mode == InputMode.List,
                            onClick = { mode = InputMode.List },
                            text = { Text("List") }
                        )
                        Tab(
                            selected = mode == InputMode.Prompt,
                            onClick = { mode = InputMode.Prompt },
                            text = { Text("Prompt") }
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Surface2C)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Accent.copy(.18f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .clickable { provider = Provider.Gemini },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                    RadioButton(
                                        selected = true,
                                        onClick = { provider = Provider.Gemini },
                                        modifier = Modifier.size(18.dp),
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Accent,
                                            unselectedColor = OnDark.copy(.6f)
                                        )
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Gemini",
                                    color = OnDark,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                item {
                    when (mode) {
                        InputMode.List -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val presets = vm.effects
                                    presets.forEach { preset ->
                                        val label = preset.title
                                        FilterChip(
                                            selected = state.selectedEffect == label,
                                            onClick = { vm.selectEffect(label) },
                                            label = {
                                                Text(
                                                    label,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = Color.White
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                containerColor = Surface2C,
                                                selectedContainerColor = Accent.copy(alpha = .18f),
                                                labelColor = Color.White,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        InputMode.Prompt -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                val focusManager = LocalFocusManager.current
                                val keyboard = LocalSoftwareKeyboardController.current

                                OutlinedTextField(
                                    value = promptText,
                                    onValueChange = { promptText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp)
                                        .onPreviewKeyEvent { e ->
                                            if (e.key == Key.Enter && e.type == KeyEventType.KeyUp) {
                                                keyboard?.hide()
                                                focusManager.clearFocus()
                                                true
                                            } else false
                                        },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    placeholder = { Text("Describe the effect you want (e.g. \"make hair red, cinematic lighting\")") },
                                    label = { Text("Prompt") },
                                    singleLine = false,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboard?.hide()
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Surface2C,
                                        unfocusedContainerColor = Surface2C,
                                        focusedTextColor = OnDark,
                                        unfocusedTextColor = OnDark,
                                        focusedLabelColor = OnDark,
                                        unfocusedLabelColor = OnDark.copy(.8f),
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    val canApply = when (mode) {
                        InputMode.List -> state.selectedImage != null &&
                                state.selectedEffect != null &&
                                !state.isLoading
                        InputMode.Prompt -> state.selectedImage != null &&
                                promptText.isNotBlank() &&
                                !state.isLoading
                    }

                    Button(
                        onClick = {
                            val src = state.selectedImage ?: return@Button
                            when (mode) {
                                InputMode.List -> {
                                    val title = state.selectedEffect ?: return@Button
                                    vm.applySelectedModel(
                                        title = title,
                                        source = src,
                                        context = context
                                    ) { ok ->
                                        if (ok) showPreview = true
                                        else scope.launch { Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show() }
                                    }
                                }
                                InputMode.Prompt -> {
                                    val prompt = promptText.trim()
                                    if (prompt.isEmpty()) return@Button
                                    vm.applyPromptGemini(
                                        prompt = prompt,
                                        source = src,
                                        context = context
                                    ) { ok ->
                                        if (ok) showPreview = true
                                        else scope.launch { Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show() }
                                    }
                                }
                            }
                        },
                        enabled = canApply,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            if (state.isLoading) stringResource(R.string.generating) else "Apply"
                        )
                    }
                }
            }
            LoadingOverlay(
                visible = state.isLoading,
                message = "Processing photo...",


                )
        }
    }

    // Preview dialog
    if (showPreview) {
        state.resultImage?.let { uri ->
            EffectPreviewDialog(
                imageUri = uri,
                onSave = {
                    vm.saveSelectedImage(context, uri) { ok ->
                        Toast.makeText(
                            context,
                            if (ok) context.getString(R.string.save_message)
                            else context.getString(R.string.save_failed_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showPreview = false
                },
                onCancel = { showPreview = false }
            )
        }
    }
}

@Composable
private fun GradientTitle(text: String, colors: List<Color>, style: TextStyle) {
    val brush = remember(colors) { Brush.linearGradient(colors) }
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(brush = brush)) { append(text) }
        },
        style = style,
        color = Color.Unspecified
    )
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

@Composable
private fun EffectPreviewDialog(
    imageUri: Uri,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = SurfaceC,
        title = { Text("Preview", color = OnDark) },
        text = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface2C)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Effect preview",
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) { Text("Save", color = OnDark) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = OnDark.copy(alpha = 0.85f))
            }
        }
    )
}
@Composable
private fun OverlayFab(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = Surface2C.copy(alpha = 0.92f),
        contentColor = Accent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(icon, contentDescription)
    }
}
@Composable
private fun LoadingOverlay(
    visible: Boolean,
    message: String = "Processing…"
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                // Tıklamaları yut (alttaki UI kilitlensin)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    color = OnDark,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

            }
        }
    }
}
