package com.example.movierecommendationsystem.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.MainActivity
import com.example.movierecommendationsystem.model.Movie
import com.example.movierecommendationsystem.adapters.MovieAdapter
import com.example.movierecommendationsystem.R
import android.util.Log
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.WatchlistActivity
import com.example.movierecommendationsystem.admin.AdminDashboardActivity

class MoviePreferenceActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var username: String
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_preferences)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val sharedPref = getSharedPreferences("userPreferences", MODE_PRIVATE)

        username = intent.getStringExtra("username")
            ?: sharedPref.getString("username", null)
                    ?: run {
                Log.e("MoviePreferenceActivity", "No user found in Intent or SharedPrefs!")
                Toast.makeText(this, "Login session expired", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        if (username == "admin") {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
            return
        }

        if (username.isNotEmpty()) {
            val userPreferences = dbHelper.getUserPreferences(username)
            Log.d("MoviePreferenceActivity", "User Preferences for $username: $userPreferences")


            // Sample movies

            val movieList = listOf(
                // Action Movies (10)
                Movie(
                    "Inception",
                    "Action",
                    "A mind-bending thriller.",
                    R.drawable.inception,
                    "https://www.imdb.com/title/tt1375666/"
                ),
                Movie(
                    "The Dark Knight",
                    "Action",
                    "A superhero battles crime.",
                    R.drawable.dark_knight,
                    "https://www.imdb.com/title/tt0468569/"
                ),
                Movie(
                    "Mad Max: Fury Road",
                    "Action",
                    "A high-octane post-apocalyptic chase.",
                    R.drawable.mad_max,
                    "https://www.imdb.com/title/tt1392190/"
                ),
                Movie(
                    "John Wick",
                    "Action",
                    "An assassin seeks revenge.",
                    R.drawable.john_wick,
                    "https://www.imdb.com/title/tt2911666/"
                ),
                Movie(
                    "Gladiator",
                    "Action",
                    "A warrior fights for justice.",
                    R.drawable.gladiator,
                    "https://www.imdb.com/title/tt0172495/"
                ),
                Movie(
                    "Avengers: Endgame",
                    "Action",
                    "Superheroes save the universe.",
                    R.drawable.endgame,
                    "https://www.imdb.com/title/tt1838556/"
                ),
                Movie(
                    "Die Hard",
                    "Action",
                    "A cop fights terrorists.",
                    R.drawable.die_hard,
                    "https://www.imdb.com/title/tt0095016/"
                ),
                Movie(
                    "Mission: Impossible",
                    "Action",
                    "A spy on a dangerous mission.",
                    R.drawable.mission_impossible,
                    "https://www.imdb.com/title/tt0117060/"
                ),
                Movie(
                    "Black Panther",
                    "Action",
                    "A king fights for his people.",
                    R.drawable.black_panther,
                    "https://www.imdb.com/title/tt1825683/"
                ),
                Movie(
                    "The Matrix",
                    "Action",
                    "A hacker discovers the truth.",
                    R.drawable.matrix,
                    "https://www.imdb.com/title/tt0133093/"
                ),

                // Comedy Movies (10)
                Movie(
                    "The Hangover",
                    "Comedy",
                    "A hilarious trip gone wrong.",
                    R.drawable.hangover,
                    "https://www.imdb.com/title/tt1119646/"
                ),
                Movie(
                    "Superbad",
                    "Comedy",
                    "Teen friends on a wild night.",
                    R.drawable.superbad,
                    "https://www.imdb.com/title/tt0829482/"
                ),
                Movie(
                    "Step Brothers",
                    "Comedy",
                    "Two grown men act like kids.",
                    R.drawable.step_brothers,
                    "https://www.imdb.com/title/tt0838283/"
                ),
                Movie(
                    "Dumb and Dumber",
                    "Comedy",
                    "Two idiots on an adventure.",
                    R.drawable.dumb_dumbers,
                    "https://www.imdb.com/title/tt0109686/"
                ),
                Movie(
                    "Anchorman",
                    "Comedy",
                    "A news anchor's crazy journey.",
                    R.drawable.anchorman,
                    "https://www.imdb.com/title/tt0357413/"
                ),
                Movie(
                    "Bridesmaids",
                    "Comedy",
                    "A chaotic wedding drama.",
                    R.drawable.bridesmaids,
                    "https://www.imdb.com/title/tt1478338/"
                ),
                Movie(
                    "21 Jump Street",
                    "Comedy",
                    "Undercover cops in high school.",
                    R.drawable.jumpstreet,
                    "https://www.imdb.com/title/tt1232829/"
                ),
                Movie(
                    "Zombieland",
                    "Comedy",
                    "A funny take on zombies.",
                    R.drawable.zombieland,
                    "https://www.imdb.com/title/tt1156398/"
                ),
                Movie(
                    "Ted",
                    "Comedy",
                    "A teddy bear comes to life.",
                    R.drawable.ted,
                    "https://www.imdb.com/title/tt1637725/"
                ),
                Movie(
                    "The Grand Budapest Hotel",
                    "Comedy",
                    "A stylish and funny mystery.",
                    R.drawable.budapest,
                    "https://www.imdb.com/title/tt2278388/"
                ),

                // Drama Movies (10)
                Movie(
                    "The Shawshank Redemption",
                    "Drama",
                    "A story of hope and perseverance.",
                    R.drawable.shawshank,
                    "https://www.imdb.com/title/tt0111161/"
                ),
                Movie(
                    "Forrest Gump",
                    "Drama",
                    "A man with an extraordinary life.",
                    R.drawable.forrest_gump,
                    "https://www.imdb.com/title/tt0109830/"
                ),
                Movie(
                    "The Green Mile",
                    "Drama",
                    "A prison story with magic.",
                    R.drawable.green_mile,
                    "https://www.imdb.com/title/tt0120689/"
                ),
                Movie(
                    "Fight Club",
                    "Drama",
                    "An underground club changes lives.",
                    R.drawable.fight_club,
                    "https://www.imdb.com/title/tt0137523/"
                ),
                Movie(
                    "Good Will Hunting",
                    "Drama",
                    "A genius struggles with life.",
                    R.drawable.good_will_hunting,
                    "https://www.imdb.com/title/tt0119217/"
                ),
                Movie(
                    "A Beautiful Mind",
                    "Drama",
                    "A mathematician's journey.",
                    R.drawable.beautiful_mind,
                    "https://www.imdb.com/title/tt0268978/"
                ),
                Movie(
                    "The Pursuit of Happyness",
                    "Drama",
                    "A man's struggle and success.",
                    R.drawable.pursuit_happyness,
                    "https://www.imdb.com/title/tt0454921/"
                ),
                Movie(
                    "Schindler's List",
                    "Drama",
                    "A businessman saves lives.",
                    R.drawable.schindlers_list,
                    "https://www.imdb.com/title/tt0108052/"
                ),
                Movie(
                    "The Godfather",
                    "Drama",
                    "A mafia family's legacy.",
                    R.drawable.godfather,
                    "https://www.imdb.com/title/tt0068646/"
                ),
                Movie(
                    "There Will Be Blood",
                    "Drama",
                    "A ruthless man's rise to power.",
                    R.drawable.there_will_be_blood,
                    "https://www.imdb.com/title/tt0469494/"
                ),

                // Horror Movies (10)
                Movie(
                    "The Conjuring",
                    "Horror",
                    "A terrifying supernatural horror.",
                    R.drawable.conjuring,
                    "https://www.imdb.com/title/tt1457767/"
                ),
                Movie(
                    "It",
                    "Horror",
                    "A clown terrorizes children.",
                    R.drawable.it,
                    "https://www.imdb.com/title/tt1396484/"
                ),
                Movie(
                    "A Nightmare on Elm Street",
                    "Horror",
                    "A killer haunts dreams.",
                    R.drawable.nightmare_elm,
                    "https://www.imdb.com/title/tt0087800/"
                ),
                Movie(
                    "The Exorcist",
                    "Horror",
                    "A girl is possessed.",
                    R.drawable.exorcist,
                    "https://www.imdb.com/title/tt0070047/"
                ),
                Movie(
                    "Paranormal Activity",
                    "Horror",
                    "A haunted house horror.",
                    R.drawable.paranormal_activity,
                    "https://www.imdb.com/title/tt1179904/"
                ),
                Movie(
                    "Hereditary",
                    "Horror",
                    "A disturbing family mystery.",
                    R.drawable.hereditary,
                    "https://www.imdb.com/title/tt8772262/"
                ),
                Movie(
                    "Halloween",
                    "Horror",
                    "A masked killer returns.",
                    R.drawable.halloween,
                    "https://www.imdb.com/title/tt0077651/"
                ),
                Movie(
                    "Scream",
                    "Horror",
                    "A killer targets teens.",
                    R.drawable.scream,
                    "https://www.imdb.com/title/tt0117591/"
                ),
                Movie(
                    "The Ring",
                    "Horror",
                    "A cursed videotape.",
                    R.drawable.the_ring,
                    "https://www.imdb.com/title/tt0298130/"
                ),
                Movie(
                    "Insidious",
                    "Horror",
                    "A family faces supernatural forces.",
                    R.drawable.insidious,
                    "https://www.imdb.com/title/tt1591095/"
                ),

                // Romance Movies (10)
                Movie(
                    "Titanic",
                    "Romance",
                    "A timeless love story.",
                    R.drawable.titanic,
                    "https://www.imdb.com/title/tt0120338/"
                ),
                Movie(
                    "The Notebook",
                    "Romance",
                    "A romantic journey over years.",
                    R.drawable.notebook,
                    "https://www.imdb.com/title/tt0332280/"
                ),
                Movie(
                    "Pride and Prejudice",
                    "Romance",
                    "A classic love story.",
                    R.drawable.pride_prejudice,
                    "https://www.imdb.com/title/tt0414387/"
                ),
                Movie(
                    "A Walk to Remember",
                    "Romance",
                    "A love story that changes lives.",
                    R.drawable.walk_to_remember,
                    "https://www.imdb.com/title/tt0281358/"
                ),
                Movie(
                    "La La Land",
                    "Romance",
                    "A musical romance in LA.",
                    R.drawable.la_la_land,
                    "https://www.imdb.com/title/tt3783958/"
                ),
                Movie(
                    "Crazy Rich Asians",
                    "Romance",
                    "A love story with luxury.",
                    R.drawable.crazy_rich_asians,
                    "https://www.imdb.com/title/tt2719848/"
                ),
                Movie(
                    "Me Before You",
                    "Romance",
                    "An emotional love story.",
                    R.drawable.me_before_you,
                    "https://www.imdb.com/title/tt2674426/"
                ),
                Movie(
                    "Pretty Woman",
                    "Romance",
                    "An unexpected love story.",
                    R.drawable.pretty_woman,
                    "https://www.imdb.com/title/tt0100405/"
                ),
                Movie(
                    "10 Things I Hate About You",
                    "Romance",
                    "A high school romance.",
                    R.drawable.ten_things,
                    "https://www.imdb.com/title/tt0147800/"
                ),
                Movie(
                    "Love Actually",
                    "Romance",
                    "Multiple love stories intertwined.",
                    R.drawable.love_actually,
                    "https://www.imdb.com/title/tt0314331/"
                )
            )
            val filteredMovies =
                userPreferences?.split(",")?.map { it.trim() }?.let { selectedGenres ->
                    movieList.filter { movie -> selectedGenres.contains(movie.genre) }
                } ?: movieList

            Log.d("RecyclerView", "Filtered Movies Count: ${filteredMovies.size}")
            adapter =
                MovieAdapter(filteredMovies, this::removeMovieFromWatchlist) // Initialize adapter
            recyclerView.adapter = adapter
        } else {
            Log.e("MoviePreferenceActivity", "No logged-in user found!")
        }

        val logoutButton = findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            with(sharedPref.edit()) {
                putBoolean("isLoggedIn", false)
                apply()
            }
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        val watchlistButton = findViewById<Button>(R.id.watchlistBtn)
        watchlistButton.setOnClickListener {
            Log.d("MoviePreferenceActivity", "Watchlist button clicked for user: $username")
            startActivity(Intent(this, WatchlistActivity::class.java).apply {
                putExtra("username", username)
            })
        }

    }

    fun removeMovieFromWatchlist(movie: Movie) {
        dbHelper.removeFromWatchlist(username, movie.title)
        val updatedMovies = dbHelper.getWatchlistMovies(username) ?: emptyList()

        // Update adapter's data
        if (::adapter.isInitialized) {
            adapter.updateList(updatedMovies) // Make sure your adapter has this method
        }

    }
}

