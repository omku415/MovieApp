package com.example.movierecommendationsystem.adapters

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.database.DatabaseHelper
import com.example.movierecommendationsystem.model.Movie
import com.example.movierecommendationsystem.Feedback
import com.example.movierecommendationsystem.R

class MovieAdapter(
    private var movieList: List<Movie>,
    private val onRemoveClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieImage: ImageView = view.findViewById(R.id.movieImage)
        val movieTitle: TextView = view.findViewById(R.id.movieTitle)
        val movieGenre: TextView = view.findViewById(R.id.movieGenre)
        val movieSummary: TextView = view.findViewById(R.id.movieSummary)
        val menuOptions: ImageView = view.findViewById(R.id.menuOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.movieTitle.text = movie.title
        holder.movieGenre.text = movie.genre
        holder.movieSummary.text = movie.summary
        holder.movieImage.setImageResource(movie.imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movie.url))
            it.context.startActivity(intent)
        }

        holder.menuOptions.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.movie_menu, popupMenu.menu)

            val dbHelper = DatabaseHelper(view.context)
            val username = getLoggedInUsername(view.context)
            val isInWatchlist = dbHelper.isMovieInWatchlist(username, movie.title)

            popupMenu.menu.findItem(R.id.add_to_watchlist).title = if (isInWatchlist) {
                "Remove from Watchlist"
            } else {
                "Add to Watchlist"
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_to_watchlist -> {
                        if (isInWatchlist) {
                            dbHelper.removeFromWatchlist(username, movie.title)
                            Toast.makeText(view.context, "${movie.title} removed from Watchlist!", Toast.LENGTH_SHORT).show()
                            onRemoveClick(movie)
                        } else {
                            addToWatchlist(movie, view.context)
                            Toast.makeText(view.context, "${movie.title} added to Watchlist!", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.give_feedback -> {
                        showFeedbackDialog(view.context, movie.title)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun showFeedbackDialog(context: Context, movieTitle: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Give Feedback for $movieTitle")

        val input = EditText(context)
        input.hint = "Enter your feedback..."
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val feedbackText = input.text.toString().trim()
            if (feedbackText.isNotEmpty()) {
                saveFeedback(context, movieTitle, feedbackText)
                Toast.makeText(context, "Feedback submitted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Feedback cannot be empty!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun saveFeedback(context: Context, movieTitle: String, feedbackText: String) {
        val dbHelper = DatabaseHelper(context)
        val username = getLoggedInUsername(context)
        val feedback = Feedback(username, movieTitle, feedbackText)
        dbHelper.addFeedback(feedback)
    }

    private fun addToWatchlist(movie: Movie, context: Context) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("username", getLoggedInUsername(context))
            put("title", movie.title)
            put("genre", movie.genre)
            put("summary", movie.summary)
            put("imageResId", movie.imageResId)
            put("url", movie.url)
        }

        db.insert("watchlist", null, values)
        db.close()
    }

    private fun getLoggedInUsername(context: Context): String {
        val sharedPref = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("username", "") ?: ""
    }

    override fun getItemCount() = movieList.size

    fun updateList(newList: List<Movie>) {
        movieList = newList
        notifyDataSetChanged()
    }
}