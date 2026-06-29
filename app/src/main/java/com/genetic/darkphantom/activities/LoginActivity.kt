package com.genetic.darkphantom.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.genetic.darkphantom.databinding.ActivityLoginBinding
import com.genetic.darkphantom.managers.FirebaseManager
import com.genetic.darkphantom.models.User
import com.genetic.darkphantom.utils.CryptoUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hardcoded admin fallback
            if (username == "SmoothKing" && password == "GeneticDarkPhantom2026") {
                navigateToDashboard("SUPER_ADMIN")
                return@setOnClickListener
            }

            // Check Firebase
            FirebaseManager.usersRef.orderByChild("username")
                .equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var found = false
                        for (userSnap in snapshot.children) {
                            val user = userSnap.getValue(User::class.java)
                            if (user != null) {
                                val hash = CryptoUtils.sha256(password + user.salt)
                                if (hash == user.passwordHash) {
                                    found = true
                                    navigateToDashboard(user.role)
                                    break
                                }
                            }
                        }
                        if (!found) {
                            Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = android.content.Intent(this, DashboardActivity::class.java)
        intent.putExtra("ROLE", role)
        startActivity(intent)
        finish()
    }
}
