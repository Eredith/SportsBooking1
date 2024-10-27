// VenueListActivity.kt
package com.example.sportsbooking

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var venueList: List<Venue>
    private lateinit var filteredVenueList: List<Venue>

    // Formatter untuk menampilkan waktu
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_list)

        // Inisialisasi Spinner
        spinnerMonth = findViewById(R.id.spinner_month)
        setupSpinner()

        // Inisialisasi RecyclerView untuk Days dan Dates
        recyclerDays = findViewById(R.id.recycler_days)
        recyclerDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        daysList = getDaysData()
        daysAdapter = DaysAdapter(daysList)
        recyclerDays.adapter = daysAdapter

        // Inisialisasi RecyclerView untuk Categories
        recyclerCategory = findViewById(R.id.recycler_category)
        recyclerCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryList = getCategoryData()
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerCategory.adapter = categoryAdapter

        // Inisialisasi RecyclerView untuk Venue
        recyclerVenue = findViewById(R.id.recycler_venue)
        recyclerVenue.layoutManager = LinearLayoutManager(this)
        venueList = getVenueData()
        filteredVenueList = venueList
        // VenueListActivity.kt
// Pada bagian inisialisasi adapter
        venueAdapter = VenueAdapter(filteredVenueList, this)
        recyclerVenue.adapter = venueAdapter


        // Inisialisasi Time Selection TextViews
        textStartTime = findViewById(R.id.text_start_time)
        textEndTime = findViewById(R.id.text_end_time)

        // Set onClickListeners untuk memilih waktu
        textStartTime.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        textEndTime.setOnClickListener {
            showTimePicker(isStartTime = false)
        }

        // Inisialisasi Reset Filter
        val resetFilter: TextView = findViewById(R.id.reset_filter)
        resetFilter.setOnClickListener {
            resetFilters()
        }
    }

    private fun setupSpinner() {
        // Mendapatkan daftar bulan
        monthsList = getMonthList()

        // Membuat ArrayAdapter menggunakan layout bawaan atau custom
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, // Anda bisa menggunakan layout custom jika diinginkan
            monthsList
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Mengatur adapter ke Spinner
        spinnerMonth.adapter = adapter

        // Menentukan bulan saat ini sebagai default
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) // 0-based index
        spinnerMonth.setSelection(currentMonth)

        // Menangani event seleksi pada Spinner
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMonth = monthsList[position]
                Toast.makeText(this@VenueListActivity, "Bulan yang dipilih: $selectedMonth", Toast.LENGTH_SHORT).show()

                // Memfilter data berdasarkan bulan yang dipilih
                filterDataByMonth(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tindakan jika tidak ada item yang dipilih
            }
        }
    }

    private fun getMonthList(): List<String> {
        // Menggunakan DateFormatSymbols untuk mendapatkan nama bulan
        return DateFormatSymbols().months.filter { it.isNotEmpty() }
    }

    // Sample data untuk days dan dates
    private fun getDaysData(): List<Day> {
        return listOf(
            Day("Mon", 25, "Januari"),
            Day("Tue", 26, "Februari"),
            Day("Wed", 27, "Maret"),
            Day("Thu", 28, "April"),
            Day("Fri", 29, "Mei"),
            Day("Sat", 30, "Juni"),
            Day("Sun", 31, "Juli")
            // Tambahkan hari lain sesuai kebutuhan
        )
    }

    // Sample data untuk categories
    private fun getCategoryData(): List<Category> {
        return listOf(
            Category("Football", R.drawable.ic_football, "Januari"),
            Category("Basketball", R.drawable.ic_basketball, "Februari"),
            Category("Tennis", R.drawable.ic_tennis, "Maret"),
            Category("Swimming", R.drawable.ic_swimming, "April")
            // Tambahkan kategori lainnya sesuai kebutuhan
        )
    }

    // Sample data untuk venue dengan time parameter
    private fun getVenueData(): List<Venue> {
        // Venue 1: ASIOP Stadium
        val asioStartTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)   // 08:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val asioEndTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)  // 22:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Venue 2: Stadion Gelora
        val geloraStartTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)   // 06:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val geloraEndTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)  // 20:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Venue 3: Lapangan Merdeka
        val merdekaStartTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val merdekaEndTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Venue 4: Lapangan Sportiva
        val sportivaStartTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val sportivaEndTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return listOf(
            Venue(
                name = "ASIOP Stadium",
                price = "Rp. 1.200.000",
                location = "Jakarta",
                category = "Sepak Bola",
                capacity = 120,
                imageResId = R.drawable.venue_image,
                availableStartTime = asioStartTime,
                availableEndTime = asioEndTime
            ),
            Venue(
                name = "Stadion Gelora",
                price = "Rp. 900.000",
                location = "Surabaya",
                category = "Sepak Bola",
                capacity = 80,
                imageResId = R.drawable.venue_image,
                availableStartTime = geloraStartTime,
                availableEndTime = geloraEndTime
            ),
            Venue(
                name = "Lapangan Merdeka",
                price = "Rp. 1.000.000",
                location = "Bandung",
                category = "Sepak Bola",
                capacity = 100,
                imageResId = R.drawable.venue_image,
                availableStartTime = merdekaStartTime,
                availableEndTime = merdekaEndTime
            ),
            Venue(
                name = "Lapangan Sportiva",
                price = "Rp. 800.000",
                location = "Bali",
                category = "Basketball",
                capacity = 90,
                imageResId = R.drawable.venue_image,
                availableStartTime = sportivaStartTime,
                availableEndTime = sportivaEndTime
            )
            // Tambahkan venue lainnya sesuai kebutuhan
        )
    }

    // Fungsi untuk memfilter data berdasarkan bulan yang dipilih
    private fun filterDataByMonth(month: String) {
        // Filter berdasarkan bulan
        val filteredDays = daysList.filter { it.month.equals(month, ignoreCase = true) }
        daysAdapter.updateData(filteredDays)

        val filteredCategories = categoryList.filter { it.month.equals(month, ignoreCase = true) }
        categoryAdapter.updateData(filteredCategories)

        // Setelah filter bulan, filter berdasarkan waktu jika sudah dipilih
        if (selectedStartTime != null && selectedEndTime != null) {
            filterVenueByTime()
        } else {
            // Jika waktu tidak dipilih, tampilkan semua venue
            venueAdapter.updateData(venueList)
        }
    }

    // Fungsi untuk memfilter data berdasarkan waktu yang dipilih
    private fun filterVenueByTime() {
        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Silakan pilih waktu mulai dan selesai", Toast.LENGTH_SHORT).show()
            return
        }

        // Pastikan Start Time sebelum End Time
        if (selectedStartTime!!.after(selectedEndTime)) {
            Toast.makeText(this, "Waktu mulai harus sebelum waktu selesai", Toast.LENGTH_SHORT).show()
            return
        }

        // Filter venue berdasarkan waktu yang dipilih
        filteredVenueList = venueList.filter { venue ->
            // Venue tersedia jika waktu mulai venue <= waktu mulai yang dipilih
            // dan waktu selesai venue >= waktu selesai yang dipilih
            venue.availableStartTime <= selectedStartTime && venue.availableEndTime >= selectedEndTime
        }

        if (filteredVenueList.isNotEmpty()) {
            venueAdapter.updateData(filteredVenueList)
        } else {
            Toast.makeText(this, "Tidak ada venue tersedia untuk waktu yang dipilih", Toast.LENGTH_SHORT).show()
            // Optionally, you can reset the adapter to show all venues or show an empty view
        }
    }

    // Fungsi untuk menampilkan TimePicker
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                selectedTime.set(Calendar.SECOND, 0)
                selectedTime.set(Calendar.MILLISECOND, 0)

                if (isStartTime) {
                    selectedStartTime = selectedTime
                    textStartTime.text = "Mulai: ${timeFormatter.format(selectedTime.time)}"
                } else {
                    selectedEndTime = selectedTime
                    textEndTime.text = "Selesai: ${timeFormatter.format(selectedTime.time)}"
                }

                // Memfilter data berdasarkan waktu yang dipilih
                filterVenueByTime()
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        picker.show()
    }

    // Fungsi untuk mereset filter
    private fun resetFilters() {
        // Reset Spinner ke bulan saat ini
        spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH))

        // Reset waktu
        selectedStartTime = null
        selectedEndTime = null
        textStartTime.text = getString(R.string.select_start_time)
        textEndTime.text = getString(R.string.select_end_time)

        // Reset RecyclerViews
        daysAdapter.updateData(getDaysData())
        categoryAdapter.updateData(getCategoryData())
        venueAdapter.updateData(venueList)

        Toast.makeText(this, "Filter direset", Toast.LENGTH_SHORT).show()
    }
}

