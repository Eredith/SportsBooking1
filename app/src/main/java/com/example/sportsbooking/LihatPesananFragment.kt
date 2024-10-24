package com.example.sportsbooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class LihatPesananFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananList: ArrayList<Pesanan>
    private lateinit var pesananAdapter: PesananAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout for Lihat Pesanan
        val view = inflater.inflate(R.layout.fragment_lihat_pesanan, container, false)

        // Inisialisasi Firestore dan RecyclerView
        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewPesanan)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pesananList = ArrayList()
        pesananAdapter = PesananAdapter(pesananList)
        recyclerView.adapter = pesananAdapter

        // Ambil data pesanan dari Firestore
        ambilDataPesanan()

        return view
    }

    private fun ambilDataPesanan() {
        firestore.collection("pesanan")
            .get()
            .addOnSuccessListener { result ->
                pesananList.clear()
                for (document in result) {
                    val pesanan = document.toObject(Pesanan::class.java)
                    pesananList.add(pesanan)
                }
                pesananAdapter.notifyDataSetChanged()  // Update RecyclerView setelah data diterima
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
