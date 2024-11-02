
package com.example.sportsbooking.venue

import java.util.Date

data class Venue(
    var id: String = "",                  // ID unik untuk venue
    var nama: String = "",                 // Nama venue atau lapangan
    var jenisLapangan: String = "",        // Jenis lapangan, misalnya "Basketball", "Football"
    var alamat: String = "",               // Alamat venue
    var status: String = "",               // Status venue, misalnya "Tersedia" atau "Tidak Tersedia"
    var imageUrl: String = "",             // URL gambar venue
    var pricePerHour: Double = 0.0,        // Harga sewa per jam
    var availableStartTime: Date? = null,  // Waktu mulai ketersediaan
    var availableEndTime: Date? = null,    // Waktu akhir ketersediaan
    var category: String = "",             // Kategori olahraga untuk venue
    val capacity: Int = 0
)
