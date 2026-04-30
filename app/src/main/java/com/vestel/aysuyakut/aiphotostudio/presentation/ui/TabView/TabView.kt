package com.vestel.aysuyakut.aiphotostudio.presentation.ui.TabView

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.AIScreen
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ProfileScreen.ProfileRoute
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp


val Bg = Color(0xFF0F0F12)
val Surface = Color(0xFF1A1A1F)
val Surface2 = Color(0xFF141417)
val Accent = Color(0xFF6E56D7)
val AccentAlt = Color(0xFF21D4FD)
val OnDark = Color(0xFFECECEC)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(  onLoggedOut: () -> Unit,   isGuest: Boolean, onBackToLogin: () -> Unit) {
    val tabs = listOf(
        TabItem(R.string.tab_ai, Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome),
        TabItem(R.string.tab_profile, Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle),
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val AI_INDEX = 0
    val PROFILE_INDEX = 1
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }

    BackHandler {
        when (pagerState.currentPage) {
            PROFILE_INDEX -> scope.launch { pagerState.animateScrollToPage(AI_INDEX) }
            AI_INDEX -> {
                if (isGuest) {
                    onBackToLogin()
                } else {
                    showLogoutDialog = true
                }
            }
        }
    }

    if (showLogoutDialog) {
        var loggingOut by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign out?", color = OnDark) },
            text  = { Text("You’re about to sign out of your account. Are you sure?", color = OnDark.copy(alpha = 0.75f)) },

            // 🎨 palette uyumu
            shape = RoundedCornerShape(16.dp),
            containerColor = Surface,

            confirmButton = {
                Button(
                    onClick = {
                        if (!loggingOut) {
                            loggingOut = true
                            showLogoutDialog = false
                            auth.signOut()
                            // googleSignInClient?.signOut()?.addOnCompleteListener { onLoggedOut() }
                            onLoggedOut()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,          // ana marka moru
                        contentColor   = OnDark,          // açık metin
                        disabledContainerColor = Accent.copy(alpha = 0.6f),
                        disabledContentColor   = OnDark.copy(alpha = 0.9f)
                    )
                ) {
                    Text("Yes, sign out")
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Surface2),  // koyu gri hat
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Bg,                 // çok hafif arka plan
                        contentColor   = OnDark.copy(alpha = 0.9f)
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Scaffold(
        containerColor = Bg,
        bottomBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Surface,
                contentColor = Accent,
                divider = {}
            ) {
                tabs.forEachIndexed { index, item ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = { scope.launch { pagerState.scrollToPage(index) } },
                        text = {
                            Text(
                                stringResource(item.title),
                                color = if (selected) OnDark else OnDark.copy(alpha = 0.6f)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selected else item.unselected,
                                contentDescription = stringResource(item.title),
                                tint = if (selected) Accent else OnDark.copy(alpha = 0.7f)
                            )
                        },
                        selectedContentColor = Accent,
                        unselectedContentColor = OnDark.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { inner ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            userScrollEnabled = false
        ) { page ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Surface2
            ) {
                when (page) {
                    0 -> AIScreen()
                    1 ->  ProfileRoute(
                        onLoggedOut = onLoggedOut
                    )
                }
            }
        }
    }
}

private data class TabItem(
    @StringRes val title: Int,
    val unselected: ImageVector,
    val selected: ImageVector,
)
