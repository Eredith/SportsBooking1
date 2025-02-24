package com.example.sportsbooking.pesanan

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.sportsbooking.R
import com.example.sportsbooking.venue.Venue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
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
    private lateinit var storageReference: StorageReference

    private lateinit var spinnerKategoriOlahraga: Spinner
    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null

    private val PICK_IMAGE_REQUEST = 100
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Food-related fields
    private lateinit var etNamaMakanan: EditText
    private lateinit var etHargaMakanan: EditText
    private lateinit var btnSimpanMakanan: Button
    private lateinit var imgMakanan: ImageView
    private lateinit var btnPilihGambarMakanan: Button
    private var imageUriMakanan: Uri? = null

    private val CAMERA_REQUEST_CODE = 101
    private lateinit var currentPhotoPath: String

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buat_pesanan, container, false)

        checkPermissions()

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

        spinnerKategoriOlahraga = view.findViewById(R.id.spinnerKategoriOlahraga)
        val kategoriOptions = arrayOf("badminton", "driving range")
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

        // Initialize the launchers
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.data?.let {
                    imageUri = it
                    imgLapangan.setImageURI(it)
                    if (::imgMakanan.isInitialized) {
                        imageUriMakanan = it
                        imgMakanan.setImageURI(it)
                    }
                }
            }
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                imgLapangan.setImageURI(imageUri)
            } else {
                Toast.makeText(requireContext(), "Gambar tidak diambil", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any input event listeners or callbacks here
        btnPilihGambar.setOnClickListener(null)
        textStartTime.setOnClickListener(null)
        textEndTime.setOnClickListener(null)
        btnSimpanPesanan.setOnClickListener(null)
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
        val options = arrayOf<CharSequence>("Ambil Foto", "Pilih dari Galeri", "Batal")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Gambar")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Ambil Foto" -> {
                    ambilGambarDariKamera()
                }
                options[item] == "Pilih dari Galeri" -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    pickImageLauncher.launch(intent)
                }
                options[item] == "Batal" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun pilihGambarMakananDariGaleri() {
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
            val venue = Venue(
                id = "",
                nama = namaLapangan,
                jenisLapangan = jenisLapangan,
                alamat = alamatLapangan,
                status = statusLapangan,
                imageUrl = uploadedImageUrl,
                pricePerHour = hargaLapangan.toDouble(),
                availableStartTime = selectedStartTime?.time,
                availableEndTime = selectedEndTime?.time,
                category = kategoriOlahraga,
                capacity = kapasitasLapangan.toInt()
            )

            firestore.collection("sports_center").document(kategoriOlahraga).collection("courts")
                .document(namaLapangan) // Menyimpan nama lapangan sebagai nama dokumen
                .set(venue)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Pesanan berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal menyimpan pesanan", Toast.LENGTH_SHORT).show()
                }
        }, onFailure = {
            Toast.makeText(requireContext(), "Gagal mengunggah gambar: ${it.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun ambilGambarDariKamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), "Gagal membuat file gambar", Toast.LENGTH_SHORT).show()
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.sportsbooking.fileprovider",
                    it
                )
                imageUri = photoURI
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePhotoLauncher.launch(intent)
            }
        } else {
            Toast.makeText(requireContext(), "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(), 0)
        }
    }

    private fun simpanMakananKeFirebase() {
        val namaMakanan = etNamaMakanan.text.toString().trim()
        val hargaMakanan = etHargaMakanan.text.toString().trim()

        if (namaMakanan.isEmpty() || hargaMakanan.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        uploadGambarKeFirebaseStorage(onSuccess = { uploadedImageUrl ->
            val makananData = mapOf(
                "nama" to namaMakanan,
                "harga" to hargaMakanan.toDouble(),
                "imageUrl" to uploadedImageUrl
            )

            firestore.collection("makanan").add(makananData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Data makanan berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal menyimpan data makanan", Toast.LENGTH_SHORT).show()
                }
        }, onFailure = {
            Toast.makeText(requireContext(), "Gagal mengunggah gambar makanan: ${it.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
