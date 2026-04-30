package com.vestel.aysuyakut.aiphotostudio
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.ColorUtils
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.vestel.aysuyakut.aiphotostudio.presentation.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Teman yoksa burayı kaldırıp direkt AppNavHost() çağır
            MaterialTheme {

                AppNavHost()
            }
        }
    }
    val Bg = Color(0xFF0F0F12)


}
@Composable
fun SystemBarsPerScreen(
    statusBar: Color,
    navBar: Color = statusBar,
    fitsSystemWindows: Boolean = true  // true: içerik barların ALTINA girmez (kayma olmaz)
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Layout davranışı (kenarlara yayma/yaymama)
            WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)

            // Renkleri bas
            window.statusBarColor = statusBar.toArgb()
            window.navigationBarColor = navBar.toArgb()

            // İkon rengini arka plana göre ayarla (açık zemin -> koyu ikon, koyu zemin -> açık ikon)
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            val statusIsLight = ColorUtils.calculateLuminance(statusBar.toArgb()) > 0.5
            val navIsLight = ColorUtils.calculateLuminance(navBar.toArgb()) > 0.5
            controller.isAppearanceLightStatusBars = statusIsLight
            controller.isAppearanceLightNavigationBars = navIsLight
        }
    }
}