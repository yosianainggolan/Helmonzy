package com.example.helmonzy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth


class Database : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_database, container, false)

        // Mengatur button agar masuk ke database
        val openDatabaseButton2 = view.findViewById<Button>(R.id.openDatabaseButton2)

        // Inisialisasi CalendarView
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)

        var selectedDate: Calendar? = null

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Tangani pemilihan tanggal di sini
            selectedDate = Calendar.getInstance()
            selectedDate?.set(year, month, dayOfMonth)
        }

        openDatabaseButton2.setOnClickListener {
            // Navigasi ke FragmentDatabase2 saat tombol diklik, atau langsung ke tanggal hari itu jika tidak ada tanggal yang dipilih
            if (selectedDate != null) {
                val bundle = Bundle()
                bundle.putSerializable("selectedDate", selectedDate)

                val fragment = Database2()
                fragment.arguments = bundle
                replaceFragment(fragment)
            } else {
                // Jika tanggal tidak dipilih, pilih tanggal hari ini
                selectedDate = Calendar.getInstance()
                val bundle = Bundle()
                bundle.putSerializable("selectedDate", selectedDate)

                val fragment = Database2()
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        }

        val resetDatabaseButton = view.findViewById<Button>(R.id.resetDatabaseButton)

        resetDatabaseButton.setOnClickListener {
            // Mendapatkan instance FirebaseAuth
            val auth = FirebaseAuth.getInstance()

            // Mendapatkan UID pengguna saat ini (jika ada yang masuk)
            val user = auth.currentUser
            val currentUserId = user?.uid

            if (currentUserId != null) {
                if (currentUserId == "----" || currentUserId == "----") {
                    showResetConfirmationDialog()
                }
                else{
                    Toast.makeText(requireContext(), "Reset database hanya dapat dilakukan Admin", Toast.LENGTH_SHORT).show()
                }
            }
            else {}
        }
        return view
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null) // Agar bisa kembali ke FragmentDatabase
        transaction.commit()
    }

    // Fungsi untuk menghapus semua data di Realtime Database
    fun deleteAllDataFromDatabase() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference

        showResetConfirmationDialog()
        reference.setValue(null) // Ini akan menghapus semua data di akar Realtime Database
        Toast.makeText(requireContext(), "Reset database berhasil", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk menghapus semua data di Firebase Storage
    fun deleteAllDataFromStorage() {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        // Mendapatkan daftar semua item di Storage (Rules harus versi 2)
        storageReference.listAll().addOnSuccessListener { listResult ->
            // Iterasi melalui daftar item dan menghapus satu per satu
            for (item in listResult.items) {
                item.delete().addOnSuccessListener {
                    println("Berhasil menghapus ${item.name}")
                }.addOnFailureListener { e ->
                    println("Gagal menghapus ${item.name}: ${e.message}")
                }
            }
        }.addOnFailureListener { e ->
            println("Gagal mendapatkan daftar item di Firebase Storage: ${e.message}")
        }
    }


    // Deklarasi menampilkan dialog
    private var isDialogShown = false

    // Menampilkan dialog
    private fun showResetConfirmationDialog() {
        if (isDialogShown) {
            return // Jangan tampilkan dialog jika sudah ditampilkan sebelumnya
        }

        isDialogShown = true

        val dialogView = layoutInflater.inflate(R.layout.reset_confirmation_dialog, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val yesButton = dialogView.findViewById<Button>(R.id.yes_button)
        val noButton = dialogView.findViewById<Button>(R.id.no_button)

        dialogMessage.text = "Apakah Anda yakin ingin mereset database?"

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()

        // Tombol ya akan menghapus database
        yesButton.setOnClickListener {
            deleteAllDataFromDatabase()
            deleteAllDataFromStorage()
            dialog.dismiss()
            isDialogShown = false // penanganan untuk mengatur dialog supaya tidak dapat ditampilkan kembali
        }

        noButton.setOnClickListener {
            dialog.dismiss()
            isDialogShown = false

        }
    }
}