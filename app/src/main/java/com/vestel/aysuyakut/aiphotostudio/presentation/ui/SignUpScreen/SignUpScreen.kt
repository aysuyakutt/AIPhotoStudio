package com.vestel.aysuyakut.aiphotostudio.presentation.ui.SignUpScreen

    import androidx.compose.animation.core.LinearEasing
    import androidx.compose.animation.core.RepeatMode
    import androidx.compose.animation.core.animateFloat
    import androidx.compose.animation.core.infiniteRepeatable
    import androidx.compose.animation.core.rememberInfiniteTransition
    import androidx.compose.animation.core.tween
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.outlined.HelpOutline
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.Shadow
    import androidx.compose.ui.text.input.ImeAction
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.input.PasswordVisualTransformation
    import androidx.compose.ui.text.input.VisualTransformation
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import android.util.Patterns
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.ui.graphics.toArgb
    import androidx.compose.ui.input.nestedscroll.nestedScroll
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import com.vestel.aysuyakut.aiphotostudio.R


    val Bg = Color(0xFF0F0F12)
    val Surface = Color(0xFF1A1A1F)
    val Surface2 = Color(0xFF141417)
    val Accent = Color(0xFF6E56D7)
    val AccentAlt = Color(0xFF21D4FD)
    val OnDark = Color(0xFFECECEC)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SignUpScreen(
        onBack: () -> Unit,
        onCreate: (String, String, String) -> Unit,
        onHelp: () -> Unit = {}
    ) {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        var pass2 by remember { mutableStateOf("") }
        var showPass by remember { mutableStateOf(false) }
        var showPass2 by remember { mutableStateOf(false) }
        var accept by remember { mutableStateOf(true) }

        val fullName = remember(firstName, lastName) {
            listOf(firstName.trim(), lastName.trim()).filter { it.isNotBlank() }.joinToString(" ")
        }

        val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        val passOk = pass.length >= 6
        val match = pass == pass2
        val nameOk = firstName.trim().length >= 2 && lastName.trim().length >= 2
        val canCreate = emailOk && passOk && match && accept && nameOk

        val scroll = rememberScrollState()
        val topColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF0F0F10),
            titleContentColor = Color(0xFFF5F5F5),
            actionIconContentColor = Color(0xFFCFCFCF)
        )
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(Bg.toArgb()))
                .systemBarsPadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = topColors,
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back", color = Color(0xFFCBCBCB)) }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.logo2),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(40.dp)         // ❗️AppBar'a uygun boyut
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text("AIPhotoStudio", fontSize = 20.sp, color = Color(0xFFF5F5F5))
                        }
                    },
                    actions = {
                        IconButton(onClick = onHelp) {
                            Icon(Icons.Outlined.HelpOutline, contentDescription = "Help")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize().background(Color(Bg.toArgb()))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)               // içerik ortalansın
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scroll)                // küçük ekran + klavye için güvenli
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GradientHeroTitle("Create Account")

                    OutlinedTextField(
                        value = firstName, onValueChange = { firstName = it },
                        label = { Text("First name") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = lastName, onValueChange = { lastName = it },
                        label = { Text("Last name") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pass, onValueChange = { pass = it },
                        label = { Text("Password (min 6)") }, singleLine = true,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Text(
                                if (showPass) "Hide" else "Show",
                                color = Color(0xFFBDBDBD),
                                modifier = Modifier.padding(end = 6.dp).clickable { showPass = !showPass }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pass2, onValueChange = { pass2 = it },
                        label = { Text("Confirm password") }, singleLine = true,
                        visualTransformation = if (showPass2) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Text(
                                if (showPass2) "Hide" else "Show",
                                color = Color(0xFFBDBDBD),
                                modifier = Modifier.padding(end = 6.dp).clickable { showPass2 = !showPass2 }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = accept,
                            onCheckedChange = { accept = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF7C4DFF),
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("I accept the Terms & Privacy", color = Color(0xFFBDBDBD))
                    }

                    Spacer(Modifier.height(6.dp))
                    if (!nameOk && (firstName.isNotBlank() || lastName.isNotBlank()))
                        Text("Please enter your first and last name.", color = Color(0xFFFF8A80))
                    if (email.isNotBlank() && !emailOk) Text("Geçerli bir email gir.", color = Color(0xFFFF8A80))
                    if (pass.isNotBlank() && !passOk) Text("Şifre en az 6 karakter olmalı.", color = Color(0xFFFF8A80))
                    if (pass2.isNotBlank() && !match) Text("Şifreler eşleşmiyor.", color = Color(0xFFFF8A80))

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = { onCreate(fullName, email.trim(), pass) },
                        enabled = canCreate,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C4DFF),
                            disabledContainerColor = Color(0xFF7C4DFF).copy(alpha = 0.4f)
                        )
                    ) { Text("Create Account", fontSize = 18.sp, color = Color.White) }

                    Spacer(Modifier.height(12.dp))

                    TextButton(onClick = onBack) {
                        Text("Already have an account? Log In", color = Color(0xFFCBCBCB))
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }



    @Composable
    private fun GradientHeroTitle(text: String) {
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
            text = text,
            fontSize = 42.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            textAlign = TextAlign.Center,
            color = Color.White, // fallback
            style = LocalTextStyle.current.copy(
                brush = gradient,
                shadow = Shadow(
                    color = Color(0xFF7C4DFF).copy(alpha = 0.55f),
                    blurRadius = 18f
                )
            )
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun fieldColors() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF191919),
        unfocusedContainerColor = Color(0xFF191919),
        focusedBorderColor = Color(0xFF2F2F2F),
        unfocusedBorderColor = Color(0xFF2A2A2A),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = Color(0xFFBDBDBD),
        unfocusedLabelColor = Color(0xFF9E9E9E),
        cursorColor = Color.White
    )
