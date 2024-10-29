package com.example.sportsbooking

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class VenueListActivity : AppCompatActivity() {

    private lateinit var recyclerDays: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var daysList: List<Day>

    private lateinit var recyclerCategory: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryList: List<Category>

    private lateinit var spinnerMonth: Spinner
    private lateinit var monthsList: List<String>

    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null

    private lateinit var recyclerVenue: RecyclerView
    private lateinit var venueAdapter: VenueAdapter
    private var venueList: MutableList<Venue> = mutableListOf()
    private var filteredVenueList: MutableList<Venue> = mutableListOf() // Changed to var

    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_list)

        // Initialize Spinner
        spinnerMonth = findViewById(R.id.spinner_month)
        setupSpinner()

        // Initialize RecyclerView for Days and Dates
        recyclerDays = findViewById(R.id.recycler_days)
        recyclerDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        daysList = getDaysData()
        daysAdapter = DaysAdapter(daysList)
        recyclerDays.adapter = daysAdapter

        // Initialize RecyclerView for Categories
        recyclerCategory = findViewById(R.id.recycler_category)
        recyclerCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryList = getCategoryData()
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerCategory.adapter = categoryAdapter

        // Initialize RecyclerView for Venue
        recyclerVenue = findViewById(R.id.recycler_venue)
        recyclerVenue.layoutManager = LinearLayoutManager(this)
        venueAdapter = VenueAdapter(filteredVenueList, this)
        recyclerVenue.adapter = venueAdapter

        // Initialize Time Selection TextViews
        textStartTime = findViewById(R.id.text_start_time)
        textEndTime = findViewById(R.id.text_end_time)

        // Set onClickListeners for time selection
        textStartTime.setOnClickListener { showTimePicker(isStartTime = true) }
        textEndTime.setOnClickListener { showTimePicker(isStartTime = false) }

        // Initialize Reset Filter
        val resetFilter: TextView = findViewById(R.id.reset_filter)
        resetFilter.setOnClickListener { resetFilters() }

        // Fetch venues from Firestore
        fetchVenuesFromFirestore()
    }

    private fun setupSpinner() {
        monthsList = getMonthList()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            monthsList
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerMonth.adapter = adapter
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        spinnerMonth.setSelection(currentMonth)

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = monthsList[position]
                Toast.makeText(this@VenueListActivity, "Bulan yang dipilih: $selectedMonth", Toast.LENGTH_SHORT).show()
                filterDataByMonth(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getMonthList(): List<String> {
        return DateFormatSymbols().months.filter { it.isNotEmpty() }
    }

    private fun getDaysData(): List<Day> {
        return listOf(
            Day("Mon", 25, "Januari"),
            Day("Tue", 26, "Februari"),
            Day("Wed", 27, "Maret"),
            Day("Thu", 28, "April"),
            Day("Fri", 29, "Mei"),
            Day("Sat", 30, "Juni"),
            Day("Sun", 31, "Juli")
        )
    }

    private fun getCategoryData(): List<Category> {
        return listOf(
            Category("Football", R.drawable.ic_football, "Januari"),
            Category("Basketball", R.drawable.ic_basketball, "Februari"),
            Category("Tennis", R.drawable.ic_tennis, "Maret"),
            Category("Swimming", R.drawable.ic_swimming, "April")
        )
    }

    private fun fetchVenuesFromFirestore() {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("venues") // Make sure this matches your Firestore collection name
            .get()
            .addOnSuccessListener { result ->
                venueList.clear()
                for (document in result) {
                    val venue = document.toObject(Venue::class.java)


                    venue.availableStartTime = mapTimestampToCalendar(document.get("availableStartTime"))
                    venue.availableEndTime = mapTimestampToCalendar(document.get("availableEndTime"))

                    venueList.add(venue)
                }
                filteredVenueList.clear()
                filteredVenueList.addAll(venueList) // Set filtered list to all venues
                venueAdapter.updateData(venueList) // Notify adapter with initial data
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mapTimestampToCalendar(timestamp: Any?): Calendar? {
        return if (timestamp is com.google.firebase.Timestamp) {
            Calendar.getInstance().apply { time = timestamp.toDate() }
        } else {
            null
        }
    }

    private fun filterDataByMonth(month: String) {
        // Filter days and categories based on selected month
        val filteredDays = daysList.filter { it.month.equals(month, ignoreCase = true) }
        daysAdapter.updateData(filteredDays)

        val filteredCategories = categoryList.filter { it.month.equals(month, ignoreCase = true) }
        categoryAdapter.updateData(filteredCategories)

        // After filtering by month, filter venues by time if selected
        if (selectedStartTime != null && selectedEndTime != null) {
            filterVenueByTime()
        } else {
            venueAdapter.updateData(venueList)
        }
    }

    private fun filterVenueByTime() {
        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Silakan pilih waktu mulai dan selesai", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime!!.after(selectedEndTime)) {
            Toast.makeText(this, "Waktu mulai harus sebelum waktu selesai", Toast.LENGTH_SHORT).show()
            return
        }

        filteredVenueList = venueList.filter { venue ->
            venue.availableStartTime!! <= selectedStartTime && venue.availableEndTime!! >= selectedEndTime
        }.toMutableList() // Ensure it's mutable

        if (filteredVenueList.isNotEmpty()) {
            venueAdapter.updateData(filteredVenueList)
        } else {
            Toast.makeText(this, "Tidak ada venue tersedia untuk waktu yang dipilih", Toast.LENGTH_SHORT).show()
            // Optionally reset adapter to show all venues or show an empty view
        }
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(
            this,
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

                filterVenueByTime()
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        picker.show()
    }

    private fun resetFilters() {
        spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH))

        selectedStartTime = null
        selectedEndTime = null
        textStartTime.text = "Mulai"
        textEndTime.text = "Selesai"

        // Reset to show all venues
        venueAdapter.updateData(venueList)
    }
}
