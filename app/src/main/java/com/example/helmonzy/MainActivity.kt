package com.example.helmonzy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.helmonzy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //No night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        replaceFragment(Home()) // Set Home Fragment at first launch

        // ALT Set NavBar Icon to Home at first launch, if not using binding
        //val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set the item with the corresponding ID as the active item, with binding. (In this case : Home fragment)
        binding.bottomNavigationView.selectedItemId(R.id.home)

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home())

                R.id.image -> replaceFragment(Image())

                R.id.stats -> replaceFragment(Stats())

                else ->{
                }
            }
            true
        }
    }

    //Function declaration to change Fragment
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    //Function declaration of selectedItemId to set the ID of NavBar
    private fun BottomNavigationView.selectedItemId(home: Int) {
        val homeItemId = R.id.home

        // Set an ID "home" as chosen item
        this.selectedItemId = homeItemId

    }
}