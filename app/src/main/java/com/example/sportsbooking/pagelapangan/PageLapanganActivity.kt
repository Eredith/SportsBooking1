package com.example.sportsbooking.pagelapangan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.example.sportsbooking.databinding.PageLapanganBinding
import com.example.sportsbooking.detaillapangan.DetailLapanganActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale

class PageLapanganActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: PageLapanganBinding
    private lateinit var mMap: GoogleMap
    private var venueLocation: String = ""
    private var venueName: String = ""
    private lateinit var textLokasi: TextView // TextView untuk menampilkan lokasi saat ini


    // Untuk mendapatkan lokasi terkini
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        venueName = intent.getStringExtra("venue_name") ?: "N/A"
        val venuePrice = intent.getDoubleExtra("venue_price", 0.0)
        venueLocation = intent.getStringExtra("venue_location") ?: "N/A"
        val venueCategory = intent.getStringExtra("venue_category") ?: "N/A"
        val venueCapacity = intent.getIntExtra("venue_capacity", 0)
        val venueStatus = intent.getStringExtra("venue_status") ?: "N/A"
        val venueImageUrl = intent.getStringExtra("venue_imageUrl") ?: ""
        val venueStartTime = intent.getStringExtra("venue_availableStartTime") ?: "N/A"
        val venueEndTime = intent.getStringExtra("venue_availableEndTime") ?: "N/A"

        // Populate UI with data
        binding.venueTitle.text = "$venueName\n$venueCategory"
        binding.venueCategory.text = venueCategory
        binding.venueAlamat.text = "Alamat: $venueLocation"
        binding.venuePrice.text = "Harga: Rp${venuePrice}"
        binding.openingHoursTitle.text = getString(R.string.jam_buka)
        binding.openingHoursText.text = "$venueStartTime - $venueEndTime"
        binding.venueLocationText.text = venueLocation
        binding.venueDescriptionDetail.text = "Deskripsi dari alamat $venueName Badminton. Deskripsi dari alamat $venueName Badminton."

        textLokasi = findViewById(R.id.text_lokasi)


        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Load venue image using Glide
        Glide.with(this)
            .load(venueImageUrl)
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .centerCrop()
            .into(binding.venueImage)

        // Back button listener
        binding.backbuttonPageLapangan.setOnClickListener {
            finish()
        }

        // Book Now button listener
        binding.lapanganPageButton.setOnClickListener {
            try {
                // Membuat Intent untuk memulai LapanganActivity
                val intent = Intent(this, DetailLapanganActivity::class.java).apply {
                    putExtra("venue_name", venueName)
                    putExtra("venue_price", venuePrice)
                    putExtra("venue_location", venueLocation)
                    putExtra("venue_category", venueCategory)
                    putExtra("venue_capacity", venueCapacity)
                    putExtra("venue_status", venueStatus)
                    putExtra("venue_imageUrl", venueImageUrl)
                    putExtra("venue_availableStartTime", venueStartTime)
                    putExtra("venue_availableEndTime", venueEndTime)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.error_booking), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coba dapatkan koordinat dari alamat
        val coordinates = getLatLngFromAddress(venueLocation)

        if (coordinates != null) {
            // Tambahkan marker di lokasi venue
            mMap.addMarker(MarkerOptions().position(coordinates).title(venueName))
            // Arahkan kamera ke lokasi venue
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        } else {
            // Handle jika koordinat tidak ditemukan, misalnya tampilkan pesan error
            Toast.makeText(this, "Tidak dapat menemukan lokasi", Toast.LENGTH_SHORT).show()
        }

        // Aktifkan lokasi saya jika izin diberikan
        enableMyLocation()
        getDeviceLocation()
    }

    private fun getLatLngFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                return LatLng(location.latitude, location.longitude)
            }
        } catch (e: IOException) {
            Log.e("PageLapanganActivity", "Error getting coordinates from address", e)
        }
        return null
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun getDeviceLocation() {
        try {
            if (isPermissionGranted()) {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Dapatkan lokasi terakhir
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation.latitude,
                                        lastKnownLocation.longitude
                                    ), 15f
                                )
                            )

                            // Dapatkan alamat dari koordinat
                            val address = getAddressFromLatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                            // Tampilkan alamat di TextView
                            textLokasi.text = address ?: "Lokasi tidak ditemukan"

                        } else {
                            Log.d("PageLapanganActivity", "Current location is null. Using defaults.")
                            Log.e("PageLapanganActivity", "Exception: %s", task.exception)
                            // Set default location (misalnya lokasi venue)
                            val coordinates = getLatLngFromAddress(venueLocation)
                            if (coordinates != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
                            }
                            mMap.uiSettings.isMyLocationButtonEnabled = false
                        }
                    } else {
                        Log.d("PageLapanganActivity", "Current location is null. Using defaults.")
                        Log.e("PageLapanganActivity", "Exception: %s", task.exception)
                        // Set default location (misalnya lokasi venue)
                        val coordinates = getLatLngFromAddress(venueLocation)
                        if (coordinates != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
                        }
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                // Format alamat sesuai kebutuhan
                return address.getAddressLine(0) // Baris alamat lengkap
            }
        } catch (e: IOException) {
            Log.e("PageLapanganActivity", "Error getting address from coordinates", e)
        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}