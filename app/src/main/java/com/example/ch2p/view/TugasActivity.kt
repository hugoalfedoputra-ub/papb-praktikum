package com.example.ch2p.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch2p.data.model.local.Tugas
import com.example.ch2p.ui.theme.CH2PTheme
import com.example.ch2p.view.model.TugasViewModel
import com.example.ch2p.view.model.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TugasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CH2PTheme {
                M8Screen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M8Screen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TugasViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Tugas-Tugasku")
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { (context as Activity).finish() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TugasForm(onTugasAdded = { newTugas ->
                viewModel.insertTugas(newTugas)
            })
            TugasCard()
        }
    }
}

@Composable
fun TugasScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TugasViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TugasForm(onTugasAdded = { newTugas ->
            viewModel.insertTugas(newTugas)
        })
        TugasCard()
    }
}

@Composable
fun TugasForm(
    onTugasAdded: (Tugas) -> Unit // Callback to handle new Tugas
) {
    var matkul by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showDatePicker = false // Close date picker
                showTimePicker = true // Open time picker
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)
                showTimePicker = false // Close time picker
            },
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            true // true for 24-hour format
        ).show()
    }

    var dateTimeSelected by remember { mutableStateOf(false) }

    val allDataIsFilled by remember {
        derivedStateOf {
            matkul.isNotBlank() && description.isNotBlank() && dateTimeSelected
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(value = matkul, onValueChange = {
            matkul = it;
        }, label = { Text("Matkul") }, modifier = Modifier.fillMaxWidth()
        )

        TextField(value = description, onValueChange = {
            description = it;
        }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth()
        )

        // Combined Date and Time Button
        Button(
            onClick = {
                showDatePicker = true
                dateTimeSelected = true // Update state after date is selected
            }, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            if (!dateTimeSelected) {
                Text("Pilih Deadline!")
            } else {
                Text(
                    "Deadline: ${
                        SimpleDateFormat("dd/MM/yyyy | HH:mm", Locale.getDefault()).format(
                            selectedDateTime.time
                        )
                    }"
                )
            }
        }

        Button(
            enabled = allDataIsFilled, onClick = {
                val newTugas = Tugas(
                    matkul = matkul,
                    description = description,
                    isDone = false,
                    dateAdded = Date().time, // Current time
                    dateDue = selectedDateTime.timeInMillis // Convert Calendar to Date
                )
                onTugasAdded(newTugas)
                // Reset input forms
                matkul = ""
                description = ""
                dateTimeSelected = false
            }, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Tambahkan Tugas!")
        }
    }
}

@Composable
fun TugasCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TugasViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )
    val tugasList by viewModel.tugasList.observeAsState(emptyList()) // Observe the LiveData
    val currentDate = remember { Date().time }
    val dateFormat =
        SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm", Locale("id")) // Indonesian locale

    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        tugasList.forEach { tugas ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(0.8f)
                    ) {

//                    Text("${tugas.id}")
                        Text(tugas.matkul, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(tugas.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (tugas.dateDue != 0L && tugas.dateDue!! < currentDate && !tugas.isDone) {
                            Text("Tugas is overdue!", color = Color.Red)
                        }
                        Text("Deadline:\n${tugas.dateDue?.let { dateFormat.format(Date(it)) } ?: ""}",
                            fontWeight = FontWeight.Bold)
                        Text("Ditambahkan pada:\n${
                            tugas.dateAdded?.let {
                                dateFormat.format(
                                    Date(
                                        it
                                    )
                                )
                            } ?: ""
                        }")
                    }
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(0.2f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = tugas.isDone,
                            onCheckedChange = { viewModel.markAsDone(tugas.id, !tugas.isDone) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.primary
                            ),

                            )
                    }

                }
            }
        }

    }

}
