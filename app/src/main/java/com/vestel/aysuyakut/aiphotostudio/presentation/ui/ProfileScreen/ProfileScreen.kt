package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.vestel.aysuyakut.aiphotostudio.SystemBarsPerScreen
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Bg
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.TabView.Surface2
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.util.ProfileScreen.ProfileDrawerContent
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.util.ProfileScreen.ProfileContentGrid
import kotlinx.coroutines.launch

val Bg = Color(0xFF0F0F12)
val Surface = Color(0xFF1A1A1F)
val Surface2 = Color(0xFF141417)
val Accent = Color(0xFF6E56D7)
val AccentAlt = Color(0xFF21D4FD)
val OnDark = Color(0xFFECECEC)

@Composable
fun ProfileRoute(
    vm: ProfileViewModel = hiltViewModel(),
    onLoggedOut:()-> Unit
) {

    val state by vm.uiState.collectAsState()

    // 🔔 Logout event’ini dinle
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            if (ev is ProfileViewModel.Event.LoggedOut) {
                onLoggedOut()
            }
        }
    }
    ProfileScreen(
        state = state,
        onOpenDrawer = vm::onOpenDrawer,
        onCloseDrawer = vm::onCloseDrawer,
        onEditProfile = vm::onEditProfile,
        onSettings = vm::onSettings,
        onLogout = vm::onLogout,
        onPhotoClick = vm::onPhotoClicked,
        onClosePhoto = vm::onClosePhoto,
        onDeletePhoto = vm::onDeletePhoto
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit, // <- bunu kullanacağız
    onPhotoClick: (OldPhoto) -> Unit,
    onClosePhoto: () -> Unit,
    onDeletePhoto: (OldPhoto, (Boolean) -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Tek noktadan logout davranışı
    fun handleLogoutClick() {
        val isGuest = FirebaseAuth.getInstance().currentUser?.isAnonymous == true
        if (isGuest) {
            onLogout()              // guest: direkt çık
        } else {
            showLogoutConfirm = true // normal: dialog göster
        }
    }

    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) drawerState.open() else drawerState.close()
    }

    SystemBarsPerScreen(
        statusBar = Surface,
        navBar = Surface,
        fitsSystemWindows = true
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = OnDark.copy(alpha = 0.20f),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Surface,
                drawerContentColor = OnDark,
                drawerTonalElevation = 0.dp
            ) {
                ProfileDrawerContent(
                    onSettings = onSettings,
                    onAccount = { /* TODO */ },
                    onPrivacy = { /* TODO */ },
                    onNotification = { /* TODO */ },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        handleLogoutClick() // <- burada vm değil, handler
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Bg,
            contentColor = OnDark,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Profile",
                            fontWeight = FontWeight.SemiBold,
                            color = OnDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Open settings",
                                tint = Accent
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Surface,
                        titleContentColor = OnDark,
                        navigationIconContentColor = Accent,
                        actionIconContentColor = AccentAlt
                    )
                )
            }
        ) { innerPadding ->
            Surface(
                color = Surface2,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                ProfileContentGrid(
                    state = state,
                    innerPadding = PaddingValues(0.dp),
                    onEditProfile = onEditProfile,
                    onOpenDrawer = onOpenDrawer,
                    onPhotoClick = onPhotoClick,
                    onClosePhoto = onClosePhoto,
                    onDeletePhoto = onDeletePhoto
                )
            }
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Closed && state.isDrawerOpen) {
            onCloseDrawer()
        }
    }

    // Sadece normal kullanıcıda açılacak (guest'te hiç true yapılmıyor)
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Sign out?", color = OnDark) },
            text  = {
                Text(
                    "You're about to sign out. Are you sure?",
                    color = OnDark.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()  // gerçekten çıkış
                    }
                ) { Text("Sign out", color = AccentAlt) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", color = OnDark)
                }
            },
            containerColor = Surface,
            titleContentColor = OnDark,
            textContentColor = OnDark
        )
    }
}
