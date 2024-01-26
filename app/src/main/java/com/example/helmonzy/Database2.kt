package com.example.helmonzy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Database2 : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageArrayList: ArrayList<Images>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_database2, container, false)

        // Dapatkan tanggal yang dipilih dari argumen
        val selectedDate = arguments?.getSerializable("selectedDate") as Calendar?

        // Menampilkan tanggal yang dipilih di TextView
        val selectedDateTextView = view.findViewById<TextView>(R.id.dateTextView)
        selectedDateTextView.text = formatDate(selectedDate)

        // RecyclerView
        imageRecyclerView = view.findViewById(R.id.recyclerView)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageRecyclerView.setHasFixedSize(true)

        // Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
        imageArrayList= arrayListOf<Images>()
        getImageData()

        return view
    }

    // Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
    private fun getImageData() {
        dbref = FirebaseDatabase.getInstance().getReference("images")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){

                        val selectedDate = arguments?.getSerializable("selectedDate") as Calendar?

                        val image = userSnapshot.getValue(Images::class.java)

                        // Memeriksa apakah gambar dengan timestamp tertentu sudah ada di dalam imageArrayList
                        val isImageAlreadyAddedTimestamp = imageArrayList.any { it.timestamp == image?.timestamp }

                        // Memperoleh gambar dengan filter tanggal
                        if (image!!.date == formatDate2(selectedDate)) {

                            // Menghindari doubling
                            if (!isImageAlreadyAddedTimestamp) {
                                imageArrayList.add(image!!)
                            } else {
                            }
                            count++
                        }
                    }
                    imageRecyclerView.adapter = MyAdapter(imageArrayList)
                }else{}

                // Mengurutkan array berdasarkan timestamp secara descending
                imageArrayList.sortByDescending {it.timestamp}

                // Mendapatkan id dari numberTotal2 yang berupa TextView
                val numberTotal = view?.findViewById<TextView>(R.id.numberTotal2)

                // Membuat teks pada TextView sesuai dengan jumlah gambar
                if (numberTotal != null) {
                    numberTotal.text = count!!.toString() + " Pelanggar"
                }
            }

            // Mengatasi error
            override fun onCancelled(error: DatabaseError) {
                "Error"
            }
        })
    }

    private fun formatDate(calendar: Calendar?): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return if (calendar != null) {
            dateFormat.format(calendar.time)
        } else {
            "Tanggal Tidak Tersedia"
        }
    }

    // Menyamakan format Date dari input dan database
    private fun formatDate2(calendar: Calendar?): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return if (calendar != null) {
            dateFormat.format(calendar.time)
        } else {
            "Tanggal Tidak Tersedia"
        }
    }

}