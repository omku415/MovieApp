package com.example.movierecommendationsystem.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.example.movierecommendationsystem.model.Movie
import android.util.Log
import com.example.movierecommendationsystem.Feedback
import com.example.movierecommendationsystem.adapters.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Original table creation (unchanged)
        val createUsersTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                username TEXT UNIQUE,
                phone TEXT,
                dob TEXT,
                moviePref TEXT,
                password TEXT,
                userType TEXT DEFAULT 'user'
            )
        """.trimIndent()

        val createWatchlistTable = """
            CREATE TABLE watchlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                title TEXT,
                genre TEXT,
                summary TEXT,
                imageResId INTEGER,
                url TEXT
            )
        """.trimIndent()

        val createFeedbackTable = """
            CREATE TABLE feedback (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                movieTitle TEXT,
                feedbackText TEXT
            )
        """.trimIndent()

        val createAdminTable = """
            CREATE TABLE IF NOT EXISTS admin (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createWatchlistTable)
        db.execSQL(createFeedbackTable)
        db.execSQL(createAdminTable)

        // ===== UPDATED ADMIN INITIALIZATION =====
        val adminPassword = hashPassword("admin123") // Single hash for consistency

        // Insert into both tables
        db.execSQL("""
            INSERT INTO users (name, username, password, userType) 
            VALUES ('Admin', 'admin', '$adminPassword', 'admin')
        """)

        db.execSQL("""
            INSERT INTO admin (username, password) 
            VALUES ('admin', '$adminPassword')
        """)
    }

    // ===== NEW METHOD =====
    fun isAdmin(username: String): Boolean {
        val db = readableDatabase
        return db.rawQuery("""
            SELECT 1 FROM users WHERE username = ? AND userType = 'admin'
            UNION
            SELECT 1 FROM admin WHERE username = ?
        """, arrayOf(username, username)).use {
            it.count > 0
        }
    }

    // ===== UPDATED ADMIN CHECK =====
    fun checkAdmin(username: String, password: String): Boolean {
        val db = readableDatabase
        return db.rawQuery("""
            SELECT password FROM admin WHERE username = ?
            UNION
            SELECT password FROM users WHERE username = ? AND userType = 'admin'
        """, arrayOf(username, username)).use { cursor ->
            val isValid = cursor.moveToFirst() && cursor.getString(0) == hashPassword(password)
            Log.d("Auth", "Admin auth ${if (isValid) "success" else "failed"}")
            isValid
        }
    }

    // ===== UPDATED USER CHECK =====
    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = """
            SELECT * FROM users 
            WHERE username = ? 
            AND password = ? 
            AND (userType IS NULL OR userType != 'admin')
        """.trimIndent()

        return db.rawQuery(query, arrayOf(username, hashPassword(password))).use {
            it.count > 0
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            // Add the 'url' column if it's not present
            db.execSQL("ALTER TABLE watchlist ADD COLUMN url TEXT")
        }
    }

    fun addUser(name: String, username: String, phone: String, dob: String, moviePref: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("username", username)
            put("phone", phone)
            put("dob", dob)
            put("moviePref", moviePref)
            put("password", hashPassword(password))
            put("userType", "user")
        }
        Log.d("RegisterActivity", "Inserting movie preferences: $moviePref")
        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }



    fun getUserPreferences(username: String): String? {
        val db = this.readableDatabase
        val query = "SELECT moviePref FROM users WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        var preferences: String? = null
        if (cursor.moveToFirst()) {
            preferences = cursor.getString(0)
        }
        Log.d("Database", "Fetched preferences for $username: $preferences")
        cursor.close()
        return preferences
    }

    fun addFeedback(feedback: Feedback) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", feedback.username)
            put("movieTitle", feedback.movieTitle)
            put("feedbackText", feedback.feedbackText)
        }

        db.insert("feedback", null, values)
        db.close()
    }


    private fun hashPassword(password: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hashBytes = md.digest(password.toByteArray())
            val stringBuilder = StringBuilder()
            for (b in hashBytes) {
                stringBuilder.append(String.format("%02x", b))
            }
            stringBuilder.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            password
        }
    }

    fun getAllFeedback(): List<Triple<String, String, String>> {
        val feedbackList = mutableListOf<Triple<String, String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT username, movieTitle, feedbackText FROM feedback", null)

        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(0)
                val movieTitle = cursor.getString(1)
                val feedbackText = cursor.getString(2)
                feedbackList.add(Triple(username, movieTitle, feedbackText))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return feedbackList
    }

    fun checkUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM users WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }



    fun getWatchlistMovies(username: String): List<Movie> {
        val watchlist = mutableListOf<Movie>()
        val db = readableDatabase ?: run {
            Log.e("DatabaseHelper", "Database is null")
            return emptyList()
        }

        // Log the SQL query being executed
        Log.d("DatabaseHelper", "Query: SELECT * FROM watchlist WHERE username = $username")

        val cursor = db.rawQuery("SELECT * FROM watchlist WHERE username = ?", arrayOf(username))

        // Check if the cursor has any data
        if (cursor.moveToFirst()) {  // This checks if the cursor has at least one row
            do {
                try {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"))
                    val summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"))
                    val imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url")) // Get the URL column

                    // Log the details of each movie fetched
                    Log.d("DatabaseHelper", "Fetched movie: $title, $genre, $summary, $url")

                    // Create the Movie object with the URL
                    watchlist.add(Movie(title, genre, summary, imageResId, url))
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "Error while fetching movie data: ${e.message}")
                }
            } while (cursor.moveToNext())
        } else {
            Log.e("DatabaseHelper", "No movies found in watchlist for user: $username")
        }

        cursor.close()  // Close cursor after usage
        db.close()      // Close database connection after operation

        return watchlist
    }

    fun isMovieInWatchlist(username: String, movieTitle: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM watchlist WHERE username = ? AND title = ?"
        val cursor = db.rawQuery(query, arrayOf(username, movieTitle))

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun removeFromWatchlist(username: String, movieTitle: String): Boolean {
        val db = writableDatabase
        val result = db.delete("watchlist", "username = ? AND title = ?", arrayOf(username, movieTitle))
        db.close()
        return result > 0
    }
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT username, phone FROM users WHERE userType = 'user'", null)

        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(0)
                val phone = cursor.getString(1)
                users.add(User(username, phone))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return users
    }




    companion object {
        private const val DATABASE_NAME = "MovieRecommenderDB"
        private const val DATABASE_VERSION = 4
    }
}
