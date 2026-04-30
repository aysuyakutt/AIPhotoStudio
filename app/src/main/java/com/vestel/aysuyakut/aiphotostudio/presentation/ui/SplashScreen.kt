package com.vestel.aysuyakut.aiphotostudio.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.SystemBarsPerScreen
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Bg
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.SurfaceC
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDone: (Boolean) -> Unit,
    vm: AuthViewModel = hiltViewModel(),
    durationMs: Long = 4000L
) {
    LaunchedEffect(Unit) {
        val loginDeferred = async(Dispatchers.IO) { vm.isLoggedIn() }
        delay(durationMs)
        val loggedIn = if (loginDeferred.isCompleted) loginDeferred.getCompleted() else false
        onDone(loggedIn)
    }
    SystemBarsPerScreen(
        statusBar = Color.Black,
        navBar = Color.Black,
        fitsSystemWindows = true // kayma yok
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo2),
                contentDescription = null
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "AIPhotoStudio",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Unleash your creativity with AI-powered art",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}
