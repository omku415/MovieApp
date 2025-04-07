package com.example.movierecommendationsystem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movierecommendationsystem.auth.RegisterActivity
import com.example.movierecommendationsystem.auth.MoviePreferenceActivity
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.ui.theme.MovieRecommendationSystemTheme
import com.example.movierecommendationsystem.admin.AdminDashboardActivity
import android.util.Log

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        sharedPref = getSharedPreferences("userPreferences", MODE_PRIVATE)

        // Check if user is already logged in by verifying shared preferences
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userType = sharedPref.getString("loggedInUser", "user")  // Default to "user" if not found

        // Log the user type for debugging purposes
        Log.d("MainActivity", "User Type Retrieved: $userType")

        // If the user is logged in, navigate to the correct screen based on the user type
        if (isLoggedIn && userType != null) {
            navigateToNextScreen(userType)
            return  // Exit early if user is logged in
        }

        setContent {
            MovieRecommendationSystemTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Background Image
                    Image(
                        painter = painterResource(id = R.drawable.login),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        var username by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }

                        // Username Input
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Enter username") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp)
                        )

                        // Password Input
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Enter password") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login Button
                        Button(
                            onClick = {
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    when {
                                        dbHelper.checkUser(username, password) -> {
                                            saveLoginState(username, "user")
                                            navigateToNextScreen("user")
                                        }
                                        dbHelper.checkAdmin(username, password) -> {
                                            saveLoginState(username, "admin")
                                            navigateToNextScreen("admin")
                                        }
                                        else -> {
                                            Toast.makeText(this@MainActivity, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this@MainActivity, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Login",
                                style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Text
                        Row {
                            Text(
                                text = "New to our app? ",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Register",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navigateToRegister() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveLoginState(username: String, userType: String) {
        sharedPref.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("loggedInUser", username)
            putString("userType", userType)
            apply()
        }
    }

    private fun navigateToNextScreen(userType: String) {
        Log.d("MainActivity", "Navigating to screen for user type: $userType")

        val intent = if (userType == "admin") {
            Log.d("MainActivity", "Navigating to Admin Dashboard")
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Log.d("MainActivity", "Navigating to Movie Preference Activity")
            Intent(this, MoviePreferenceActivity::class.java)
        }

        // Log the action being performed
        Log.d("MainActivity", "Starting activity: ${intent.component?.className}")
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
