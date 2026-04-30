package com.vestel.aysuyakut.aiphotostudio.presentation.ui.util.ProfileScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vestel.aysuyakut.aiphotostudio.R
import com.vestel.aysuyakut.aiphotostudio.presentation.ui.LoginScreen.AuthViewModel

// 🎨 Palet (başka yerde varsa burayı sil)
private val Surface1 = Color(0xFF1A1A1F)
private val Accent1 = Color(0xFF6E56D7)
private val AccentAlt1 = Color(0xFF21D4FD)
private val OnDark1 = Color(0xFFECECEC)

@Composable
fun ProfileDrawerContent(
    onSettings: () -> Unit,
    onAccount: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onNotification: () -> Unit = {},
    onLogout: () -> Unit
) {
    val itemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = Accent1.copy(alpha = 0.15f),
        unselectedContainerColor = Color.Transparent,
        selectedTextColor = OnDark1,
        unselectedTextColor = OnDark1.copy(alpha = 0.85f),
        selectedIconColor = Accent1,
        unselectedIconColor = OnDark1.copy(alpha = 0.7f)
    )

    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = Surface1,
        drawerContentColor = OnDark1,
        drawerTonalElevation = 0.dp,
        modifier = Modifier.width(170.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.settings)) },
            selected = false,
            onClick = onSettings,
            colors = itemColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.account)) },
            selected = false,
            onClick = onAccount,
            colors = itemColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.privacy)) },
            selected = false,
            onClick = onPrivacy,
            colors = itemColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )


        NavigationDrawerItem(
            label = { Text(stringResource(R.string.notification)) },
            selected = false,
            onClick = onNotification,
            colors = itemColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick =  onLogout,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.logout),
                color = AccentAlt1
            )
        }
    }
}
