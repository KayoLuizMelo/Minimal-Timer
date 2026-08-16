package com.kayo.minimaltimer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kayo.minimaltimer.database.DatabaseHelper
import com.kayo.minimaltimer.utils.HelperMethods
import java.util.*

/**
 * MÓDULO 7: Activity para Google Maps e Geolocalização.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvAddress: TextView
    private lateinit var etSearch: EditText
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var timerSaveContainer: LinearLayout
    private lateinit var etLocationTimer: EditText
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        dbHelper = DatabaseHelper(this)
        tvAddress = findViewById(R.id.tvAddress)
        etSearch = findViewById(R.id.etSearch)
        timerSaveContainer = findViewById(R.id.timerSaveContainer)
        etLocationTimer = findViewById(R.id.etLocationTimer)
        val btnSaveLocationTimer = findViewById<View>(R.id.btnSaveLocationTimer)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val fabMyLocation = findViewById<FloatingActionButton>(R.id.fabMyLocation)
        val fabMapType = findViewById<FloatingActionButton>(R.id.fabMapType)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSearch.setOnClickListener {
            val location = etSearch.text.toString()
            if (location.isNotEmpty()) {
                searchLocation(location)
            }
        }

        btnSaveLocationTimer.setOnClickListener {
            val minutes = etLocationTimer.text.toString().toIntOrNull()
            if (minutes != null && selectedLatLng != null) {
                dbHelper.saveLocationTimer(selectedLatLng!!.latitude, selectedLatLng!!.longitude, tvAddress.text.toString(), minutes)
                HelperMethods.showToast(this, "Timer de $minutes min salvo para este local!")
                mMap.addMarker(MarkerOptions()
                    .position(selectedLatLng!!)
                    .title("Timer: $minutes min")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            } else {
                HelperMethods.showToast(this, "Informe os minutos!")
            }
        }

        fabMyLocation.setOnClickListener {
            getCurrentLocation()
        }

        fabMapType.setOnClickListener {
            if (::mMap.isInitialized) {
                mMap.mapType = if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL) {
                    GoogleMap.MAP_TYPE_SATELLITE
                } else {
                    GoogleMap.MAP_TYPE_NORMAL
                }
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
            selectedLatLng = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Local Selecionado"))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            reverseGeocode(latLng)
            
            // Verifica se já existe um timer salvo
            val savedMin = dbHelper.getLocationTimer(latLng.latitude, latLng.longitude)
            if (savedMin != null) {
                etLocationTimer.setText(savedMin.toString())
                HelperMethods.showToast(this, "Timer existente: $savedMin min")
            } else {
                etLocationTimer.setText("")
            }
            timerSaveContainer.visibility = View.VISIBLE
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
                selectedLatLng = latLng
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                tvAddress.text = "Endereço: ${address.getAddressLine(0)}"
                timerSaveContainer.visibility = View.VISIBLE
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
                tvAddress.text = "Endereço: $address"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
