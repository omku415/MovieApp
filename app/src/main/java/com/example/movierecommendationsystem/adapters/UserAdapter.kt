package com.example.movierecommendationsystem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.R
import com.example.movierecommendationsystem.database.DatabaseHelper

data class User(val username: String, val phone: String)

class UserAdapter(
    private var userList: MutableList<User>,
    private val context: Context,
    private val onUserDeleted: () -> Unit // notify UI to refresh
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.usernameTextView.text = user.username
        holder.phoneTextView.text = "Phone: ${user.phone}"

        holder.deleteIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete '${user.username}'?")
                .setPositiveButton("Delete") { _, _ ->
                    val dbHelper = DatabaseHelper(context)
                    val db = dbHelper.writableDatabase
                    val rows = db.delete("users", "username = ?", arrayOf(user.username))
                    db.close()

                    if (rows > 0) {
                        userList.removeAt(position)
                        notifyItemRemoved(position)
                        onUserDeleted()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<User>) {
        userList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
