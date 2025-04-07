package com.example.movierecommendationsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movierecommendationsystem.R
import com.example.movierecommendationsystem.Feedback

class FeedbackAdapter(private var feedbackList: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameView: TextView = itemView.findViewById(R.id.usernameTextView)
        val movieTitleView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        val feedbackView: TextView = itemView.findViewById(R.id.feedbackTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.usernameView.text = feedback.username
        holder.movieTitleView.text = feedback.movieTitle
        holder.feedbackView.text = feedback.feedbackText
    }

    override fun getItemCount(): Int = feedbackList.size

    fun updateList(newList: List<Feedback>) {
        feedbackList = newList
        notifyDataSetChanged()
    }
}