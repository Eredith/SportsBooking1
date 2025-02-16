package com.example.sportsbooking.detailtransaksi

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore

class DetailTransaksiActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var category: String
    private lateinit var court: String
    private lateinit var bookingDate: String
    private lateinit var timeSlot: String
    private var currentRating: Int = 0
    private lateinit var stars: Array<ImageView>
    private lateinit var feedbackEditText: EditText
    private lateinit var submitButton: AppCompatButton
    private lateinit var downloadButton: AppCompatButton
    private lateinit var detailCardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_transaksi)

        db = FirebaseFirestore.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar_detail_transaction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Transaksi"
        toolbar.setNavigationOnClickListener { onBackPressed() }

        category = intent.getStringExtra("category") ?: ""
        court = intent.getStringExtra("court") ?: ""
        bookingDate = intent.getStringExtra("bookingDate") ?: ""
        timeSlot = intent.getStringExtra("timeSlot") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val status = intent.getStringExtra("status") ?: ""

        findViewById<TextView>(R.id.text_booking_id).text = "Booking ID: ${generateBookingId(bookingDate, timeSlot)}"
        findViewById<TextView>(R.id.text_category).text = "Kategori: $category"
        findViewById<TextView>(R.id.text_court_name).text = "Lapangan: $court"
        findViewById<TextView>(R.id.text_date).text = "Tanggal: $bookingDate"
        findViewById<TextView>(R.id.text_time).text = "Waktu: $timeSlot"
        findViewById<TextView>(R.id.text_username).text = "Nama: $username"
        val statusText = findViewById<TextView>(R.id.text_status)
        when (status.lowercase()) {
            "booked" -> statusText.text = "Aktif".also { statusText.setTextColor(getColor(android.R.color.holo_green_dark)) }
            "completed" -> statusText.text = "Selesai".also { statusText.setTextColor(getColor(android.R.color.holo_blue_dark)) }
            "cancelled" -> statusText.text = "Dibatalkan".also { statusText.setTextColor(getColor(android.R.color.holo_red_dark)) }
        }

        val star1 = findViewById<ImageView>(R.id.star_1)
        val star2 = findViewById<ImageView>(R.id.star_2)
        val star3 = findViewById<ImageView>(R.id.star_3)
        val star4 = findViewById<ImageView>(R.id.star_4)
        val star5 = findViewById<ImageView>(R.id.star_5)
        stars = arrayOf(star1, star2, star3, star4, star5)

        feedbackEditText = findViewById(R.id.edit_text_feedback)
        submitButton = findViewById(R.id.button_submit_feedback)
        downloadButton = findViewById(R.id.button_download_detail)
        detailCardView = findViewById(R.id.detail_card_view)

        fetchRatingFromFirebase()

        for (i in 0 until stars.size) {
            stars[i].setOnClickListener {
                val ratingValue = i + 1
                setRating(ratingValue)
            }
        }

        submitButton.setOnClickListener {
            val feedbackText = feedbackEditText.text.toString()
            saveRatingAndFeedbackToFirebase(currentRating, feedbackText)
        }

        downloadButton.setOnClickListener {
            val intent = android.content.Intent(this, PdfPreviewActivity::class.java).apply {
                putExtra("bookingId", generateBookingId(bookingDate, timeSlot))
                putExtra("category", category)
                putExtra("court", court)
                putExtra("bookingDate", bookingDate)
                putExtra("timeSlot", timeSlot)
                putExtra("username", username)
                putExtra("status", statusText.text.toString())
                putExtra("rating", currentRating.toString())
                putExtra("feedback", feedbackEditText.text.toString())
            }
            startActivity(intent)
        }
    }

    private fun generateBookingId(date: String, timeSlot: String): String {
        val cleanDate = date.replace(Regex("[^A-Za-z0-9]"), "")
        val cleanTime = timeSlot.replace(Regex("[^A-Za-z0-9]"), "")
        return "BK-$cleanDate-$cleanTime"
    }

    private fun setRating(rating: Int) {
        currentRating = rating
        for (i in 0 until 5) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_border)
            }
        }
    }

    private fun fetchRatingFromFirebase() {
        val bookingDateDocRef = db.collection("sports_center")
            .document(category)
            .collection("courts")
            .document(court)
            .collection("bookings")
            .document(bookingDate)

        bookingDateDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val bookingData = document.data
                if (bookingData != null && bookingData.containsKey(timeSlot)) {
                    val fetchedRating = (bookingData[timeSlot] as? Map<*, *>)?.get("rating") as? Long ?: 0
                    val fetchedFeedback = (bookingData[timeSlot] as? Map<*, *>)?.get("feedback") as? String ?: ""
                    setRating(fetchedRating.toInt())
                    feedbackEditText.setText(fetchedFeedback)
                } else {
                    Log.w("DetailTransaksiActivity", "Time slot not found while fetching rating.")
                }
            } else {
                Log.w("DetailTransaksiActivity", "Booking date document not found while fetching rating.")
            }
        }.addOnFailureListener { e ->
            Log.e("DetailTransaksiActivity", "Error fetching rating", e)
        }
    }

    private fun saveRatingAndFeedbackToFirebase(rating: Int, feedback: String) {
        val bookingDateDocRef = db.collection("sports_center")
            .document(category)
            .collection("courts")
            .document(court)
            .collection("bookings")
            .document(bookingDate)

        bookingDateDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val bookingData = document.data
                if (bookingData != null && bookingData.containsKey(timeSlot)) {
                    val updates = hashMapOf<String, Any>(
                        "$timeSlot.rating" to rating,
                        "$timeSlot.feedback" to feedback
                    )
                    bookingDateDocRef.update(updates)
                        .addOnSuccessListener {
                            Log.d("DetailTransaksiActivity", "Rating and feedback saved successfully: $rating stars, feedback: $feedback")
                            Toast.makeText(this, "Rating dan feedback berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("DetailTransaksiActivity", "Error saving rating and feedback", e)
                            Toast.makeText(this, "Gagal menyimpan rating dan feedback.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.w("DetailTransaksiActivity", "Time slot not found while saving rating and feedback.")
                }
            } else {
                Log.w("DetailTransaksiActivity", "Booking date document not found while saving rating and feedback.")
            }
        }
    }
}