package com.example.ch2p.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// Rotate image loaded from Uri
private fun rotateImageFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        // 1. Open input stream once and reuse
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 2. Decode bounds first
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)

            // 3. Get rotation from EXIF using the same input stream
            val exif = ExifInterface(inputStream) // Changed this line
            val rotation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )

            // 4. Calculate rotation degrees
            val rotationDegrees = when (rotation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }.toFloat() // Convert to Float here

            // 5. Decode and rotate bitmap using a new input stream (since the first one is closed)
            context.contentResolver.openInputStream(uri)?.use { inputStream2 ->
                val bitmap = BitmapFactory.decodeStream(inputStream2)
                val matrix = Matrix()
                matrix.postRotate(rotationDegrees) // Use calculated rotation degrees
                matrix.postScale(-1f, 1f) // flip horizontally
                return Bitmap.createBitmap(
                    bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true
                )
            }
        }
    } catch (e: Exception) {
        Log.e("CameraXActivity", "Failed to rotate image: ${e.message}", e)
        null
    }
}

class CameraViewModel : ViewModel() {
    private val _capturedImageUri = mutableStateOf<Uri?>(null)
    val capturedImageUri: State<Uri?> = _capturedImageUri

    fun setCapturedImageUri(uri: Uri?) {
        _capturedImageUri.value = uri
    }
}

class CameraXActivity : ComponentActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var imageCapture: ImageCapture? = null


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        setContent {
            val viewModel = viewModel<CameraViewModel>()
            CameraScreenContent(viewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Composable
    fun CameraScreenContent(viewModel: CameraViewModel = viewModel()) {
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        val context = LocalContext.current
        var preview by remember { mutableStateOf<Preview?>(null) }
        val capturedImageUri = viewModel.capturedImageUri

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    imageCapture = ImageCapture.Builder().build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraXActivity", "Use case binding failed", exc)
                    }

                }, executor)
                previewView
            }, modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(onClick = {
                lifecycleScope.launch {
                    takePhoto(context, viewModel)
                }
            }) {
                Icon(Icons.Filled.Done, contentDescription = "Ambil foto")
            }
        }

        LaunchedEffect(capturedImageUri.value) {
            capturedImageUri.value?.let { uri ->
                val resultIntent = Intent()
                resultIntent.putExtra("capturedImageUri", uri.toString())
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun takePhoto(context: Context, viewModel: CameraViewModel) {
        val name = SimpleDateFormat(
            FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CH2P")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        imageCapture?.takePicture(outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraXActivity", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri

                    savedUri?.let {
                        // Rotate the image *after* it's saved
                        val rotatedBitmap = rotateImageFromUri(context, savedUri)

                        // Update the ViewModel with the rotated bitmap's Uri. If you need the bitmap itself, create another function in the viewModel
                        rotatedBitmap?.let { viewModel.setCapturedImageUri(savedUri) }
                        // savePhotoToGalleryUseCase.call(rotatedBitmap!!)

                        val msg = "Foto berhasil diambil!"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d("CameraXActivity", msg)

                        val resultIntent = Intent()
                        resultIntent.data = savedUri // Put the Uri in the result Intent
                        setResult(RESULT_OK, resultIntent)

                        finish()
                    }
                }
            })
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

