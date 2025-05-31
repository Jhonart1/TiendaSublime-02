package com.sargames.tiendasublime.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val TAG = "LocationManager"

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            Log.e(TAG, "No hay permisos de ubicación")
            onLocationReceived(null)
            return
        }

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(listener: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(TAG, "Ubicación obtenida: Latitud: ${location.latitude}, Longitud: ${location.longitude}")
                    onLocationReceived(location)
                } else {
                    Log.e(TAG, "No se pudo obtener la ubicación")
                    onLocationReceived(null)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener la ubicación: ${e.message}")
                onLocationReceived(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al solicitar la ubicación: ${e.message}")
            onLocationReceived(null)
        }
    }
} 