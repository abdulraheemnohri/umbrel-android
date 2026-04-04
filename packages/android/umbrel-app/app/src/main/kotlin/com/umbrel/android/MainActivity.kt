package com.umbrel.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umbrel.android.runtime.RuntimeManager
import com.umbrel.android.runtime.ResourceTracker
import com.umbrel.android.runtime.LogManager
import com.umbrel.android.runtime.SambaManager
import com.umbrel.android.ui.apps.AppStoreScreen
import com.umbrel.android.ui.apps.StoreApp
import com.umbrel.android.ui.dashboard.DashboardScreen
import com.umbrel.android.ui.dashboard.UmbrelApp
import com.umbrel.android.ui.terminal.TerminalScreen
import com.umbrel.android.ui.files.FilesScreen
import com.umbrel.android.ui.theme.UmbrelTheme

class MainActivity : ComponentActivity() {
    private lateinit var runtimeManager: RuntimeManager
    private lateinit var resourceTracker: ResourceTracker
    private lateinit var logManager: LogManager
    private lateinit var sambaManager: SambaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runtimeManager = RuntimeManager(this)
        resourceTracker = ResourceTracker(this)
        logManager = LogManager()
        sambaManager = SambaManager(runtimeManager.linuxEnv, runtimeManager.lifecycleManager)

        resourceTracker.startTracking()

        setContent {
            UmbrelTheme {
                MainApp(resourceTracker, logManager, sambaManager)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(resourceTracker: ResourceTracker, logManager: LogManager, sambaManager: SambaManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Dashboard,
        Screen.Files,
        Screen.AppStore,
        Screen.Terminal
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    apps = listOf(
                        UmbrelApp("bitcoin", "Bitcoin Node", "Running"),
                        UmbrelApp("nextcloud", "Nextcloud", "Stopped")
                    ),
                    resourceTracker = resourceTracker
                )
            }
            composable(Screen.Files.route) {
                FilesScreen(sambaManager = sambaManager, phoneIp = "192.168.1.5")
            }
            composable(Screen.AppStore.route) {
                AppStoreScreen(
                    apps = listOf(
                        StoreApp("jellyfin", "Jellyfin", "The Free Software Media System"),
                        StoreApp("ipfs", "IPFS", "A peer-to-peer hypermedia protocol")
                    )
                )
            }
            composable(Screen.Terminal.route) {
                TerminalScreen(logManager = logManager)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Home")
    object Files : Screen("files", "Files")
    object AppStore : Screen("appstore", "Apps")
    object Terminal : Screen("terminal", "Terminal")
}
