package com.example.sportsbooking.pesanan

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private lateinit var spinnerKategoriOlahraga: Spinner
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
        val view = inflater.inflate(R.layout.fragment_buat_pesanan, container, false)

        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        imgLapangan = view.findViewById(R.id.imgLapangan)
        btnPilihGambar = view.findViewById(R.id.btnPilihGambar)

        btnPilihGambar.setOnClickListener {
            pilihGambarDariGaleri()
        }

        etNama = view.findViewById(R.id.etNama)
        etJenisLapangan = view.findViewById(R.id.etJenisLapangan)
        etHargaLapangan = view.findViewById(R.id.etHargaLapangan)
        etKapasitasLapangan = view.findViewById(R.id.etKapasitasLapangan)
        etAlamat = view.findViewById(R.id.etAlamat)
        spinnerStatusLapangan = view.findViewById(R.id.spinnerStatusLapangan)
        btnSimpanPesanan = view.findViewById(R.id.btnSimpanPesanan)

        textStartTime = view.findViewById(R.id.textStartTime)
        textEndTime = view.findViewById(R.id.textEndTime)

        // Spinner untuk kategori olahraga
        spinnerKategoriOlahraga = view.findViewById(R.id.spinnerKategoriOlahraga)
        val kategoriOptions = arrayOf("badminton", "golf")
        val kategoriAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            kategoriOptions
        )
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategoriOlahraga.adapter = kategoriAdapter

        val statusOptions = arrayOf("Tersedia", "Tidak Tersedia")
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusLapangan.adapter = statusAdapter

        textStartTime.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        textEndTime.setOnClickListener {
            showTimePicker(isStartTime = false)
        }

        btnSimpanPesanan.setOnClickListener {
            simpanPesananKeFirebase()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imgLapangan.setImageURI(imageUri)
        }
    }

    private fun uploadGambarKeFirebaseStorage(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        imageUri?.let {
            val fileName = "images/${System.currentTimeMillis()}.jpg"
            val fileReference = storageReference.child(fileName)

            fileReference.putFile(it)
                .addOnSuccessListener { taskSnapshot ->
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
            true
        )

        picker.show()
    }

    private fun simpanPesananKeFirebase() {
        val namaLapangan = etNama.text.toString().trim()
        val jenisLapangan = etJenisLapangan.text.toString().trim()
        val hargaLapangan = etHargaLapangan.text.toString().trim()
        val kapasitasLapangan = etKapasitasLapangan.text.toString().trim()
        val alamatLapangan = etAlamat.text.toString().trim()
        val statusLapangan = spinnerStatusLapangan.selectedItem.toString()
        val kategoriOlahraga = spinnerKategoriOlahraga.selectedItem.toString()

        if (namaLapangan.isEmpty() || jenisLapangan.isEmpty() || hargaLapangan.isEmpty() ||
            kapasitasLapangan.isEmpty() || alamatLapangan.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(requireContext(), "Silakan pilih waktu mulai dan selesai", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime!!.after(selectedEndTime)) {
            Toast.makeText(requireContext(), "Waktu mulai harus sebelum waktu selesai", Toast.LENGTH_SHORT).show()
            return
        }

        uploadGambarKeFirebaseStorage(onSuccess = { uploadedImageUrl ->
            val venue = mapOf(
                "name" to namaLapangan,
                "price" to hargaLapangan,
                "location" to alamatLapangan,
                "category" to jenisLapangan,
                "capacity" to kapasitasLapangan.toInt(),
                "imageResource" to uploadedImageUrl,
                "status" to statusLapangan,
                "availableStartTime" to selectedStartTime?.time,
                "availableEndTime" to selectedEndTime?.time
            )

            firestore.collection("sports_center").document(kategoriOlahraga)
                .collection("courts")
                .document(namaLapangan) // Menggunakan namaLapangan sebagai documentId
                .set(venue)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Venue berhasil disimpan", Toast.LENGTH_SHORT).show()
                    etNama.text.clear()
                    etJenisLapangan.text.clear()
                    etHargaLapangan.text.clear()
                    etKapasitasLapangan.text.clear()
                    etAlamat.text.clear()
                    spinnerStatusLapangan.setSelection(0)
                    imgLapangan.setImageURI(null)
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
