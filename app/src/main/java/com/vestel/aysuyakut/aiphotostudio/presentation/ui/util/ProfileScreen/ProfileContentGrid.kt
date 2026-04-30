package com.vestel.aysuyakut.aiphotostudio.presentation.ui.util.ProfileScreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen.ProfileUiState
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen.OldPhoto
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen.saveUriToGallery

import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
val Bg = Color(0xFF0F0F12)
val Surface = Color(0xFF1A1A1F)
val Surface2 = Color(0xFF141417)
val Accent = Color(0xFF6E56D7)
val AccentAlt = Color(0xFF21D4FD)
val OnDark = Color(0xFFECECEC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContentGrid(
    state: ProfileUiState,
    innerPadding: PaddingValues,
    onEditProfile: () -> Unit,
    onOpenDrawer: () -> Unit,
    onPhotoClick: (OldPhoto) -> Unit,
    onClosePhoto: () -> Unit,
    onDeletePhoto: (OldPhoto, (Boolean) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val defaultProfile = R.drawable.profile

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(innerPadding),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header card
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface,
                    contentColor = OnDark
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarRes = state.profileImageRes ?: defaultProfile
                    Image(
                        painter = rememberAsyncImagePainter(model = avatarRes),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Surface2)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Accent, AccentAlt),
                                    start = Offset.Zero,
                                    end = Offset(80f, 80f)
                                ),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            state.userName,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnDark
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            state.userInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnDark.copy(alpha = 0.7f)
                        )
                    }
                    Row {
                        IconButton(onClick = onEditProfile) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit),
                                tint = Accent
                            )
                        }
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings),
                                tint = AccentAlt
                            )
                        }
                    }
                }
            }
        }

        // Empty state
        if (state.photos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No photos yet",
                        color = OnDark.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Grid photos
            items(state.photos, key = { it.id }) { post ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface2)
                        .clickable { onPhotoClick(post) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = post.uri),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(64.dp)) }
        }
    }

    // Fullscreen
    state.selectedPhoto?.let { selected ->
        FullscreenImage(
            photo = selected,
            onClose = onClosePhoto,
            onDelete = { photo ->
                onDeletePhoto(photo) { success ->
                    Toast.makeText(
                        context,
                        context.getString(
                            if (success) R.string.delete_toast else R.string.delete_toast_failed
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@Composable
private fun FullscreenImage(
    photo: OldPhoto,
    onClose: () -> Unit,
    onDelete: (OldPhoto) -> Unit,
) {
    val context = LocalContext.current

    // UI state
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Permission explainer state
    var askWritePerm by remember { mutableStateOf(false) } // API <= 28
    var askReadPerm by remember { mutableStateOf(false) }  // API 33+
    var pendingActionAfterPerm by remember { mutableStateOf<(() -> Unit)?>(null) }

    val needsLegacyWritePerm = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

    val requestWritePerm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pendingActionAfterPerm?.invoke()
        else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        pendingActionAfterPerm = null
    }

    val requestReadImagesPerm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pendingActionAfterPerm?.invoke()
        else Toast.makeText(context, "Photos permission denied", Toast.LENGTH_SHORT).show()
        pendingActionAfterPerm = null
    }

    fun saveToGallery() {
        if (needsLegacyWritePerm && !context.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            askWritePerm = true
            pendingActionAfterPerm = { doSave(context, photo) }
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            photo.uri.scheme == "content" &&
            !context.hasPermission(Manifest.permission.READ_MEDIA_IMAGES)
        ) {
            askReadPerm = true
            pendingActionAfterPerm = { doSave(context, photo) }
            return
        }
        doSave(context, photo)
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg.copy(alpha = 0.98f))
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = photo.uri),
                contentDescription = stringResource(R.string.cd_full_photo),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClose() }
            )

            // SOL ÜST: ⋮ (Dropdown)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = Surface.copy(alpha = 0.6f), shape = CircleShape)
                ) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "More", tint = OnDark)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    properties = PopupProperties(focusable = true, clippingEnabled = false),
                    offset = DpOffset(0.dp, 4.dp)
                ) {
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Outlined.Download, null, tint = AccentAlt) },
                        text = { Text("Save to Gallery", color = Color.Black) },
                        onClick = {
                            menuExpanded = false
                            saveToGallery()
                        }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Outlined.Share, null, tint = Bg) },
                        text = { Text("Share", color = Bg) },
                        onClick = {
                            menuExpanded = false
                            sharePhoto(context, photo)
                        }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) },
                        text = { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            showDeleteConfirm = true
                        }
                    )
                }
            }


            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
                    .size(36.dp)
                    .background(color = Surface.copy(alpha = 0.6f), shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close), tint = OnDark)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteConfirm = false },
            icon = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.delete_confirm_title), color = OnDark) },
            text = { Text(stringResource(R.string.delete_confirm_text), color = OnDark.copy(alpha = 0.9f)) },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true
                        showDeleteConfirm = false
                        onDelete(photo)
                    }
                ) { Text(stringResource(R.string.yes_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(enabled = !isDeleting, onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.no_keep), color = OnDark)
                }
            },
            containerColor = Surface2,
            titleContentColor = OnDark,
            textContentColor = OnDark.copy(alpha = .9f)
        )
    }

    if (askWritePerm) {
        PermissionExplainerDialog(
            title = "Allow storage access?",
            message = "We need storage permission to save this image to your Gallery on older Android versions.",
            confirmText = "Continue",
            onConfirm = {
                askWritePerm = false
                requestWritePerm.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            },
            onDismiss = { askWritePerm = false }
        )
    }
    if (askReadPerm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionExplainerDialog(
            title = "Allow photos access?",
            message = "We need access to your photos to read and copy this image into your Gallery.",
            confirmText = "Continue",
            onConfirm = {
                askReadPerm = false
                requestReadImagesPerm.launch(Manifest.permission.READ_MEDIA_IMAGES)
            },
            onDismiss = { askReadPerm = false }
        )
    }
}

