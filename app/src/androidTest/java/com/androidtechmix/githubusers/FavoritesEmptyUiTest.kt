package com.androidtechmix.githubusers

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidtechmix.githubusers.core.ui.theme.AndroidTechMixTheme
import com.androidtechmix.githubusers.feature.favorites.ui.FavoritesScreen
import com.androidtechmix.githubusers.feature.favorites.ui.state.FavoritesUiState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesEmptyUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyFavorites_showsEmptyState() {
        composeRule.setContent {
            AndroidTechMixTheme(dynamicColor = false) {
                FavoritesScreen(
                    uiState = FavoritesUiState(favorites = emptyList(), isLoading = false),
                    snackbarHostState = remember { SnackbarHostState() },
                    onEvent = {},
                    onGoToSearch = {},
                )
            }
        }

        composeRule.onNodeWithText("No favorites yet").assertIsDisplayed()
        composeRule.onNodeWithText("Search users").assertIsDisplayed()
    }
}
