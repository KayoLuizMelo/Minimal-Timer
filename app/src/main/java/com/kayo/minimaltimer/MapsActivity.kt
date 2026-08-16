package com.kayo.minimaltimer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kayo.minimaltimer.utils.HelperMethods
import java.util.*

/**
 * MÓDULO 7: Activity para Google Maps e Geolocalização.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocationInfo: TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tvLocationInfo = findViewById(R.id.tvLocationInfo)
        etSearch = findViewById(R.id.etSearch)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSearch.setOnClickListener {
            val location = etSearch.text.toString()
            if (location.isNotEmpty()) {
                searchLocation(location)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Habilita localização se tiver permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }

        // Interação: Toque no mapa (Geocodificação Reversa)
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Local Selecionado"))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            reverseGeocode(latLng)
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    reverseGeocode(currentLatLng)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // Geocodificação: Endereço -> Coordenadas
    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                tvLocationInfo.text = "Endereço: ${address.getAddressLine(0)}"
            } else {
                HelperMethods.showToast(this, "Local não encontrado")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Geocodificação Reversa: Coordenadas -> Endereço
    private fun reverseGeocode(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0].getAddressLine(0)
                tvLocationInfo.text = "Endereço: $address"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
