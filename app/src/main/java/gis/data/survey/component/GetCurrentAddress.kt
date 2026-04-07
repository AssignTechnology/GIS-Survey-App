package gis.data.survey.component

import com.google.accompanist.permissions.isGranted


import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetCurrentAddress(
    onAddressFetched: (String?) -> Unit // Callback to send address to another screen/text field
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val isLocationEnabled = remember { mutableStateOf(isLocationEnabled(context)) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    LaunchedEffect(Unit) {
        if (!isLocationEnabled.value) {
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            onAddressFetched(null)
            return@LaunchedEffect
        }

        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
            return@LaunchedEffect
        }

        // Fetch location
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    processLocation(location, geocoder, onAddressFetched)
                } else {
                    // Request fresh location if last is null
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
                                    processLocation(loc, geocoder, onAddressFetched)
                                } ?: onAddressFetched(null)
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        },
                        android.os.Looper.getMainLooper()
                    )
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                onAddressFetched(null)
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
            onAddressFetched(null)
        }
    }
}

private fun processLocation(
    location: android.location.Location,
    geocoder: Geocoder,
    onAddressFetched: (String?) -> Unit
) {
    try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
        onAddressFetched(address)
    } catch (e: Exception) {
        onAddressFetched("Unable to get address")
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}