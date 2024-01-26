package com.example.helmonzy

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Home : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref : DatabaseReference
    private lateinit var imageArrayList: ArrayList<Images>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // inflater masuk variable karena ada perubahan pada home**
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Dapatkan referensi ke TextView
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        // Dapatkan tanggal saat ini
        val currentDate = Date()

        // Set Locale ke Bahasa Indonesia
        val locale = Locale("id", "ID") // "id" untuk bahasa Indonesia, "ID" untuk Indonesia
        Locale.setDefault(locale)

        // Format tanggal dan hari sesuai dengan format "Hari, DD Month YYYY"
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        //Locale.getDefault() jika mengikuti bahasa sistem

        val formattedDate = dateFormat.format(currentDate)

        // Atur tanggal dan hari dalam TextView
        dateTextView.text = formattedDate


        // SIGN OUT
        // Inisialisasi FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Mengambil referensi ke tombol logout (misalnya, buttonLogout)
        val iconLogout = view.findViewById<ImageView>(R.id.iconLogout)

        // Menambahkan onClickListener untuk tombol logout
        iconLogout.setOnClickListener {
            logoutUser()
        }

        // Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
        getImageData()

        return view
    }

    private fun logoutUser() {
        firebaseAuth.signOut() // Melakukan logout pengguna

        // Mengarahkan pengguna kembali ke layar login atau lokasi lain yang sesuai
        // Contoh pengalihan ke layar masuk:
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Menutup aktivitas saat ini (biasanya MainActivity)
    }


    // Mendapatkan data gambar dan menentukan jumlah pelanggar secara realtime
    private fun getImageData() {
        dbref = FirebaseDatabase.getInstance().getReference("images")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    // Saat sudah ada data pelanggar
                    var count = 0
                    for (userSnapshot in snapshot.children){

                        val selectedDate = Date()

                        val image = userSnapshot.getValue(Images::class.java)

                        // Memperoleh gambar dengan filter tanggal
                        if (image!!.date == formatDate2(selectedDate)) {
                            count++
                        }
                    }

                    val numberTotal =view?.findViewById<TextView>(R.id.numberTextView)
                    numberTotal?.text = count!!.toString()

                    // Menentukan background ImageView berdasarkan jumlah gambar
                    val circleImageView = view?.findViewById<ImageView>(R.id.circleShape)
                    val drawableId = if ( count > 0) R.drawable.circle_shape_red else R.drawable.circle_shape_green
                    circleImageView?.setBackgroundResource(drawableId)

                }else{
                    // Saat belum ada data pelanggar
                    val numberTotal =view?.findViewById<TextView>(R.id.numberTextView)
                    numberTotal?.text = "0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun formatDate2(date: Date?): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return if (date != null) {
            dateFormat.format(date.time)
        } else {
            "Tanggal Tidak Tersedia"
        }
    }
}








