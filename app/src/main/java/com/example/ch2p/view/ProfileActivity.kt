package com.example.ch2p.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ch2p.R
import com.example.ch2p.ui.theme.CH2PTheme
import com.example.ch2p.view.model.ProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CH2PTheme {
                M6Screen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M6Screen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Profilku")
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { (context as Activity).finish() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
//            ProfileCard()
        }
    }
}

const val uname: String = "rayhanegar"

@Composable
fun ProfileCard(modifier: Modifier = Modifier, onSignOut: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel()
    val profileInfo by viewModel.userDetail.collectAsState()
    val context = LocalContext.current

    val googleSignInClient = remember { // Remember the client
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getUserDetail(uname)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profileInfo?.avatarUrl,
                contentDescription = "${profileInfo?.name} profile picture",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            profileInfo?.name?.let { Text(it, fontSize = 24.sp, fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(8.dp))
            profileInfo?.login?.let {
                Button(onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/${it}")
                        )
                    )
                }) { Text(it) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
        ) {
            profileInfo?.bio?.let { Text(it) }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Following", fontWeight = FontWeight.Bold)
                    profileInfo?.following?.let { Text(it.toString()) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Followers", fontWeight = FontWeight.Bold)
                    profileInfo?.followers?.let { Text(it.toString()) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Repositori terbaru", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            RepoCard(modifier)
        }

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
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Add padding
        ) {
            Text("Sign Out dari Aplikasi")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepoCard(modifier: Modifier = Modifier) {
    val viewModel: ProfileViewModel = viewModel()
    val userRepoInfo by viewModel.repoDetail.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getUserRepoDetail(uname)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Iterate directly through userRepoInfo (no need for null check)
        userRepoInfo.forEach { repoItem ->
            repoItem?.let { repo -> // Null check for individual repoItem
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RectangleShape,
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(repo.htmlUrl)
                                    )
                                )
                            }) {
                            Text(repo.name ?: "") // Default value for repo.name
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(100)) {
                                Text(
                                    repo.visibility,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            if (repo.language?.isNotEmpty() == true) {
                                Surface(shape = RoundedCornerShape(100)) {
                                    Text(
                                        repo.language,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                            Surface(shape = RoundedCornerShape(100)) {
                                Text(
                                    repo.defaultBranch,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Surface(shape = RoundedCornerShape(100)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier.padding(start = 8.dp),
                                        painter = painterResource(id = R.drawable.eye),
                                        contentDescription = "Watcher count"
                                    )
                                    Text(
                                        repo.watchersCount.toString(),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}