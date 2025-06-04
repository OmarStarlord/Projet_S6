package com.example.myapp.user

import java.util.Date

data class Cours(
    val id: String,
    val nom: String,
    val type: String,
    val enseignantEmail: String,
    val date_cours: Date
)
