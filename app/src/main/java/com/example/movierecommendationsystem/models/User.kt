package com.example.movierecommendationsystem.models

data class User(
    val name: String,
    val username: String,
    val phone: String,
    val dob: String,
    val moviePref: String,
    val password: String
)
