package com.example.movierecommendationsystem.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movierecommendationsystem.R
import com.example.movierecommendationsystem.database.DatabaseHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        val nameField = findViewById<EditText>(R.id.name)
        val usernameField = findViewById<EditText>(R.id.username)
        val phoneField = findViewById<EditText>(R.id.phone)
        val dobField = findViewById<EditText>(R.id.dob)
        val passwordField = findViewById<EditText>(R.id.password)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        // Movie preference checkboxes
        val actionCheckBox = findViewById<CheckBox>(R.id.checkbox_action)
        val comedyCheckBox = findViewById<CheckBox>(R.id.checkbox_comedy)
        val dramaCheckBox = findViewById<CheckBox>(R.id.checkbox_drama)
        val horrorCheckBox = findViewById<CheckBox>(R.id.checkbox_horror)
        val romanceCheckBox = findViewById<CheckBox>(R.id.checkbox_romance)

        registerBtn.setOnClickListener {
            // Get the text values from fields and trim them to remove whitespace
            val name = nameField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            val dob = dobField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Log the values to debug
            Log.d("RegisterActivity", "Name: '$name', Username: '$username', Phone: '$phone', DOB: '$dob', Password: '$password'")

            // Check if any field is empty
            if (name.isEmpty() || username.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if movie preferences are selected
            val selectedGenres = mutableListOf<String>()
            if (actionCheckBox.isChecked) selectedGenres.add("Action")
            if (comedyCheckBox.isChecked) selectedGenres.add("Comedy")
            if (dramaCheckBox.isChecked) selectedGenres.add("Drama")
            if (horrorCheckBox.isChecked) selectedGenres.add("Horror")
            if (romanceCheckBox.isChecked) selectedGenres.add("Romance")

            if (selectedGenres.isEmpty()) {
                Toast.makeText(this, "Please select at least one movie genre!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val moviePref = selectedGenres.joinToString(", ")

            // Log the selected genres for debugging
            Log.d("RegisterActivity", "Selected Movie Genres: $moviePref")

            // Check if username already exists in the database
            if (dbHelper.checkUsernameExists(username)) {
                Toast.makeText(this, "Username already taken!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt to add the user
            val success = dbHelper.addUser(name, username, phone, dob, moviePref, password)
            if (success) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                // Navigate to LoginActivity after successful registration
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Close the RegisterActivity to prevent going back to it
            } else {
                Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
