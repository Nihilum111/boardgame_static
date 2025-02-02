package com.example.boardgamestatistic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.boardgamestatistic.ui.NavigationComponent
import com.example.boardgamestatistic.ui.theme.BoardGameStatisticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGameStatisticTheme {
                NavigationComponent()
            }
        }
    }
}
