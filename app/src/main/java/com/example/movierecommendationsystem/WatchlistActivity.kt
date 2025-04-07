package com.example.movierecommendationsystem
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.adapters.MovieAdapter
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.model.Movie

class WatchlistActivity : AppCompatActivity() {
    private lateinit var adapter: MovieAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Get username from Intent first, then SharedPreferences
        username = intent.getStringExtra("username")
            ?: getSharedPreferences("userPreferences", MODE_PRIVATE)
                .getString("username", null)
                    ?: run {
                Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        // ✅ Log for debugging
        Log.d("WatchlistActivity", "Using username: $username")

        dbHelper = DatabaseHelper(this)
        val watchlistMovies = dbHelper.getWatchlistMovies(username)
        adapter = MovieAdapter(watchlistMovies, this::removeMovieFromWatchlist)
        recyclerView.adapter = adapter
    }

    private fun removeMovieFromWatchlist(movie: Movie) {
        dbHelper.removeFromWatchlist(username, movie.title)
        val updatedMovies = dbHelper.getWatchlistMovies(username)
        adapter.updateList(updatedMovies)
    }
}