package com.example.sportsbooking.pesanan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadMakananFragment : Fragment() {

    private lateinit var etNamaMakanan: EditText
    private lateinit var etHargaMakanan: EditText
    private lateinit var btnSimpanMakanan: Button
    private lateinit var imgMakanan: ImageView
    private lateinit var btnPilihGambarMakanan: Button
    private var imageUriMakanan: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_makanan, container, false)

        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        etNamaMakanan = view.findViewById(R.id.etNamaMakanan)
        etHargaMakanan = view.findViewById(R.id.etHargaMakanan)
        btnSimpanMakanan = view.findViewById(R.id.btnSimpanMakanan)
        imgMakanan = view.findViewById(R.id.imgMakanan)
        btnPilihGambarMakanan = view.findViewById(R.id.btnPilihGambarMakanan)

        btnPilihGambarMakanan.setOnClickListener {
            pilihGambarMakananDariGaleri()
        }

        btnSimpanMakanan.setOnClickListener {
            simpanMakananKeFirebase()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUriMakanan = data.data
            imgMakanan.setImageURI(imageUriMakanan)
        }
    }

    private fun pilihGambarMakananDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun uploadGambarKeFirebaseStorage(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        imageUriMakanan?.let {
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