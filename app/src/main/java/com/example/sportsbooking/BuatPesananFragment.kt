package com.example.sportsbooking

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class BuatPesananFragment : Fragment() {

    private lateinit var etNama: EditText
    private lateinit var etJenisLapangan: EditText
    private lateinit var etHargaLapangan: EditText
    private lateinit var etKapasitasLapangan: EditText
    private lateinit var etAlamat: EditText
    private lateinit var spinnerStatusLapangan: Spinner
    private lateinit var btnSimpanPesanan: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imgLapangan: ImageView
    private lateinit var btnPilihGambar: Button
    private var imageUri: Uri? = null
    private var imageUrl: String = ""
    private lateinit var storageReference: StorageReference

    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null

    private val PICK_IMAGE_REQUEST = 100
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk Buat Pesanan
        val view = inflater.inflate(R.layout.fragment_buat_pesanan, container, false)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()
        // Inisialisasi Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Inisialisasi elemen UI
        imgLapangan = view.findViewById(R.id.imgLapangan)
        btnPilihGambar = view.findViewById(R.id.btnPilihGambar)

        // Listener untuk memilih gambar
        btnPilihGambar.setOnClickListener {
            pilihGambarDariGaleri()
        }

        // Inisialisasi elemen UI lainnya
        etNama = view.findViewById(R.id.etNama)
        etJenisLapangan = view.findViewById(R.id.etJenisLapangan)
        etHargaLapangan = view.findViewById(R.id.etHargaLapangan)
        etKapasitasLapangan = view.findViewById(R.id.etKapasitasLapangan)
        etAlamat = view.findViewById(R.id.etAlamat)
        spinnerStatusLapangan = view.findViewById(R.id.spinnerStatusLapangan)
        btnSimpanPesanan = view.findViewById(R.id.btnSimpanPesanan)

        textStartTime = view.findViewById(R.id.textStartTime)
        textEndTime = view.findViewById(R.id.textEndTime)

        // Data untuk Spinner (status ketersediaan lapangan)
        val statusOptions = arrayOf("Tersedia", "Tidak Tersedia")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusLapangan.adapter = adapter

        // Listener untuk memilih waktu mulai
        textStartTime.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        // Listener untuk memilih waktu selesai
        textEndTime.setOnClickListener {
            showTimePicker(isStartTime = false)
        }

        // Listener untuk tombol simpan pesanan
        btnSimpanPesanan.setOnClickListener {
            simpanPesananKeFirebase()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imgLapangan.setImageURI(imageUri)  // Tampilkan gambar yang dipilih di ImageView
        }
    }

    private fun uploadGambarKeFirebaseStorage(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        imageUri?.let {
            val fileName = "images/${System.currentTimeMillis()}.jpg"
            val fileReference = storageReference.child(fileName)

            fileReference.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
                    // Mendapatkan URL gambar yang diupload
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }.addOnFailureListener { e ->
                        onFailure(e)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } ?: run {
            onFailure(Exception("No image selected"))
        }
    }

    private fun pilihGambarDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Fungsi untuk memilih waktu menggunakan TimePickerDialog
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (isStartTime) {
                    selectedStartTime = selectedTime
                    textStartTime.text = "Mulai: ${timeFormatter.format(selectedTime.time)}"
                } else {
                    selectedEndTime = selectedTime
                    textEndTime.text = "Selesai: ${timeFormatter.format(selectedTime.time)}"
                }
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        picker.show()
    }

    // Fungsi untuk menyimpan pesanan ke Firebase Firestore
    private fun simpanPesananKeFirebase() {
        val namaLapangan = etNama.text.toString().trim()
        val jenisLapangan = etJenisLapangan.text.toString().trim()
        val hargaLapangan = etHargaLapangan.text.toString().trim()
        val kapasitasLapangan = etKapasitasLapangan.text.toString().trim()
        val alamatLapangan = etAlamat.text.toString().trim()
        val statusLapangan = spinnerStatusLapangan.selectedItem.toString()

        // Validasi input
        if (namaLapangan.isEmpty() || jenisLapangan.isEmpty() || hargaLapangan.isEmpty() ||
            kapasitasLapangan.isEmpty() || alamatLapangan.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi waktu
        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(requireContext(), "Silakan pilih waktu mulai dan selesai", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime!!.after(selectedEndTime)) {
            Toast.makeText(requireContext(), "Waktu mulai harus sebelum waktu selesai", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload gambar terlebih dahulu jika ada gambar yang dipilih
        uploadGambarKeFirebaseStorage(onSuccess = { uploadedImageUrl ->
            // Jika upload gambar berhasil, simpan pesanan ke Firestore
            val venue = Venue(
                name = namaLapangan,
                price = hargaLapangan,
                location = alamatLapangan,
                category = jenisLapangan,
                capacity = kapasitasLapangan.toInt(),
                imageResource = uploadedImageUrl,
                status = statusLapangan,
                availableStartTime = selectedStartTime?.time,
                availableEndTime = selectedEndTime?.time
            )

            firestore.collection("venues")
                .add(venue)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Venue berhasil disimpan", Toast.LENGTH_SHORT).show()
                    // Reset input setelah berhasil disimpan
                    etNama.text.clear()
                    etJenisLapangan.text.clear()
                    etHargaLapangan.text.clear()
                    etKapasitasLapangan.text.clear()
                    etAlamat.text.clear()
                    spinnerStatusLapangan.setSelection(0)
                    imgLapangan.setImageURI(null)  // Reset gambar yang ditampilkan
                    textStartTime.text = "Mulai: Belum Dipilih"
                    textEndTime.text = "Selesai: Belum Dipilih"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Gagal menyimpan venue: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }, onFailure = { exception ->
            Toast.makeText(requireContext(), "Gagal mengupload gambar: ${exception.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
