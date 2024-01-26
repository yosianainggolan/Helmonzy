package com.example.helmonzy

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Stats : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var dbref: DatabaseReference
    private lateinit var imageArrayList: ArrayList<Images>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
        imageArrayList = arrayListOf<Images>()
        getImageData()


        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        barChart = view.findViewById(R.id.barChart)

        // Inisialisasi dan setel data grafik
        setupBarChart4()

        return view
    }


    private fun getImageData() {
        dbref = FirebaseDatabase.getInstance().getReference("images")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var count = 0
                    for (userSnapshot in snapshot.children) {

                        val image = userSnapshot.getValue(Images::class.java)
                        count++
                        imageArrayList.add(image!!)
                    }
                    val numberTotals = view?.findViewById<TextView>(R.id.numberTotal)
                    numberTotals?.text = count!!.toString()

                } else {
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setupBarChart4() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dbref = FirebaseDatabase.getInstance().getReference("images")
        val entries = ArrayList<BarEntry>()
        var daysOfWeek = ArrayList<String>()

        // Mengambil data dari Firebase
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //Mencegah penumpukan data pada bar chart dengan membersihkan data sebelum menambahkan yang baru
                entries.clear()
                daysOfWeek.clear()

                if (snapshot.exists()) {
                    //Saat sudah ada data pelanggar
                    for (i in 6 downTo 0) {
                        // Iterasi dipisah karena tipe data Calendar tidak dapat membaca nilai negatif pada i

                        //Iterasi untuk tanggal pada sumbu X [-6 -> 0] - pembacaan ke kanan
                        var date = currentDate.clone() as Calendar
                        date.add(Calendar.DAY_OF_YEAR, -i)

                        //Iterasi untuk posisi Bar Chart [0 -> -6] - pembacaan ke kiri
                        var date2= currentDate.clone() as Calendar
                        date2.add(Calendar.DAY_OF_YEAR, i-6)

                        // Menghitung pelanggar
                        var count = 0
                        for (userSnapshot in snapshot.children) {
                            val image = userSnapshot.getValue(Images::class.java)
                            if (image?.date == formatDate2(date2)) {
                                count++
                            }
                        }

                        val dataValue = count.toFloat()

                        //val dataValue = violationsCount.toFloat()
                        entries.add(BarEntry(i.toFloat(), dataValue))

                        var formattedDate =
                            dateFormat.format(date.time) // Mengubah tanggal yang telah diubah menjadi format yang diinginkan (misalnya, "dd/MM/yyyy")
                        daysOfWeek.add(formattedDate) // Menambahkan tanggal yang telah diformat ke dalam daftar 'daysOfWeek'

                    }
                    updateBarChart(entries, daysOfWeek)
                } else {
                    //Saat belum ada data pelanggar
                    for (i in 0..6) {
                        var date = currentDate.clone() as Calendar
                        date.add(Calendar.DAY_OF_YEAR, -i)
                        var formattedDate =
                            dateFormat.format(date.time) // Mengubah tanggal yang telah diubah menjadi format yang diinginkan (misalnya, "dd/MM/yyyy")
                        daysOfWeek.add(formattedDate) // Menambahkan tanggal yang telah diformat ke dalam daftar 'daysOfWeek'

                        val violationsCount = 0
                        val dataValue = violationsCount.toFloat()
                        entries.add(BarEntry(i.toFloat(), dataValue))
                    }
                    updateBarChart(entries, daysOfWeek)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                "Error"
            }
        })
    }

    private fun updateBarChart(entries: ArrayList<BarEntry>, daysOfWeek: ArrayList<String>) {
        val dataSet = BarDataSet(entries, "Jumlah Pelanggar")
        dataSet.color = Color.BLACK //Warna Bar Chart
        dataSet.valueTextColor = Color.RED //Warna teks nilai grafik
        dataSet.valueTextSize = 20f // Ukuran teks dalam satuan sp

        //Mengubah nilai di atas bar chart dari float menjadi integer
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Konversi nilai float menjadi integer dan tampilkan sebagai string
                return value.toInt().toString()
            }
        }

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(dataSet)

        val data = BarData(dataSets)
        data.barWidth = 0.7f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.invalidate()

        // Konfigurasi lainnya seperti label, legenda, dsb.
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setTouchEnabled(false)
        barChart.isDragEnabled = false
        barChart.setScaleEnabled(false)

        val xAxis = barChart.xAxis
        xAxis?.setLabelCount(7)
        xAxis?.valueFormatter = IndexAxisValueFormatter(daysOfWeek)
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.textSize = 10f
        xAxis?.labelRotationAngle = 0f
        xAxis?.setDrawGridLines(false)

        barChart.axisLeft.isEnabled = false // Menghilangkan sumbu Y kiri
        barChart.axisRight.isEnabled = false // Menghilangkan sumbu Y kanan
        val minimumValue = 0f
        val rightAxis: YAxis =
            barChart.axisRight
        val leftAxis: YAxis =
            barChart.axisLeft
        leftAxis.axisMinimum = minimumValue
        rightAxis.axisMinimum = minimumValue


        // Tampilkan jumlah total pelanggar selama 7 hari terakhir
        val totalViolations = entries.sumOf { it.y.toInt() } // Menghitung total pelanggaran
        val totalTextView = view?.findViewById<TextView>(R.id.numberTotal2)
        totalTextView!!.text = totalViolations!!.toString() + " Pelanggar"

        barChart.invalidate() // Memperbarui tampilan grafik secara paksa
    }

    private fun formatDate2(calendar: Calendar?): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return if (calendar != null) {
            dateFormat.format(calendar.time)
        } else {
            "Tanggal Tidak Tersedia"
        }
    }
}



