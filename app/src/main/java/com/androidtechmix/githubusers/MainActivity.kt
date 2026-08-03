package com.androidtechmix.githubusers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.androidtechmix.githubusers.navigation.GitHubUsersNavHost
import com.androidtechmix.githubusers.core.ui.theme.AndroidTechMixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTechMixTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GitHubUsersNavHost()
                }
            }
        }
    }
}
