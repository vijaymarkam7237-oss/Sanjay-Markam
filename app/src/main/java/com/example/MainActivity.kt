package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BattleArenaApp
import com.example.ui.theme.BattleArenaTheme
import com.example.ui.viewmodel.ArenaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ArenaViewModel = viewModel(
                factory = ArenaViewModel.Factory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            BattleArenaTheme(darkTheme = uiState.preferences.darkThemeEnabled) {
                BattleArenaApp(viewModel = viewModel)
            }
        }
    }
}
