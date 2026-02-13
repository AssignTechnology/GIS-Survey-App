package com.example.npmlbd.component

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.scale

@RequiresApi(Build.VERSION_CODES.R)
object ImagePicker {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowImagePicker(
        showBottomSheet: Boolean,
        onDismiss: () -> Unit,
        onImageSelected: (ImageResult?) -> Unit,
        onCameraSelected: () -> Unit
    ) {
        val context = LocalContext.current
        val sheetState = rememberModalBottomSheetState()
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val cameraGranted = permissions[android.Manifest.permission.CAMERA] ?: false
            val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            if (cameraGranted && locationGranted && isLocationEnabled(context)) {
                onCameraSelected()
                onDismiss()
            } else {
                Toast.makeText(
                    context,
                    if (!isLocationEnabled(context)) "Please enable location services" else "Camera and location permissions are required",
                    Toast.LENGTH_LONG
                ).show()
                if (!isLocationEnabled(context)) {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                onDismiss()
            }
        }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let { it->
                    onImageSelected(null)
                    val modifiedUri = processGalleryImage(context, uri)
                    val base64 = modifiedUri?.let { getBase64FromUri(context, it) }
                    onImageSelected(ImageResult(modifiedUri, base64, null))
            }
            onDismiss()
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onDismiss()
                    onImageSelected(null)
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Camera",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            val cameraPermissionStatus = ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.CAMERA
                            )
                            val locationPermissionStatus = ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED &&
                                locationPermissionStatus == PackageManager.PERMISSION_GRANTED &&
                                isLocationEnabled(context)
                            ) {
                                onCameraSelected()
                                onDismiss()
                            } else {
                                if (!isLocationEnabled(context)) {
                                    Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
                                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                    onDismiss()
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                            }
                        }) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Camera,
                                    contentDescription = "Camera",
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                       /* IconButton(onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("Gallery")
                            }
                        }*/
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun CameraPreviewScreen(
        onImageCaptured: (ImageResult?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val previewView = remember { PreviewView(context) }
        val imageCapture = remember {
            ImageCapture.Builder()
                .setTargetRotation(context.display.rotation)
                .build()
        }
        var isBackCamera by remember { mutableStateOf(true) }
        var locationInfo by remember { mutableStateOf<LocationInfo?>(null) }
        val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
        val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val allPermissionsGranted = cameraPermissionState.status.isGranted && locationPermissionState.status.isGranted
        var isLocationEnabled by remember { mutableStateOf(isLocationEnabled(context)) }

        // Location handling
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        val geocoder = remember { Geocoder(context, Locale.getDefault()) }

        DisposableEffect(Unit) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {}
                override fun onProviderEnabled(provider: String) {
                    isLocationEnabled = true
                }
                override fun onProviderDisabled(provider: String) {
                    isLocationEnabled = false
                }
            }
            if (allPermissionsGranted) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
            onDispose {
                locationManager.removeUpdates(listener)
            }
        }

        LaunchedEffect(allPermissionsGranted, isLocationEnabled) {
            if (allPermissionsGranted && isLocationEnabled) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            try {
                                val addresses = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                )
                                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
                                locationInfo = LocationInfo(
                                    address = address,
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    timestamp = SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        Locale.getDefault()
                                    ).format(Date())
                                )
                            } catch (e: Exception) {
                                locationInfo = LocationInfo(
                                    address = "Unable to get address"+e.message,
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    timestamp = SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        Locale.getDefault()
                                    ).format(Date())
                                )
                            }
                        } else {
                            val locationRequest = LocationRequest.create().apply {
                                interval = 1000
                                fastestInterval = 500
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            }
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : LocationCallback() {
                                    override fun onLocationResult(result: LocationResult) {
                                        result.lastLocation?.let { loc ->
                                            try {
                                                val addresses = geocoder.getFromLocation(
                                                    loc.latitude,
                                                    loc.longitude,
                                                    1
                                                )
                                                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
                                                locationInfo = LocationInfo(
                                                    address = address,
                                                    latitude = loc.latitude,
                                                    longitude = loc.longitude,
                                                    timestamp = SimpleDateFormat(
                                                        "yyyy-MM-dd HH:mm:ss",
                                                        Locale.getDefault()
                                                    ).format(Date())
                                                )
                                            } catch (e: Exception) {
                                                locationInfo = LocationInfo(
                                                    address = "Unable to get address"+e.message,
                                                    latitude = loc.latitude,
                                                    longitude = loc.longitude,
                                                    timestamp = SimpleDateFormat(
                                                        "yyyy-MM-dd HH:mm:ss",
                                                        Locale.getDefault()
                                                    ).format(Date())
                                                )
                                            }
                                            fusedLocationClient.removeLocationUpdates(this)
                                        }
                                    }
                                },
                                Looper.getMainLooper()
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Location permission required"+e.message, Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            } else {
                if (!isLocationEnabled) {
                    Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {
                    if (!cameraPermissionState.status.isGranted) cameraPermissionState.launchPermissionRequest()
                    if (!locationPermissionState.status.isGranted) locationPermissionState.launchPermissionRequest()
                }
                onDismiss()
            }
        }

        fun bindCamera(cameraProvider: ProcessCameraProvider) {
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(
                    if (isBackCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
                )
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (_: Exception) {
                Toast.makeText(context, "Failed to start camera", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }

        LaunchedEffect(isBackCamera, allPermissionsGranted, isLocationEnabled) {
            if (allPermissionsGranted && isLocationEnabled) {
                val cameraProvider = cameraProviderFuture.get()
                bindCamera(cameraProvider)
            }
        }

        if (!allPermissionsGranted || !isLocationEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (!isLocationEnabled)
                            "Location services required to use camera"
                        else
                            "Camera and location permissions required",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = {
                            if (!isLocationEnabled) {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            } else {
                                if (!cameraPermissionState.status.isGranted) cameraPermissionState.launchPermissionRequest()
                                if (!locationPermissionState.status.isGranted) locationPermissionState.launchPermissionRequest()
                            }
                        }
                    ) {
                        Text("Grant Permissions")
                    }
                }
            }
            return
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
            )

            locationInfo?.let { info ->
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 150.dp)
                        .padding(horizontal = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Text(
                            text = "Location: ${info.address}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Lat: ${info.latitude}, Long: ${info.longitude}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Time: ${info.timestamp}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    if (isLocationEnabled) {
                        val photoFile = createImageFile(context)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    locationInfo?.let { info ->
                                        val modifiedUri = addTextToImage(context, photoFile, info)
                                        val base64 = modifiedUri?.let { getBase64FromUri(context, it) }
                                        Toast.makeText(context, "Photo captured", Toast.LENGTH_SHORT).show()
                                        onImageCaptured(ImageResult(modifiedUri, base64, info))
                                        cameraProviderFuture.get().unbindAll()
                                        onDismiss()
                                    } ?: run {
                                        Toast.makeText(context, "Location data unavailable", Toast.LENGTH_SHORT).show()
                                        onImageCaptured(null)
                                        cameraProviderFuture.get().unbindAll()
                                        onDismiss()
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
                                    onImageCaptured(null)
                                    cameraProviderFuture.get().unbindAll()
                                    onDismiss()
                                }
                            }
                        )
                    } else {
                        Toast.makeText(context, "Location services or permissions missing", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Camera,
                    contentDescription = "Capture Photo",
                    modifier = Modifier.size(100.dp)
                )
            }

            FloatingActionButton(
                onClick = { isBackCamera = !isBackCamera },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        requireNotNull(storageDir) { "Storage directory is null" }
        if (!storageDir.exists()) storageDir.mkdirs()
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun getBase64FromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                // Resize proportionally to reduce file size, keeping full image
                val maxDimension = 1920f // Cap longest side at 1920 pixels
                val scale = if (bitmap.width > bitmap.height) {
                    maxDimension / bitmap.width
                } else {
                    maxDimension / bitmap.height
                }
                val scaledBitmap = if (scale < 1f) {
                    bitmap.scale((bitmap.width * scale).toInt(), (bitmap.height * scale).toInt())
                } else {
                    bitmap
                }
                val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream) // Reduced quality to 85
                val bytes = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun processGalleryImage(context: Context, uri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                // Check EXIF orientation
                val tempFile = createImageFile(context)
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) // Temporary save to get EXIF
                }
                val exif = ExifInterface(tempFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }
                // Resize proportionally to reduce file size, keeping full image
                val maxDimension = 1920f // Cap longest side at 1920 pixels
                val scale = if (rotatedBitmap.width > rotatedBitmap.height) {
                    maxDimension / rotatedBitmap.width
                } else {
                    maxDimension / rotatedBitmap.height
                }
                val resizedBitmap = if (scale < 1f) {
                    rotatedBitmap.scale(
                        (rotatedBitmap.width * scale).toInt(),
                        (rotatedBitmap.height * scale).toInt()
                    )
                } else {
                    rotatedBitmap
                }
                // Save the processed image
                val outputFile = createImageFile(context)
                FileOutputStream(outputFile).use { out ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) // Reduced quality to 85
                }
                // Reset EXIF orientation
                val newExif = ExifInterface(outputFile.absolutePath)
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
                newExif.saveAttributes()
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addTextToImage(context: Context, file: File, locationInfo: LocationInfo): Uri? {
        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
            val maxDimension = 1920f
            val scale = if (rotatedBitmap.width > rotatedBitmap.height) {
                maxDimension / rotatedBitmap.width
            } else {
                maxDimension / rotatedBitmap.height
            }
            val resizedBitmap = if (scale < 1f) {
                rotatedBitmap.scale(
                    (rotatedBitmap.width * scale).toInt(),
                    (rotatedBitmap.height * scale).toInt()
                )
            } else {
                rotatedBitmap
            }
            val mutableBitmap = resizedBitmap.copy(resizedBitmap.config!!, true)
            val canvas = Canvas(mutableBitmap)
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = 40f
                setShadowLayer(1f, 0f, 1f, Color.BLACK)
                isAntiAlias = true
            }

            val text = """
                Location: ${locationInfo.address}
                Lat: ${locationInfo.latitude}, Long: ${locationInfo.longitude}
                Time: ${locationInfo.timestamp}
            """.trimIndent()
            val textBounds = Rect()
            paint.getTextBounds(text, 0, text.length, textBounds)
            val padding = 20f
            val textHeight = textBounds.height() * 3
            val textY = mutableBitmap.height - textHeight
            val textX = padding

            val backgroundPaint = Paint().apply {
                color = Color.BLACK
                alpha = 150
            }
            canvas.drawRect(
                0f,
                mutableBitmap.height - textHeight - padding * 2,
                mutableBitmap.width.toFloat(),
                mutableBitmap.height.toFloat(),
                backgroundPaint
            )

            text.split("\n").forEachIndexed { index, line ->
                canvas.drawText(line, textX, textY + (index * paint.textSize * 1.2f), paint)
            }

            val outputFile = createImageFile(context)
            FileOutputStream(outputFile).use { out ->
                mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) // Reduced quality to 85
            }
            val newExif = ExifInterface(outputFile.absolutePath)
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
            newExif.saveAttributes()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    data class LocationInfo(
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val timestamp: String
    )

    data class ImageResult(
        val uri: Uri?,
        val base64: String?,
        val locationInfo: LocationInfo?
    )
}