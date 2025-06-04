package com.example.myapp.user


data class Cours (
    val nom: String,
    val type: String,  // <- c'est bien une string
    val enseignantEmail: String,
    val date_cours: String
) : java.io.Serializable
