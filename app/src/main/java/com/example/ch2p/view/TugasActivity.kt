package com.example.ch2p.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ch2p.data.model.local.Tugas
import com.example.ch2p.view.model.TugasViewModel
import com.example.ch2p.view.model.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TugasActivity : ComponentActivity() {
    // Key Point: Managing Camera Permission State
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    private val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted

    // Declare a launcher for the camera permission request, handling the permission result
    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, update the state
                _isCameraPermissionGranted.value = true
            } else {
                // Permission denied: inform the user to enable it through settings
                Toast.makeText(
                    this,
                    "Go to settings and enable camera permission to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Checks camera permission and either starts the camera directly or requests permission
    private fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, update the state
                _isCameraPermissionGranted.value = true
            }

            else -> {
                // Permission is not granted: request it
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("CameraPermission", "Permission Granted")
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
//                setCameraPreview()
            } else {
                Log.e("CameraPermission", "Permission Denied")
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                // Handle permission denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) -> {
//                setCameraPreview() // Permission already granted
//            }
//            else -> {
//                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
//            }
//        }

        setContent {
            // Collect the camera permission state as a Compose state to automatically update the UI upon change
            val permissionGranted = isCameraPermissionGranted.collectAsState().value

            Box(modifier = Modifier.fillMaxSize()) {
                // Conditional UI rendering based on camera permission state
                if (permissionGranted) {
                    // If permission is granted, display the camera preview
                    CameraPreview()
                } else {
                    // If permission is not granted, display a button to request camera permission
                    Button(
                        onClick = {
                            // Invoke the method from BaseActivity to handle permission request
                            handleCameraPermission()
                        }, modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = "Start Preview")
                    }
                }
            }

//            CH2PTheme {

//                if (showCameraPreview) {
//                    CameraPreviewScreen()
//                } else {
//                    M8Screen()
//                }
//            }
        }

        enableEdgeToEdge()
    }

//    private fun setCameraPreview() {
//        showCameraPreview = true
//    }
}

@Composable
fun CameraPreview() {

    // Obtain the current context and lifecycle owner
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Remember a LifecycleCameraController for this composable
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // Bind the LifecycleCameraController to the lifecycleOwner
            bindToLifecycle(lifecycleOwner)
        }
    }

    // Key Point: Displaying the Camera Preview
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { ctx ->
        // Initialize the PreviewView and configure it
        PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_START
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            controller = cameraController // Set the controller to manage the camera lifecycle
        }
    }, onRelease = {
        // Release the camera controller when the composable is removed from the screen
        cameraController.unbind()
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Composable
fun CameraPreviewScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val cameraProvider = cameraProviderFuture.get() // Get the CameraProvider

    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    } // Set scale type
    val cameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

    DisposableEffect(lifecycleOwner, cameraProvider) {
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview
        )
        preview.surfaceProvider = previewView.surfaceProvider

        onDispose {
            cameraProvider.unbindAll()
        }
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
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
//            TugasForm(onTugasAdded = { newTugas ->
//                viewModel.insertTugas(newTugas)
//            })
            TugasCard()
        }
    }
}

@Composable
fun TugasScreen(
    modifier: Modifier = Modifier,
    cameraPermissionGranted: Boolean,
    onRequestCameraPermission: () -> Unit
) {
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
        TugasForm(cameraPermissionGranted,
            onTugasAdded = { newTugas -> viewModel.insertTugas(newTugas) },
            onRequestCameraPermission = { onRequestCameraPermission() })
        TugasCard()
    }
}

@Composable
fun TugasForm(
    cameraPermissionGranted: Boolean, onTugasAdded: (Tugas) -> Unit, // Callback to handle new Tugas
    onRequestCameraPermission: () -> Unit
) {
    var matkul by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for CameraXActivity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data  // Get the image URI (if any)
            capturedImageUri = imageUri
        }
    }

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
            matkul = it
        }, label = { Text("Matkul") }, modifier = Modifier.fillMaxWidth()
        )

        TextField(value = description, onValueChange = {
            description = it
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

//        Button(
//            onClick = {
//                // Navigate to the camera preview screen
//                showCameraPreview = true
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.CenterHorizontally)
//        ) {
//            Text("Open Camera")
//        }

        // Conditional UI rendering based on camera permission state
        if (cameraPermissionGranted) {
            // If permission is granted, display the camera preview
            Button(
                onClick = {
                    val intent = Intent(context, CameraXActivity::class.java)
                    launcher.launch(intent)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Open Camera")
            }
        } else {
            // If permission is not granted, display a button to request camera permission
            Button(
                onClick = {
                    // Invoke the method from MainActivity to handle permission request
                    onRequestCameraPermission()
                }, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Request Camera Permission")
            }
        }

        capturedImageUri?.let { uri ->
            Text(text = "Nama file gambar: ${uri}")
            AsyncImage(
                model = uri,
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            )
        }

        Button(
            enabled = allDataIsFilled, onClick = {
                val newTugas = Tugas(
                    matkul = matkul,
                    description = description,
                    isDone = false,
                    dateAdded = Date().time, // Current time
                    dateDue = selectedDateTime.timeInMillis, // Convert Calendar to Date
                    imageUri = capturedImageUri
                )
                onTugasAdded(newTugas)
                // Reset input forms
                matkul = ""
                description = ""
                dateTimeSelected = false
                capturedImageUri = null
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
                        Text(tugas.matkul, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(tugas.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = tugas.imageUri,
                            contentDescription = "Captured Image",
                            modifier = Modifier.fillMaxWidth().height(128.dp)
                        )
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
                            )
                        )
                    }
                }
            }
        }
    }
}
