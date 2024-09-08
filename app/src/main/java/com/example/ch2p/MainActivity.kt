package com.example.ch2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch2p.ui.theme.CH2PTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CH2PTheme {
                Wrapper()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wrapper(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tugas M2")
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UpdatableElement()
        }
    }
}

@Composable
fun NamaMhs(name: String, nim:String, modifier: Modifier = Modifier){
    Column (modifier = Modifier) {
        Text(
            text = name,
            modifier = modifier
        )
        Text(
            text = nim,
            modifier = modifier
        )
    }
}

@Composable
fun UpdatableElement(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    var textDisplay by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(IntrinsicSize.Max)
    ){
        NamaMhs("Hugo Alfedo Putra", "225150201111013")
        Text("Tuliskan teks di kolom di bawah ini agar muncul di kolom paling bawah!")
        TextField(
            value = text,
            onValueChange = {text = it},
            label = {Text("Tulis sesuatu di sini!")},
            modifier = Modifier.focusRequester(focusRequester).fillMaxWidth()
        )
        Button(
            onClick = {
                val textValue = text
                textDisplay = textValue
                text = ""
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()) {
            Text("Isikan!")
        }
        TextField(value = textDisplay, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CH2PTheme {
        Greeting("Android")
    }
}