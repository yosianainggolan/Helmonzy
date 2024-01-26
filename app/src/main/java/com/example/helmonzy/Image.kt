package com.example.helmonzy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Image : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageArrayList: ArrayList<Images>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // inflater masuk variable karena ada perubahan pada image**
        val view = inflater.inflate(R.layout.fragment_image, container, false)

        // Dapatkan referensi ke TextView
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        // Dapatkan tanggal saat ini
        val currentDate = Date()

        // Set Locale ke Bahasa Indonesia
        val locale = Locale("id", "ID") // "id" untuk bahasa Indonesia, "ID" untuk Indonesia
        Locale.setDefault(locale)

        // Format tanggal dan hari sesuai dengan format "Hari, DD Month YYYY"
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale) //Locale.getDefault() jika mengikuti bahasa sistem
        val formattedDate = dateFormat.format(currentDate)

        // Atur tanggal dan hari dalam TextView
        dateTextView.text = formattedDate

        // Mengatur button agar masuk ke database
        val openDatabaseButton = view.findViewById<Button>(R.id.openDatabaseButton)
        val openDatabaseButton2 = view.findViewById<Button>(R.id.openDatabaseButton2)

        // Tambahkan OnClickListener untuk tombol
        openDatabaseButton.setOnClickListener {
            openDatabaseFragment() // Panggil fungsi untuk membuka FragmentDatabase
        }

        openDatabaseButton2.setOnClickListener{
            var selectedDate = Calendar.getInstance()
            val bundle = Bundle()
            bundle.putSerializable("selectedDate", selectedDate)

            val fragment = Database2()
            fragment.arguments = bundle
            openDatabaseFragment2(fragment)
        }

        //RecyclerView
        imageRecyclerView = view.findViewById(R.id.recyclerView)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageRecyclerView.setHasFixedSize(true)

        //Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
        imageArrayList= arrayListOf<Images>()
        getLastImage()

        return view
    }

    //Fungsi button 'Lihat Database'
    private fun openDatabaseFragment() {
        val fragment = Database() // Menuju fragment Database
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null) // Untuk dapat kembali ke Fragment sebelumnya
        fragmentTransaction.commit()
    }

    //Fungsi button 'Lihat Pelanggar Hari Ini'
    private fun openDatabaseFragment2(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null) // Untuk dapat kembali ke Fragment sebelumnya
        fragmentTransaction.commit()
    }

    //Mendapatkan data gambar terakhir
    private fun getLastImage(){
        dbref = FirebaseDatabase.getInstance().getReference("images")

        dbref.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(object :
            ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //Saat sudah ada data pelanggar
                    for (userSnapshot in snapshot.children){

                        val selectedDate = arguments?.getSerializable("selectedDate") as Calendar?

                        val image = userSnapshot.getValue(Images::class.java)
                        val theDate = view?.findViewById<TextView>(R.id.dateTextView)
                        theDate?.text = formatDate2(image?.date)

                        imageArrayList.add(image!!)
                    }

                    imageRecyclerView.adapter = MyAdapter(imageArrayList)
                }else{
                    //Saat belum ada data pelanggar
                    val theDate = view?.findViewById<TextView>(R.id.dateTextView)
                    theDate?.text="Belum ada pelanggar"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                "Error"
            }
        })

    }

    // Mengubah format tanggal input menjadi format output yang baru dalam String
    private fun formatDate2(string: String?): String {
        val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

        try {
            val date = inputDateFormat.parse(string)
            if (date != null) {
                return outputDateFormat.format(date)
            }
        } catch (e: ParseException) {
            // Penanganan kesalahan jika format string tidak valid
        }

        return "Tanggal Tidak Tersedia" // return jika terjadi pengecualian
    }
}





