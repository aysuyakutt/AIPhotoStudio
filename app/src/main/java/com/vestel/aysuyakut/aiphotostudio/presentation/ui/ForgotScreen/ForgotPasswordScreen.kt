package com.vestel.aysuyakut.aiphotostudio.presentation.ui.ForgotScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Accent
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.AccentAlt
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Bg
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.OnDark
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.Surface2C
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.AIScreen.SurfaceC
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.SignUpScreen.Surface

@Composable
fun ForgotPasswordRoute(
    onBack: () -> Unit,
    vm: ForgotPasswordViewModel = hiltViewModel()
) {
    val ui = vm.ui
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(ui.message) {
        ui.message?.let { snackbar.showSnackbar(it) }
    }

    ForgotPasswordScreen(
        email = ui.email,
        isLoading = ui.isLoading,
        emailError = ui.emailError,
        sent = ui.sent,
        onBack = onBack,
        onEmailChange = vm::onEmailChange,
        onSubmit = vm::sendReset,
        snackbarHostState = snackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordScreen(
    email: String,
    isLoading: Boolean,
    emailError: String?,
    sent: Boolean,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val kb = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password", color = OnDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OnDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Bg
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Bg)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = Accent
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Enter your email and we'll send you a reset link.",
                    color = OnDark.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email", color = OnDark.copy(alpha = 0.8f)) },
                    singleLine = true,
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) Text(emailError, color = Color(0xFFFF6B6B))
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            kb?.hide()
                            onSubmit()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Surface2C,
                        focusedTextColor = OnDark,
                        unfocusedTextColor = OnDark,
                        cursorColor = Accent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceC, RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        kb?.hide()
                        onSubmit()
                    },
                    enabled = !isLoading && email.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = OnDark
                    )
                ) {
                    Text("Send reset link")
                }

                if (sent) {
                    Spacer(Modifier.height(20.dp))
                    // Bilgilendirme kutusu
                    Surface(
                        color = Surface2C,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Email sent!",
                                color = OnDark,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Check your inbox (and spam), then follow the link to set a new password.",
                                color = OnDark.copy(alpha = 0.85f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Row {
                                TextButton(onClick = { openEmailApp(context) }) {
                                    Text("Open email app", color = AccentAlt)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun openEmailApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Fallback
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
        context.startActivity(intent)
    }
}