/* -- helpers -- */

private fun doSave(context: android.content.Context, photo: OldPhoto) {
    val res = context.saveUriToGallery(
        source = photo.uri,
        displayName = "AIPhoto_${photo.id ?: System.currentTimeMillis()}"
    )
    if (res.isSuccess) {
        Toast.makeText(context, "Saved to Gallery ✅", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Save failed ❌", Toast.LENGTH_SHORT).show()
    }
}

private fun android.content.Context.hasPermission(p: String) =
    ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED

@Composable
private fun PermissionExplainerDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = OnDark) },
        text  = { Text(message, color = OnDark.copy(alpha = 0.9f)) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText, color = AccentAlt) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Not now", color = OnDark) } },
        containerColor = Surface,
        titleContentColor = OnDark,
        textContentColor = OnDark
    )
}

private fun sharePhoto(context: android.content.Context, photo: OldPhoto) {
    val shareUri = getShareableUri(context, photo.uri)
    val mime = context.contentResolver.getType(shareUri) ?: "image/*"

    val share = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, shareUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(share, ""))
}

private fun getShareableUri(context: android.content.Context, src: Uri): Uri {
    return when (src.scheme) {
        "content" -> {
            src
        }
        "file" -> {
            val file = File(src.path ?: return src)
            try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (_: IllegalArgumentException) {
                copyToCacheAndGetUri(context, src)
            }
        }
        else -> {
            copyToCacheAndGetUri(context, src)
        }
    }
}

private fun copyToCacheAndGetUri(context: android.content.Context, src: Uri): Uri {
    val cacheDir = File(context.cacheDir, "share").apply { mkdirs() }
    val outFile = File(cacheDir, "shared_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(src).use { input ->
        FileOutputStream(outFile).use { output ->
            if (input != null) input.copyTo(output)
        }
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        outFile
    )
}