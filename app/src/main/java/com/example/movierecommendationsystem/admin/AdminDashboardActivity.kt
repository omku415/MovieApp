package com.example.movierecommendationsystem.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.R
import com.example.movierecommendationsystem.MainActivity
import com.example.movierecommendationsystem.adapter.FeedbackAdapter
import com.example.movierecommendationsystem.adapters.UserAdapter
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.Feedback
import com.example.movierecommendationsystem.adapters.User

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var logoutBtn: Button
    private lateinit var deleteUserBtn: ImageButton
    private lateinit var viewFeedbackBtn: ImageButton
    private lateinit var feedbackRecyclerView: RecyclerView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter
    private lateinit var userAdapter: UserAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Initialize views
        logoutBtn = findViewById(R.id.logoutBtn)
        deleteUserBtn = findViewById(R.id.deleteUserBtn)
        viewFeedbackBtn = findViewById(R.id.viewFeedbackBtn)
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView)
        userRecyclerView = findViewById(R.id.userRecyclerView)

        // Initialize database and adapters
        dbHelper = DatabaseHelper(this)
        feedbackAdapter = FeedbackAdapter(emptyList())
        userAdapter = UserAdapter(
            mutableListOf(),
            this
        ) {
            loadUsers() // refresh user list after deletion
        }


        feedbackRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        feedbackRecyclerView.adapter = feedbackAdapter
        userRecyclerView.adapter = userAdapter

        // Show feedback by default
        loadFeedback()

        // Button actions
        logoutBtn.setOnClickListener { logoutUser() }
        viewFeedbackBtn.setOnClickListener { loadFeedback() }
        deleteUserBtn.setOnClickListener { loadUsers() }
    }

    private fun loadFeedback() {
        val tripleList = dbHelper.getAllFeedback()
        val feedbackList = tripleList.map { (username, movieTitle, feedbackText) ->
            Feedback(username, movieTitle, feedbackText)
        }

        feedbackAdapter.updateList(feedbackList)
        feedbackRecyclerView.adapter = feedbackAdapter

        // Toggle visibility
        feedbackRecyclerView.visibility = View.VISIBLE
        userRecyclerView.visibility = View.GONE

        Toast.makeText(this, "Loaded ${feedbackList.size} feedback items", Toast.LENGTH_SHORT).show()
    }

    private fun loadUsers() {
        val users = dbHelper.getAllUsers()
        userAdapter.updateList(users)
        userRecyclerView.adapter = userAdapter

        // Toggle visibility
        userRecyclerView.visibility = View.VISIBLE
        feedbackRecyclerView.visibility = View.GONE

        Toast.makeText(this, "Loaded ${users.size} users", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        getSharedPreferences("userPreferences", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showDeleteUserDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("This will permanently delete the user account")
            .setPositiveButton("Delete") { dialog, _ ->
                Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
