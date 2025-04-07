package com.example.movierecommendationsystem.model

data class Movie(
    val title: String,
    val genre: String,
    val summary: String,
    val imageResId: Int,
    val url: String
)
