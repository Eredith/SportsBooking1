package com.example.sportsbooking.detailtransaksi

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sportsbooking.R
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfPreviewActivity : AppCompatActivity() {

    // Data dari Intent
    private lateinit var bookingId: String
    private lateinit var category: String
    private lateinit var court: String
    private lateinit var bookingDate: String
    private lateinit var timeSlot: String
    private lateinit var username: String
    private lateinit var status: String
    private var rating: Int = 0
    private lateinit var feedback: String

    // Style Constants
    private val primaryColor = ColorConstants.BLUE
    private val accentColor = ColorConstants.DARK_GRAY
    private val headerFontSize = 16f
    private val bodyFontSize = 10f

    companion object {
        private const val STORAGE_PERMISSION_CODE = 102
        private const val BASE_PRICE = 150000 // Harga dasar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_preview)

        // Setup Toolbar
        findViewById<Toolbar>(R.id.toolbar_pdf_preview).apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = "Preview Invoice"
            setNavigationOnClickListener { onBackPressed() }
        }

        // Ambil data dari Intent
        getIntentData()

        // Setup Tombol Download
        findViewById<Button>(R.id.button_download_pdf).setOnClickListener {
            checkStoragePermission()
        }
    }

    private fun getIntentData() {
        bookingId = intent.getStringExtra("bookingId") ?: ""
        category = intent.getStringExtra("category") ?: ""
        court = intent.getStringExtra("court") ?: ""
        bookingDate = intent.getStringExtra("bookingDate") ?: ""
        timeSlot = intent.getStringExtra("timeSlot") ?: ""
        username = intent.getStringExtra("username") ?: ""
        status = intent.getStringExtra("status") ?: ""
        rating = intent.getStringExtra("rating")?.toIntOrNull() ?: 0
        feedback = intent.getStringExtra("feedback") ?: ""
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            generateAndSavePdf()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                generateAndSavePdf()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            generateAndSavePdf()
        } else {
            Toast.makeText(
                this,
                "Izin penyimpanan diperlukan untuk menyimpan PDF",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun generateAndSavePdf() {
        val pdfFile = generatePdf()
        if (pdfFile != null) {
            saveToMediaStore(pdfFile)
            Toast.makeText(this, "PDF berhasil disimpan di Downloads", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal membuat PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generatePdf(): File? {
        return try {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "SportsInvoices"
            )
            dir.mkdirs()

            val fileName = "Invoice_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val pdfFile = File(dir, fileName)

            PdfDocument(PdfWriter(pdfFile)).use { pdfDocument ->
                Document(pdfDocument).apply {
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    setFontSize(bodyFontSize)

                    // Header
                    add(createHeader())

                    // Informasi Pelanggan
                    add(createCustomerInfoSection())

                    // Detail Transaksi
                    add(createTransactionDetails())

                    // Ringkasan Pembayaran
                    add(createPaymentSummary())

                    // Footer
                    add(createFooter())
                }
            }
            pdfFile
        } catch (e: Exception) {
            Log.e("PDF_ERROR", "Error generating PDF: ${e.message}")
            null
        }
    }

    private fun createHeader(): Table {
        return Table(floatArrayOf(1f)).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setMarginBottom(15f)

            addCell(Cell().apply {
                add(Paragraph("SPORTS BOOKING CENTER")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontColor(primaryColor)
                    .setFontSize(headerFontSize)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER))
            })

            addCell(Cell().apply {
                add(Paragraph("Invoice: $bookingId")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER))
            })
        }
    }

    private fun createCustomerInfoSection(): Table {
        return Table(2).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setMarginTop(10f)
            setMarginBottom(20f)

            addStyledCell("Tanggal Invoice", formatDate(Date()))
            addStyledCell("Nama Pelanggan", username)
            addStyledCell("Status Pembayaran", status)
            addStyledCell("Rating Pengguna", convertRatingToStars())
        }
    }

    private fun createTransactionDetails(): Table {
        return Table(floatArrayOf(3f, 2f, 2f, 2f)).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setMarginBottom(15f)

            // Header
            addHeaderCell(createTableHeaderCell("Deskripsi"))
            addHeaderCell(createTableHeaderCell("Kategori"))
            addHeaderCell(createTableHeaderCell("Waktu"))
            addHeaderCell(createTableHeaderCell("Harga"))

            // Konten
            addContentCell(court)
            addContentCell(category)
            addContentCell(timeSlot)
            addContentCell(formatCurrency(BASE_PRICE))
        }
    }

    private fun createPaymentSummary(): Table {
        val tax = (BASE_PRICE * 0.1).toInt()
        val total = BASE_PRICE + tax

        return Table(2).apply {
            setWidth(UnitValue.createPercentValue(40f))
            setHorizontalAlignment(HorizontalAlignment.RIGHT)
            setMarginBottom(20f)

            addPaymentRow("Subtotal", BASE_PRICE)
            addPaymentRow("Pajak (10%)", tax)
            addPaymentRow("Total", total, true)

            addCell(Cell(1, 2).apply {
                add(Paragraph("Terbilang: ${NumberToWords.convert(total.toLong())} RUPIAH")
                    .setFontSize(9f)
                    .setFontColor(accentColor)
                    .setBorder(Border.NO_BORDER))
            })
        }
    }

    private fun createFooter(): Paragraph {
        return Paragraph().apply {
            add("\n\nKritik dan Saran:\n$feedback\n\n")
            add("--------------------------------\n")
            add("Powered by Sports Booking App\n")
            add("Jl. Olahraga No. 123, Kota Sportif\n")
            add("Telp: (021) 555-1234 | Email: info@sportsbooking.com")
                .setFontSize(8f)
                .setFontColor(accentColor)
                .setTextAlignment(TextAlignment.CENTER)
        }
    }

    // Helper Functions
    private fun Table.addStyledCell(label: String, value: String) {
        addCell(createCell(label, true).setBackgroundColor(ColorConstants.LIGHT_GRAY))
        addCell(createCell(value))
    }

    private fun createTableHeaderCell(text: String): Cell {
        return Cell().apply {
            add(Paragraph(text).setBold())
            setBackgroundColor(primaryColor)
            setFontColor(ColorConstants.WHITE)
            setTextAlignment(TextAlignment.CENTER)
            setPadding(5f)
        }
    }

    private fun Table.addContentCell(text: String) {
        addCell(createCell(text).setTextAlignment(TextAlignment.LEFT))
    }

    private fun Table.addPaymentRow(label: String, amount: Int, isTotal: Boolean = false) {
        addCell(createCell(label, isTotal).setTextAlignment(TextAlignment.RIGHT))
        addCell(createCell(formatCurrency(amount), isTotal).setTextAlignment(TextAlignment.RIGHT))
    }

    private fun createCell(text: String, isHeader: Boolean = false): Cell {
        return Cell().apply {
            add(Paragraph(text).apply {
                if (isHeader) setBold()
            })
            setPadding(5f)
            setBorder(Border.NO_BORDER)
        }
    }

    private fun formatCurrency(amount: Int): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
    }

    private fun convertRatingToStars(): String {
        return "★".repeat(rating) + "☆".repeat(5 - rating)
    }

    private fun saveToMediaStore(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SportsInvoices")
            }

            contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)?.let { uri ->
                contentResolver.openOutputStream(uri).use { output ->
                    file.inputStream().copyTo(output!!)
                }
            }
        }
    }
}

object NumberToWords {
    private val units = arrayOf(
        "", "SATU", "DUA", "TIGA", "EMPAT", "LIMA", "ENAM", "TUJUH", "DELAPAN", "SEMBILAN",
        "SEPULUH", "SEBELAS", "DUA BELAS", "TIGA BELAS", "EMPAT BELAS", "LIMA BELAS",
        "ENAM BELAS", "TUJUH BELAS", "DELAPAN BELAS", "SEMBILAN BELAS"
    )

    fun convert(n: Long): String {
        return when {
            n < 20 -> units[n.toInt()]
            n < 100 -> "${units[(n / 10).toInt() + 10]} ${convert(n % 10)}".trim()
            n < 200 -> "SERATUS ${convert(n % 100)}".trim()
            n < 1000 -> "${units[(n / 100).toInt()]} RATUS ${convert(n % 100)}".trim()
            n < 2000 -> "SERIBU ${convert(n % 1000)}".trim()
            n < 1000000 -> "${convert(n / 1000)} RIBU ${convert(n % 1000)}".trim()
            n < 1000000000 -> "${convert(n / 1000000)} JUTA ${convert(n % 1000000)}".trim()
            else -> "Nilai terlalu besar"
        }.replace("\\s+".toRegex(), " ")
    }
}