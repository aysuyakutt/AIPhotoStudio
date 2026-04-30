@file:Suppress("UnusedImport")

package com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.SystemBarsPerScreen
import com.vestel.aysuyakut.aiphotostudio.presentation.navigation.Routes
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.AccentAlt
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Bg
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.OnDark
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.TabView.Surface2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUp: () -> Unit,
    onGoogleLogin: () -> Unit,
    onGuestLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onHelp: () -> Unit = {},
    isLoading: Boolean = false
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    SystemBarsPerScreen(
        statusBar = Bg,
        navBar = Bg,
        fitsSystemWindows = true
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F10))
            .systemBarsPadding()
            .imePadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 4.dp, end = 24.dp)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo2),
                    contentDescription = null,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(end = 5.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    "AIPhotoStudio",
                    fontSize = 18.sp,
                    color = Color(0xFFF5F5F5)
                )
            }
            IconButton(onClick = onHelp) {
                Icon(
                    Icons.Outlined.HelpOutline,
                    contentDescription = "Help",
                    tint = Color(0xFFCFCFCF)
                )
            }
        }

        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FancyHeroTitle()

            // EMAIL — görünümü kaybetmeden kilitle
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF191919),
                    unfocusedContainerColor = Color(0xFF191919),
                    focusedBorderColor = Color(0xFF2F2F2F),
                    unfocusedBorderColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFFBDBDBD),
                    unfocusedLabelColor = Color(0xFF9E9E9E),
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") }, singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        if (showPassword) "Hide" else "Show",
                        color = Color(0xFFBDBDBD),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable(enabled = !isLoading) { showPassword = !showPassword }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF191919),
                    unfocusedContainerColor = Color(0xFF191919),
                    focusedBorderColor = Color(0xFF2F2F2F),
                    unfocusedBorderColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFFBDBDBD),
                    unfocusedLabelColor = Color(0xFF9E9E9E),
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onForgotPassword,   modifier = Modifier.align(Alignment.Start),    contentPadding = PaddingValues(0.dp)) {
                Text("Forgot password?", color = OnDark)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    if (!isLoading) {
                        focusManager.clearFocus()
                        onLogin(email.trim(), password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6C63FF).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Signing in...", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onSignUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(18.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1A1A1B))
                    .clickable(
                        enabled = !isLoading,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { onGoogleLogin() }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("G", color = Color(0xFF202124), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text("Continue with Google", color = Color(0xFFF5F5F5), fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onGuestLogin,
                enabled = !isLoading
            ) {
                Text("Continue as Guest", color = Color(0xFFCBCBCB), fontSize = 16.sp)
            }
        }

        if (isLoading) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.04f))
            )
        }
    }
}

@Composable
fun FancyHeroTitle() {
    val infinite = rememberInfiniteTransition(label = "shine")
    val offset by infinite.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shineAnim"
    )

    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF), Color(0xFF7C4DFF)),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 0f)
    )

    Text(
        text = "Login or Sign Up",
        fontSize = 38.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        textAlign = TextAlign.Center,
        style = LocalTextStyle.current.copy(
            brush = gradient,
            shadow = Shadow(
                color = Color(0xFF7C4DFF).copy(alpha = 0.55f),
                blurRadius = 18f
            )
        )
    )
}

@Composable
private fun GradientText(
    text: String,
    fontSize: TextUnit,
    weight: FontWeight,
    animatedShift: Float
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF7C4DFF),
            Color(0xFF00E5FF),
            Color(0xFF7C4DFF)
        ),
        start = Offset(animatedShift, 0f),
        end = Offset(animatedShift + 400f, 0f)
    )

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = weight,
        letterSpacing = 0.5.sp,
        style = LocalTextStyle.current.copy(
            brush = gradient,
            shadow = Shadow(
                color = Color(0xFF7C4DFF).copy(alpha = 0.55f),
                offset = Offset(0f, 0f),
                blurRadius = 18f
            )
        )
    )
}

@Composable
fun LoginRoute(
    onSuccess: () -> Unit,
    navController: NavController,
    onSignUp: () -> Unit,
    onHelp: () -> Unit = {},
    onGuest: () -> Unit
) {
    val vm: AuthViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }
    var lastAttemptWasGuest by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(Unit) {
        vm.events.collectLatest { ev ->
            when (ev) {
                is AuthEvent.Success -> {
                    isLoading = false
                    if (lastAttemptWasGuest) onGuest() else onSuccess()
                    lastAttemptWasGuest = false
                }
                is AuthEvent.Error -> {
                    isLoading = false
                    snackbarHostState.showSnackbar(ev.msg.ifBlank { "Giriş başarısız" })
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.padding(padding)) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            LoginScreen(
                onLogin = { email, pass ->
                    isLoading = true
                    lastAttemptWasGuest = false
                    vm.login(email, pass)
                },
                onSignUp = onSignUp,
                onGoogleLogin = {
                    if (!isLoading) {
                        activity?.let {
                            isLoading = true
                            lastAttemptWasGuest = false
                            vm.loginWithGoogle(it)
                        } ?: scope.launch {
                            snackbarHostState.showSnackbar("Cannot start Google sign-in on this screen.")
                        }
                    }
                },
                onGuestLogin = {
                    isLoading = true
                    lastAttemptWasGuest = true
                    vm.continueAsGuest()
                },
                onForgotPassword = {  navController.navigate(Routes.Forgot) },
                onHelp = onHelp,
                isLoading = isLoading
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
