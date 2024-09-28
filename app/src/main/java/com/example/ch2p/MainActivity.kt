package com.example.ch2p

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ch2p.ui.theme.CH2PTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

enum class WeeklyScreens {
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

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            CH2PTheme {
                MainScreenContent()
            }
        }
    }

    @Composable
    fun MainScreenContent() {
        val auth = Firebase.auth
        val isLoggedIn = remember { mutableStateOf(auth.currentUser) }
        var showSignOutDialog by remember { mutableStateOf(false) }

        if (isLoggedIn.value != null) {
            AppContent {
                showSignOutDialog = true
            }
        } else {
            LoginScreen {
                isLoggedIn.value = it
            }
        }

        if (showSignOutDialog) {
            SignOutConfirmationDialog(
                onConfirm = {
                    // Sign out user
                    auth.signOut()
                    isLoggedIn.value = auth.currentUser
                    showSignOutDialog = false
                },
                onCancel = { showSignOutDialog = false }
            )
        }
    }
}

@Composable
fun SignOutConfirmationDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Sign Out") },
        text = { Text("Apakah Anda yakin ingin sign out?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Sign Out") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(onSignOut: () -> Unit) {
    val navController = rememberNavController()
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
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            currentRoute ?: WeeklyScreens.M2.name

            val context = LocalContext.current
            val googleSignInClient = remember { // Remember the client
                GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
            }
            val coroutineScope = rememberCoroutineScope()

            Column(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Firebase sign-out
                                Firebase.auth.signOut()

                                // Google Sign-In client sign-out
                                googleSignInClient.signOut().addOnCompleteListener {
                                    // Update UI or perform actions after sign-out
                                    onSignOut()
                                }
                            } catch (e: Exception) {
                                Log.e("SignOut", "Error signing out: ", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp) // Add padding
                ) {
                    Text("Sign Out")
                }
                BottomAppBar(actions = {
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(WeeklyScreens.entries) { item ->
                            TextButton(
                                onClick = {
                                    try {
                                        navController.navigate(item.name) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } catch (e: Exception) {
                                        navController.navigate(WeeklyScreens.M2.name) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (currentRoute == item.name) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Gray
                                    }
                                )
                            ) {
                                Text("$item", fontSize = 20.sp)
                            }
                        }
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val context = LocalContext.current
        NavHost(
            navController = navController,
            startDestination = WeeklyScreens.M5.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = WeeklyScreens.M2.name) {
                M2Screen()
            }
            composable(route = WeeklyScreens.M3.name) {
                M3Screen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            composable(route = WeeklyScreens.M4.name) {
                M4Screen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            composable(route = WeeklyScreens.M5.name) {
                M5Screen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onLaunchListActivity = {
                        // Lambda to launch ListActivity
                        val intent = Intent(context, ListActivity::class.java)
                        context.startActivity(intent)
                    })
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (FirebaseUser?) -> Unit) {
    val context = LocalContext.current
    val showError by rememberSaveable { mutableStateOf(false) }
    rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleSignInResult(result, onLoginSuccess)
    }

    // Google Sign-In Options (GSO)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    // Google Sign-In Client
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Sign in with Google")
        }
        if (showError) {
            Text("Login gagal!", color = Color.Red)
        }
    }
}

private fun handleSignInResult(result: ActivityResult, onLoginSuccess: (FirebaseUser?) -> Unit) {
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("LoginScreen", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!, onLoginSuccess)
        } catch (e: ApiException) {
            Log.w("LoginScreen", "Google sign in failed", e)
        }
    }
}

private fun firebaseAuthWithGoogle(idToken: String, onComplete: (FirebaseUser?) -> Unit) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    Firebase.auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success
                Log.d("LoginScreen", "signInWithCredential:success")
                onComplete(Firebase.auth.currentUser) // Pass FirebaseUser
            } else {
                // If sign in fails
                Log.w("LoginScreen", "signInWithCredential:failure", task.exception)
                onComplete(null) // Pass null on failure
            }
        }
}

@Composable
fun M2Screen() {
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
        ManyFieldsElement()
    }
}

@Composable
fun M4Screen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        NamaMhs("Hugo Alfedo Putra", "225150201111013")
        Text("Latihan UI State", fontSize = 16.sp)
        ManyStatefulFieldsElement()
    }
}

@Composable
fun M5Screen(modifier: Modifier = Modifier, onLaunchListActivity: () -> Unit) {
    // Example Button to launch ListActivity (if needed)
    Column(
        modifier = modifier // Optional: Make the Row/Column fill the width
    ) {
        Button(
            onClick = onLaunchListActivity,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buka ListActivity")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManyStatefulFieldsElement() {
    var credName by remember { mutableStateOf("") }
    var credId by remember { mutableStateOf("") }
    var storageName by remember { mutableStateOf("") }
    var storageId by remember { mutableStateOf("") }
    var isNameValid by remember { mutableStateOf(false) }
    var isIdValid by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val nameRegex = Regex("[a-zA-Z\\s]+")
    val idRegex = Regex("[0-9]+")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            value = storageName,
            onValueChange = {
                if (it.length <= 50) {
                    storageName = it
                }
                isNameValid = it.length > 3 && it.matches(nameRegex)
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
                isIdValid = it.length >= 15 && it.matches(idRegex)
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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        Toast
                            .makeText(
                                context,
                                if (isNameValid && isIdValid) "$storageName ($storageId)" else "Silakan isi data diri Anda!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                ),
            content = {
                Box(
                    contentAlignment = Alignment.Center // Center content in Box
                ) {
                    Text(
                        text = "Preview",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(100),
            color = MaterialTheme.colorScheme.surfaceTint,
        )
        Button(
            onClick = {
                focusManager.clearFocus()
                credName = storageName
                credId = storageId
                storageName = ""
                storageId = ""
                isNameValid = false
                isIdValid = false
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = (isNameValid && isIdValid)
        ) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (credName.isNotEmpty() && credId.isNotEmpty()) {
            Column {
                Text("Selamat datang, ${credName}!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(text = "Identitasmu: $credId")
            }
        }
    }
}

@Composable
fun ManyFieldsElement() {
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
                Text(text = "Identitasmu: $credId")
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
fun UpdatableElement() {
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