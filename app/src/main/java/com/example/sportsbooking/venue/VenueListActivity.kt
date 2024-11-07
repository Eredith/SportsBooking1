package com.example.sportsbooking.venue

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.MainActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.days.Day
import com.example.sportsbooking.days.DaysAdapter
import com.example.sportsbooking.profile.ProfileActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VenueListActivity : AppCompatActivity() {

    private lateinit var recyclerDays: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private var daysList: List<Day> = listOf()

    private lateinit var recyclerCategory: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryList: List<Category> = listOf()
    private var filteredCategories: List<Category> = listOf()

    private lateinit var spinnerMonth: Spinner
    private var monthsList: List<String> = listOf()

    private lateinit var textStartTime: TextView
    private lateinit var textEndTime: TextView
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null

    private lateinit var recyclerVenue: RecyclerView
    private lateinit var venueAdapter: VenueAdapter
    private var venueList: MutableList<Venue> = mutableListOf()
    private var filteredVenueList: MutableList<Venue> = mutableListOf()

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
        daysAdapter = DaysAdapter(daysList) { day -> onDateSelected(day) }
        recyclerDays.adapter = daysAdapter

        // Initialize RecyclerView for Categories
        recyclerCategory = findViewById(R.id.recycler_category)
        recyclerCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryList = getCategoryData()
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            Toast.makeText(this, "Selected: ${category.name}", Toast.LENGTH_SHORT).show()
            filterVenuesByCategory(category.name)
        }
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

        setupBottomNavigation()
    }

    private fun filterVenuesByCategory(categoryName: String) {
        val normalizedCategoryName = when (categoryName) {
            "Bulu Tangkis" -> "Badminton"
            "General Sports" -> "General Sports"
            "Driving Range" -> "Driving Range"
            else -> categoryName
        }

        filteredVenueList = venueList.filter { it.category.equals(normalizedCategoryName, ignoreCase = true) }.toMutableList()
        venueAdapter.updateData(filteredVenueList)
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
                filterDataByMonth(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getMonthList(): List<String> {
        return DateFormatSymbols(Locale("id", "ID")).months.filter { it.isNotEmpty() }
    }

    private fun getDaysData(): List<Day> {
        return listOf(
            Day("Mon", 25, "January"),
            Day("Tue", 26, "January"),
            Day("Wed", 27, "January"),
            Day("Thu", 28, "February"),
            Day("Fri", 29, "February"),
            Day("Sat", 30, "March"),
            Day("Sun", 31, "March")
        )
    }

    private fun filterDataByMonth(month: String) {
        val filteredDays = daysList.filter { it.month.equals(month, ignoreCase = true) }
        daysAdapter.updateData(filteredDays)

        filteredCategories = categoryList.filter { it.month.equals(month, ignoreCase = true) }
        categoryAdapter.updateData(filteredCategories)

        if (selectedStartTime != null && selectedEndTime != null) {
            filterVenueByTime()
        } else {
            venueAdapter.updateData(venueList)
        }
    }

    private fun fetchVenuesFromFirestore() {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("sports_center")
            .document("badminton")
            .collection("courts")
            .get()
            .addOnSuccessListener { result ->
                venueList.clear()
                for (document in result) {
                    val venue = document.toObject(Venue::class.java)

                    // Retrieving date fields directly from document
                    venue.availableStartTime = document.getDate("availableStartTime")
                    venue.availableEndTime = document.getDate("availableEndTime")

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

    private fun getCategoryData(): List<Category> {
        val months = DateFormatSymbols(Locale("id", "ID")).months.filter { it.isNotEmpty() }
        val categoryData = mutableListOf<Category>()

        months.forEach { month ->
            when (month) {
                "Januari" -> categoryData.add(Category("Football", R.drawable.ic_football, month))
                "Februari" -> categoryData.add(Category("Basketball", R.drawable.ic_basketball, month))
                "Maret" -> categoryData.add(Category("Tennis", R.drawable.ic_tennis, month))
                "April" -> categoryData.add(Category("Swimming", R.drawable.ic_swimming, month))
                else -> categoryData.add(Category("General Sports", R.drawable.ic_football, month))
            }
            categoryData.add(Category("Bulu Tangkis", R.drawable.shuttlecock, month))
            categoryData.add(Category("Driving Range", R.drawable.golf, month))
        }

        return categoryData
    }

    private fun filterVenueByTime() {
        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime!!.after(selectedEndTime)) {
            Toast.makeText(this, "Start time must be before end time", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedStartDate: Date? = selectedStartTime?.time
        val selectedEndDate: Date? = selectedEndTime?.time

        filteredVenueList = venueList.filter { venue ->
            venue.availableStartTime != null && venue.availableEndTime != null &&
                    venue.availableStartTime!! <= selectedStartDate!! &&
                    venue.availableEndTime!! >= selectedEndDate!!
        }.toMutableList()

        venueAdapter.updateData(filteredVenueList)
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
                    textStartTime.text = "Start: ${timeFormatter.format(selectedTime.time)}"
                } else {
                    selectedEndTime = selectedTime
                    textEndTime.text = "End: ${timeFormatter.format(selectedTime.time)}"
                }

                if (selectedStartTime != null && selectedEndTime != null) {
                    filterVenueByTime()
                }
            },
            currentHour,
            currentMinute,
            true
        )
        picker.show()
    }

    private fun resetFilters() {
        selectedStartTime = null
        selectedEndTime = null
        textStartTime.text = "Start Time"
        textEndTime.text = "End Time"

        filteredVenueList.clear()
        filteredVenueList.addAll(venueList)
        venueAdapter.updateData(filteredVenueList)
    }

    private fun onDateSelected(day: Day) {
        Toast.makeText(this, "Selected Date: ${day.date} ${day.month}", Toast.LENGTH_SHORT).show()
    }
    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}