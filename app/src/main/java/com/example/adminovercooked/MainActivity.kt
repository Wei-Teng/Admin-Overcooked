package com.example.adminovercooked

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.adminovercooked.user.ProfileFragment
import com.example.adminovercooked.databinding.ActivityMainBinding
import com.example.adminovercooked.recipereport.ReportFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ReportFragment()).commit()
        setupBottomNavView()
    }

    private fun setupBottomNavView() {
        val bottomNavView = binding.bottomNavView
        bottomNavView.setItemSelected(R.id.bottomNavView, true)
        bottomNavView.setOnItemSelectedListener {
            var fragment : Fragment? = null
            when (it) {
                R.id.reportFragment -> fragment = ReportFragment()
                R.id.profileFragment -> fragment = ProfileFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment!!).commit()
        }
    }
}