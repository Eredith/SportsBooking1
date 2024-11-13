package com.example.sportsbooking.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore

class MakananFragment : Fragment() {

    private lateinit var recyclerViewMakanan: RecyclerView
    private lateinit var makananAdapter: MakananAdapter
    private val makananList = mutableListOf<Makanan>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_makanan, container, false)

        recyclerViewMakanan = view.findViewById(R.id.recyclerViewMakanan)
        recyclerViewMakanan.layoutManager = LinearLayoutManager(requireContext())
        makananAdapter = MakananAdapter(makananList)
        recyclerViewMakanan.adapter = makananAdapter

        loadMakananData()

        return view
    }

    private fun loadMakananData() {
        firestore.collection("makanan").get()
            .addOnSuccessListener { result ->
                makananList.clear()
                for (document in result) {
                    val makanan = document.toObject(Makanan::class.java)
                    makananList.add(makanan)
                }
                makananAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal mengambil data makanan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}