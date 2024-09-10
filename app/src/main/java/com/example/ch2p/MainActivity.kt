package com.example.ch2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.ch2p.ui.theme.CH2PTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class WeeklyScreens() {
    M2,
    M3,
    M4,
    M5,
    M6,
    M7,
    M8,
    M9,
    M10,
    M11,
    M12,
}

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
fun Wrapper(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = WeeklyScreens.valueOf(
        backStackEntry?.destination?.route ?: WeeklyScreens.M2.name
    )

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text("Tugas $currentScreen")
                }
            )
        },
        bottomBar = {
            BottomAppBar(actions = {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(WeeklyScreens.entries) { item ->
                        TextButton(onClick = {
                            navController.navigate(item.name) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Text("$item", fontSize = 20.sp)
                        }
                    }
                }
            })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WeeklyScreens.M2.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = WeeklyScreens.M2.name) {
                M2Screen(modifier = Modifier.fillMaxWidth())
            }
            composable(route = WeeklyScreens.M3.name) {
                M3Screen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun M2Screen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UpdatableElement()
    }
}

@Composable
fun M3Screen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        NamaMhs("Hugo Alfedo Putra", "225150201111013")
        Text("Latihan TextField banyak", fontSize = 16.sp)
        ManyFieldsElement(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ManyFieldsElement(modifier: Modifier = Modifier) {
    var credName by remember { mutableStateOf("") }
    var credId by remember { mutableStateOf("") }
    var storageName by remember { mutableStateOf("") }
    var storageId by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            value = storageName,
            onValueChange = {
                if (it.length <= 50) {
                    storageName = it
                }
            },
            label = { Text("Nama Lengkap Mahasiswa") },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Your name"
                )
            }
        )
        TextField(
            value = storageId,
            onValueChange = {
                if (it.length <= 15) {
                    storageId = it
                }
            },
            label = { Text("Nomor Induk Mahasiswa") },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Your student ID"
                )
            }
        )
        Button(
            onClick = {
                credName = storageName
                credId = storageId
                storageName = ""
                storageId = ""
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (credName != "" && credId != "") {
            Row {
                Text("Selamat datang, ${credName}!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            Row {
                Text(text = "Identitasmu: ${credId}")
            }
        }
    }
}

@Composable
fun NamaMhs(name: String, nim: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier) {
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
    ) {
        NamaMhs("Hugo Alfedo Putra", "225150201111013")
        Text("Tuliskan teks di kolom di bawah ini agar muncul di kolom paling bawah!")
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tulis sesuatu di sini!") },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )
        Button(
            onClick = {
                val textValue = text
                textDisplay = textValue
                text = ""
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Isikan!")
        }
        TextField(
            value = textDisplay,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}