package com.example.ch2p

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M5Screen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Jadwal Kuliah")
                },
                actions = {
                    Button(onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                ProfileActivity::class.java
                            )
                        )
                    }) {
                        Icon(Icons.Outlined.AccountCircle, "My profile")
                    }
                }
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
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScheduleCard()
//            Button(onClick = {
//                setFirestoreData()
//            }) { Text("Isikan data ke Firestore") }
        }
    }

}

@Composable
fun ScheduleCard(modifier: Modifier = Modifier) {
    val viewModel: ListViewModel = viewModel()
    val schedule by viewModel.schedule.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(schedule) { matkul ->
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primaryContainer) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Column for hari, jamMulai, and jamSelesai
                    Column(modifier = Modifier.weight(0.3f)) {
                        Text(matkul.hari)
                        Text(matkul.jamMulai)
                        Text(matkul.jamSelesai)
                    }

                    // Column for kelas, nama, and praktikum
                    Column(modifier = Modifier.weight(1f)) {
                        Text(matkul.kelas)
                        Text(matkul.nama)
                        Text(if (matkul.praktikum) "Tersedia praktikum" else "Tidak ada praktikum")
                    }
                }
            }
        }
    }
}

//fun setFirestoreData(){
//    val db = Firebase.firestore
//    val matkul = db.collection("matkul")
//
//    // Create a new user with a first and last name
//    val sch3 = hashMapOf(
//        "hari" to "Selasa",
//        "jam_mulai" to "12:50",
//        "jam_selesai" to "14:29",
//        "kelas" to "F2.4",
//        "nama" to "Rekayasa Perangkat Lunak",
//        "praktikum" to false
//    )
//    matkul.add(sch3)
//
//    val sch4 = hashMapOf(
//        "hari" to "Selasa",
//        "jam_mulai" to "15:30",
//        "jam_selesai" to "17:09",
//        "kelas" to "F3.10",
//        "nama" to "Statistika Inferensi",
//        "praktikum" to false
//    )
//    matkul.add(sch4)
//
//    val sch5 = hashMapOf(
//        "hari" to "Rabu",
//        "jam_mulai" to "09:30",
//        "jam_selesai" to "11:59",
//        "kelas" to "F3.10",
//        "nama" to "Metodologi Penelitian dan Penulisan Ilmiah",
//        "praktikum" to false
//    )
//    matkul.add(sch5)
//
//    val sch6 = hashMapOf(
//        "hari" to "Rabu",
//        "jam_mulai" to "12:50",
//        "jam_selesai" to "14:29",
//        "kelas" to "F2.8",
//        "nama" to "Pengembangan Aplikasi Perangkat Bergerak",
//        "praktikum" to true
//    )
//    matkul.add(sch6)
//
//    val sch7 = hashMapOf(
//        "hari" to "Rabu",
//        "jam_mulai" to "14:30",
//        "jam_selesai" to "16:19",
//        "kelas" to "F4.10",
//        "nama" to "Jaringan Saraf Tiruan",
//        "praktikum" to true
//    )
//    matkul.add(sch7)
//
//    val sch8 = hashMapOf(
//        "hari" to "Kamis",
//        "jam_mulai" to "07:00",
//        "jam_selesai" to "08:39",
//        "kelas" to "F4.12",
//        "nama" to "Jaringan Nirkabel",
//        "praktikum" to false
//    )
//    matkul.add(sch8)
//
//    val sch9 = hashMapOf(
//        "hari" to "Kamis",
//        "jam_mulai" to "12:50",
//        "jam_selesai" to "14:29",
//        "kelas" to "G1.2",
//        "nama" to "Jaringan Saraf Tiruan",
//        "praktikum" to true
//    )
//    matkul.add(sch9)
//
//    val sch10 = hashMapOf(
//        "hari" to "Kamis",
//        "jam_mulai" to "15:30",
//        "jam_selesai" to "17:09",
//        "kelas" to "F3.8",
//        "nama" to "Rekayasa Perangkat Lunak",
//        "praktikum" to false
//    )
//    matkul.add(sch10)
//
//    val sch11 = hashMapOf(
//        "hari" to "Jumat",
//        "jam_mulai" to "12:50",
//        "jam_selesai" to "13:39",
//        "kelas" to "F4.12",
//        "nama" to "Jaringan Nirkabel",
//        "praktikum" to false
//    )
//    matkul.add(sch11)
//}