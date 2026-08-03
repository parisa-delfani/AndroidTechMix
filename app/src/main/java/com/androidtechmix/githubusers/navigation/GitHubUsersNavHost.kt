package com.androidtechmix.githubusers.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.androidtechmix.githubusers.R
import com.androidtechmix.githubusers.feature.favorites.navigation.FavoritesDestination
import com.androidtechmix.githubusers.feature.favorites.ui.FavoritesRoute
import com.androidtechmix.githubusers.feature.search.navigation.SearchDestination
import com.androidtechmix.githubusers.feature.search.ui.SearchRoute
import com.androidtechmix.githubusers.feature.userdetail.navigation.UserDetailDestination
import com.androidtechmix.githubusers.feature.userdetail.ui.UserDetailRoute
import kotlin.reflect.KClass

private data class TopLevelDestination(
    val route: KClass<*>,
    val labelRes: Int,
    val icon: ImageVector,
    val navigateTarget: Any,
)

@Composable
fun GitHubUsersNavHost() {
    val navController = rememberNavController()
    val destinations = listOf(
        TopLevelDestination(
            route = SearchDestination::class,
            labelRes = R.string.nav_search,
            icon = Icons.Filled.Search,
            navigateTarget = SearchDestination,
        ),
        TopLevelDestination(
            route = FavoritesDestination::class,
            labelRes = R.string.nav_favorites,
            icon = Icons.Filled.Favorite,
            navigateTarget = FavoritesDestination,
        ),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = destinations.any { dest ->
        currentDestination?.hierarchy?.any { it.hasRoute(dest.route) } == true
    }

    // Zero content insets here so screens' own Scaffolds/TopAppBars own the status-bar inset.
    // Applying safeDrawing on both outer + inner Scaffolds doubles the top gap.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    destinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(destination.route)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.navigateTarget) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(text = stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SearchDestination,
            modifier = Modifier.padding(padding),
        ) {
            composable<SearchDestination> {
                SearchRoute(
                    onOpenUser = { login ->
                        navController.navigate(UserDetailDestination(login = login))
                    },
                )
            }
            composable<FavoritesDestination> {
                FavoritesRoute(
                    onOpenUser = { login ->
                        navController.navigate(UserDetailDestination(login = login))
                    },
                    onGoToSearch = {
                        navController.navigate(SearchDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable<UserDetailDestination> {
                UserDetailRoute(onBack = { navController.popBackStack() })
            }
        }
    }
}
