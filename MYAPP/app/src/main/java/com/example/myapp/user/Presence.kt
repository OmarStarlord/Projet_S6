package com.example.myapp.user
import com.example.myapp.user.Presence
import com.example.myapp.user.EtudiantPresence
import com.example.myapp.user.StatutPresence


data class Presence(
    val coursId: String,
    val date: String,
    val typeCours: String,
    val etudiants: List<EtudiantPresence>
)

data class EtudiantPresence(
    val email: String,
    val statut: StatutPresence
)

enum class StatutPresence {
    PRESENT,
    ABSENT,
    JUSTIFIE
}

