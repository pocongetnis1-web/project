package com.genetic.darkphantom.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.genetic.darkphantom.R
import com.genetic.darkphantom.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private var currentRole = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentRole = intent.getStringExtra("ROLE") ?: "USER"

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "GENETIC v6.0"

        // Long-press title untuk Settings (hanya SUPER_ADMIN)
        binding.toolbar.setOnLongClickListener {
            if (currentRole == "SUPER_ADMIN") {
                startActivity(Intent(this, SettingsActivity::class.java))
                Toast.makeText(this, "Settings opened", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.menu_devices -> {
                    loadFragment(DevicesFragment())
                    true
                }
                R.id.menu_kontrol -> {
                    if (currentRole in listOf("SUPER_ADMIN", "ADMIN", "DEVELOPER")) {
                        loadFragment(KontrolFragment())
                    } else {
                        Toast.makeText(this, "Akses ditolak!", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                R.id.menu_pesan -> {
                    loadFragment(PesanFragment())
                    true
                }
                else -> false
            }
        }

        // Default: Home
        binding.bottomNav.selectedItemId = R.id.menu_home
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
