package com.example.movierecommendationsystem.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movierecommendationsystem.R
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.auth.MoviePreferenceActivity
import com.example.movierecommendationsystem.admin.AdminDashboardActivity
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        sharedPref = getSharedPreferences("userPreferences", MODE_PRIVATE)

        val usernameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check admin first
            if (dbHelper.checkAdmin(username, password)) {
                Log.d("LoginDebug", "Admin login successful for: $username")
                with(sharedPref.edit()) {
                    putString("username", username)
                    putString("userType", "admin")  // Only critical addition
                    apply()
                }
                startActivity(Intent(this, AdminDashboardActivity::class.java).apply {
                    putExtra("username", username)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            // Then check regular user
            else if (dbHelper.checkUser(username, password)) {
                Log.d("LoginDebug", "User login successful for: $username")
                with(sharedPref.edit()) {
                    putString("username", username)
                    putString("userType", "user")  // Only critical addition
                    apply()
                }
                startActivity(Intent(this, MoviePreferenceActivity::class.java).apply {
                    putExtra("username", username)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                Log.w("LoginDebug", "Failed login attempt for: $username")
            }
        }
    }
}