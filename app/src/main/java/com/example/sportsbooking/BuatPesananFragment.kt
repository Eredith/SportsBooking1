package com.example.sportsbooking

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BuatPesananFragment : Fragment() {

    private lateinit var etNama: EditText
    private lateinit var etJenisLapangan: EditText
    private lateinit var etAlamat: EditText
    private lateinit var spinnerStatusLapangan: Spinner
    private lateinit var btnSimpanPesanan: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imgLapangan: ImageView
    private lateinit var btnPilihGambar: Button
    private var imageUri: Uri? = null
    private var imageUrl: String = ""
    private lateinit var storageReference: StorageReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout for Buat Pesanan
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

        // Inisialisasi elemen UI
        etNama = view.findViewById(R.id.etNama)
        etJenisLapangan = view.findViewById(R.id.etJenisLapangan)
        etAlamat = view.findViewById(R.id.etAlamat)
        spinnerStatusLapangan = view.findViewById(R.id.spinnerStatusLapangan)
        btnSimpanPesanan = view.findViewById(R.id.btnSimpanPesanan)

        // Data untuk Spinner (status ketersediaan lapangan)
        val statusOptions = arrayOf("Tersedia", "Tidak Tersedia")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusLapangan.adapter = adapter

        // Listener untuk tombol simpan pesanan
        btnSimpanPesanan.setOnClickListener {
            simpanPesananKeFirebase()
        }

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data
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
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    private fun pilihGambarDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // Fungsi untuk menyimpan pesanan ke Firebase Firestore
    private fun simpanPesananKeFirebase() {
        val namaLapangan = etNama.text.toString().trim()
        val jenisLapangan = etJenisLapangan.text.toString().trim()
        val alamatLapangan = etAlamat.text.toString().trim()
        val statusLapangan = spinnerStatusLapangan.selectedItem.toString()

        if (namaLapangan.isEmpty() || jenisLapangan.isEmpty() || alamatLapangan.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload gambar terlebih dahulu jika ada gambar yang dipilih
        uploadGambarKeFirebaseStorage(onSuccess = { imageUrl ->
            // Jika upload gambar berhasil, simpan pesanan ke Firestore
            val pesanan = hashMapOf(
                "nama" to namaLapangan,
                "jenisLapangan" to jenisLapangan,
                "alamat" to alamatLapangan,
                "status" to statusLapangan,
                "imageUrl" to imageUrl  // Simpan URL gambar
            )

            firestore.collection("pesanan")
                .add(pesanan)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Pesanan berhasil disimpan", Toast.LENGTH_SHORT).show()
                    // Reset input setelah berhasil disimpan
                    etNama.text.clear()
                    etJenisLapangan.text.clear()
                    etAlamat.text.clear()
                    spinnerStatusLapangan.setSelection(0)
                    imgLapangan.setImageResource(0)  // Reset gambar yang ditampilkan
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Gagal menyimpan pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }, onFailure = { exception ->
            Toast.makeText(requireContext(), "Gagal mengupload gambar: ${exception.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
