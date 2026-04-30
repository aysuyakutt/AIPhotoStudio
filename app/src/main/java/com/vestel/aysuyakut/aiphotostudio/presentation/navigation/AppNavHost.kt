package com.vestel.aysuyakut.aiphotostudio.presentation.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.TabView.HomeScreen
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen.AuthEvent
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen.AuthViewModel
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.SplashScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.ForgotScreen.ForgotPasswordRoute
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen.LoginRoute
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.SignUpScreen.SignUpScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.Splash) {

        composable("splash") {
            SplashScreen(onDone = { loggedIn ->
                nav.navigate(if (loggedIn) "${Routes.Home}?guest=false" else Routes.Login) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            })
        }

        composable(Routes.Login) {
            LoginRoute(
                onSuccess = {
                    // Gerçek login → Login'i stack’ten sil
                    nav.navigate("${Routes.Home}?guest=false", navOptions {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    })
                },
                onGuest = {
                    nav.navigate("${Routes.Home}?guest=true")
                },
                onSignUp = { nav.navigate(Routes.SignUp) },
                onHelp = { /* ... */ },
                navController = nav
            )
        }

        composable(Routes.SignUp) {
            val vm: AuthViewModel = hiltViewModel()
            val snackbar = remember { SnackbarHostState() }
            val isLoading by vm.loading.collectAsState()

            LaunchedEffect(Unit) {
                vm.events.collectLatest { ev ->
                    when (ev) {
                        is AuthEvent.Success -> {

                            nav.navigate(Routes.Login) {
                                popUpTo(Routes.SignUp) { inclusive = true }
                                launchSingleTop = true
                            }

                        }
                        is AuthEvent.Error -> {
                            snackbar.showSnackbar(ev.msg)
                        }
                    }
                }
            }

            Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
                Box(Modifier.padding(padding)) {
                    SignUpScreen(
                        onBack  = { nav.popBackStack() },
                        onHelp  = { /* ... */ },
                        onCreate = { fullName, email, pass ->
                            vm.signUp(fullName, email, pass)
                        }
                    )

                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
        composable(
            route = "${Routes.Home}?guest={guest}",
            arguments = listOf(navArgument("guest"){ type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val isGuest = backStackEntry.arguments?.getBoolean("guest") ?: false

            HomeScreen(
                isGuest = isGuest,
                onLoggedOut = {

                    nav.navigate(Routes.Login) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Forgot) {
            ForgotPasswordRoute(onBack = { nav.popBackStack() })
        }
    }

}


fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

