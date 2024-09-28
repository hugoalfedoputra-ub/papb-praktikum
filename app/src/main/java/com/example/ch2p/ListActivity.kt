package com.example.ch2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ch2p.ui.theme.CH2PTheme

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CH2PTheme {
                M5Screen()
            }
        }
    }
}

@Composable
fun M5Screen(modifier: Modifier = Modifier) {
    Text("test")
}