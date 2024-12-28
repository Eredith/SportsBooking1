package com.example.sportsbooking.pesanan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore

class LihatPesananFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananList: ArrayList<Pesanan>
    private lateinit var pesananAdapter: PesananAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var db: FirebaseFirestore
    private val allBookings = mutableListOf<Pesanan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lihat_pesanan, container, false)

        firestore = FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewPesanan)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pesananList = ArrayList()
        pesananAdapter = PesananAdapter(pesananList)
        recyclerView.adapter = pesananAdapter

        fetchAllBookings()

        return view
    }

    private fun fetchAllBookings() {
        val categories = listOf("badminton", "driving range")
        fetchBookings(categories)
    }

    private fun fetchBookings(categories: List<String>) {
        for (category in categories) {
            val courtsRef = db.collection("sports_center")
                .document(category)
                .collection("courts")

            courtsRef.get()
                .addOnSuccessListener { courtDocuments ->
                    if (courtDocuments.isEmpty) {
                        Log.d("BookingActivity", "No courts found for category $category")
                    } else {
                        for (courtDocument in courtDocuments) {
                            val court = courtDocument.id
                            val bookingsRef = db.collection("sports_center")
                                .document(category)
                                .collection("courts")
                                .document(court)
                                .collection("bookings")

                            bookingsRef.get()
                                .addOnSuccessListener { bookingDocuments ->
                                    if (bookingDocuments.isEmpty) {
                                        Log.d("BookingActivity", "No bookings found for $category - $court")
                                    } else {
                                        for (bookingDocument in bookingDocuments) {
                                            val bookingDate = bookingDocument.id
                                            val bookingData = bookingDocument.data

                                            bookingData.forEach { (timeSlot, details) ->
                                                if (details is Map<*, *>) {
                                                    val bookedBy = details["booked_by"] as? String
                                                    val status = details["status"] as? String
                                                    if (status == "booked") {
                                                        if (bookedBy != null) {
                                                            getUsername(bookedBy) { username ->
                                                                allBookings.add(
                                                                    Pesanan(
                                                                        sportsCenter = category,
                                                                        court = court,
                                                                        bookingDate = bookingDate,
                                                                        timeSlot = timeSlot,
                                                                        bookedBy = username,
                                                                        status = status
                                                                    )
                                                                )
                                                                if (categories.indexOf(category) == categories.size - 1 && courtDocument == courtDocuments.last()) {
                                                                    displayBookings(allBookings)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BookingActivity", "Failed to fetch bookings for $category - $court", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BookingActivity", "Failed to fetch courts for category $category", e)
                }
        }
    }

    private fun displayBookings(bookings: List<Pesanan>) {
        if (bookings.isEmpty()) {
            recyclerView.visibility = View.GONE
            view?.findViewById<TextView>(R.id.empty_view)?.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.empty_view)?.visibility = View.GONE
            pesananAdapter = PesananAdapter(bookings)
            recyclerView.adapter = pesananAdapter
        }
    }

    private fun getUsername(userId: String, callback: (String) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("username") ?: userId
                    callback(username)
                } else {
                    callback(userId)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LihatPesananFragment", "Failed to fetch username for userId: $userId", e)
                callback(userId)
            }
    }
}