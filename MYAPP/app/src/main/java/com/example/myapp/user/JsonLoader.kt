package com.example.myapp.user

import android.content.Context
import android.util.Log
import com.example.myapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*


private const val PI_HOST = "10.74.252.206"
private const val PI_PORT = 22
private const val PI_USER = "groupe6"
private const val PI_PASS = "Pascal8bit"
private const val REMOTE_DATA_DIR = "/data"

/**
 * Ouvre une session SFTP vers la Pi (groupe6@10.74.252.206) et retourne le Session + ChannelSftp.
 * L'appelant doit appeler channel.disconnect() et session.disconnect() après usage.
 */
private fun openSftpChannel(): Pair<Session, ChannelSftp>? {
    return try {
        val jsch = JSch()
        val session = jsch.getSession(PI_USER, PI_HOST, PI_PORT).apply {
            setPassword(PI_PASS)
            setConfig("StrictHostKeyChecking", "no")
            connect(10_000)
        }
        @Suppress("UNCHECKED_CAST")
        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect(10_000)
        Pair(session, channel)
    } catch (e: Exception) {
        // Log the full stack trace:
        Log.e("SFTP", "Impossible d’ouvrir le canal SFTP — exception complète :", e)
        null
    }
}


/**
 * Télécharge “remoteName” depuis REMOTE_DATA_DIR sur la Pi et renvoie le contenu UTF-8 en String.
 * Sauvegarde aussi une copie locale sous filesDir/remoteName si saveLocalCopy = true.
 * Retourne null si échec.
 */
private fun downloadRemoteFile(
    context: Context,
    remoteName: String,
    saveLocalCopy: Boolean = true
): String? {
    val pair = openSftpChannel() ?: return null
    val (session, channel) = pair
    return try {
        channel.cd(REMOTE_DATA_DIR)
        val baos = ByteArrayOutputStream()
        channel.get(remoteName, baos)
        val content = baos.toString(StandardCharsets.UTF_8.name())

        if (saveLocalCopy) {
            try {
                val localFile = File(context.filesDir, remoteName)
                localFile.writeText(content)
                Log.i("DOWNLOAD_REMOTE", "Copie locale enregistrée : ${localFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("DOWNLOAD_REMOTE", "Impossible d’écrire la copie locale de $remoteName : ${e.localizedMessage}")
            }
        }
        content
    } catch (e: Exception) {
        Log.e("DOWNLOAD_REMOTE", "Échec du téléchargement de $remoteName depuis la Pi : ${e.localizedMessage}")
        null
    } finally {
        channel.disconnect()
        session.disconnect()
    }
}

/**
 * Liste tous les fichiers dans REMOTE_DATA_DIR dont le nom commence par “presence_” et se termine par “.json”.
 * Retourne la liste de noms de fichiers ou une liste vide si échec.
 */
private fun listRemotePresenceFiles(): List<String> {
    val pair = openSftpChannel() ?: return emptyList()
    val (session, channel) = pair
    return try {
        channel.cd(REMOTE_DATA_DIR)
        @Suppress("UNCHECKED_CAST")
        val entries = channel.ls(REMOTE_DATA_DIR) as Vector<ChannelSftp.LsEntry>
        val result = mutableListOf<String>()
        for (item in entries) {
            val name = item.filename
            if (name.startsWith("presence_") && name.endsWith(".json")) {
                result.add(name)
            }
        }
        result
    } catch (e: Exception) {
        Log.e("SFTP_LIST", "Impossible de lister les présences distantes : ${e.localizedMessage}")
        emptyList()
    } finally {
        channel.disconnect()
        session.disconnect()
    }
}


data class UserJson(
    val email: String,
    val password: String,
    val role: String,
    val name: String,
    val photoResId: String,
    val groupeTD: String?,
    val groupeTP: String?
)

data class CoursJson(
    val nom: String,
    val type: String,
    val enseignantEmail: String,
    val date_cours: String
)

/**
 * Charge la liste des utilisateurs depuis “users.json” sur la Pi (REMOTE_DATA_DIR/users.json).
 * Parse le JSON et retourne la List<User>. En cas d’échec ou si le fichier n’existe pas, retourne emptyList().
 * Sauvegarde une copie locale sous filesDir/users.json.
 */
fun loadUsers(context: Context): List<User> {
    val gson = Gson()
    val json = downloadRemoteFile(context, "users.json", saveLocalCopy = true)
    if (json.isNullOrBlank()) {
        Log.e("LOAD_USERS", "users.json est vide ou n'a pas pu être téléchargé.")
        return emptyList()
    }

    return try {
        val type = object : TypeToken<List<UserJson>>() {}.type
        val userJsonList: List<UserJson> = gson.fromJson(json, type)
        userJsonList.map {
            User(
                email = it.email,
                password = it.password,
                role = Role.valueOf(it.role),
                name = it.name,
                photoResId = when (it.photoResId) {
                    "teacher_photo" -> R.drawable.teacher_photo
                    "student_photo" -> R.drawable.student_photo
                    else -> R.drawable.default_avatar
                },
                studentInfo = if (it.role == "STUDENT") {
                    StudentInfo(
                        groupeTD = it.groupeTD ?: "",
                        groupeTP = it.groupeTP ?: ""
                    )
                } else null
            )
        }
    } catch (e: Exception) {
        Log.e("LOAD_USERS", "Impossible de parser users.json : ${e.localizedMessage}")
        emptyList()
    }
}

/**
 * Charge la liste des cours depuis “cours.json” sur la Pi (REMOTE_DATA_DIR/cours.json).
 * Parse le JSON et retourne la List<Cours>. En cas d’échec ou si le fichier n’existe pas, retourne emptyList().
 * Sauvegarde une copie locale sous filesDir/cours.json.
 */
fun loadCourses(context: Context): List<Cours> {
    val gson = Gson()
    val json = downloadRemoteFile(context, "cours.json", saveLocalCopy = true)
    if (json != null) {
        Log.i("DEBUG_COURS_JSON", json)
    }
    if (json.isNullOrBlank()) {
        Log.e("LOAD_COURSES", "cours.json est vide ou n'a pas pu être téléchargé.")
        return emptyList()
    }

    return try {
        val type = object : TypeToken<List<CoursJson>>() {}.type
        val coursJsonList: List<CoursJson> = gson.fromJson(json, type)
        coursJsonList.map {
            Cours(
                nom = it.nom.trim(),
                type = it.type.trim().uppercase(Locale.ROOT),
                enseignantEmail = it.enseignantEmail.trim(),
                date_cours = it.date_cours.trim()
            )
        }
    } catch (e: Exception) {
        Log.e("LOAD_COURSES", "Impossible de parser cours.json : ${e.localizedMessage}")
        emptyList()
    }
}




/**
 * Charge toutes les présences distantes : récupère la liste des fichiers “presence_*.json” sur la Pi,
 * télécharge chacun, parse en Presence et renvoie la liste.
 * Sauvegarde une copie locale dans filesDir/<filename>.
 * Si aucun fichier n’est trouvé, retourne emptyList().
 */
fun loadPresences(context: Context): List<Presence> {
    val gson = Gson()
    val remoteNames = listRemotePresenceFiles()
    if (remoteNames.isEmpty()) {
        Log.d("LOAD_PRESENCES", "Aucun fichier presence_*.json trouvé sur la Pi.")
        return emptyList()
    }

    val result = mutableListOf<Presence>()
    for (filename in remoteNames) {
        val json = downloadRemoteFile(context, filename, saveLocalCopy = true)
        if (json.isNullOrBlank()) {
            Log.e("LOAD_PRESENCES", "Échec du téléchargement ou contenu vide pour : $filename")
            continue
        }
        try {
            val pres = gson.fromJson(json, Presence::class.java)
            result.add(pres)
        } catch (e: Exception) {
            Log.e("LOAD_PRESENCES", "Impossible de parser $filename : ${e.localizedMessage}")
        }
    }
    return result
}

/**
 * Sauvegarde une présence unique sur la Pi en créant “presence_<coursId>_<dateParts>.json”
 * dans filesDir (copie locale), puis en uploadant ce fichier via SFTP vers REMOTE_DATA_DIR.
 * Si la Pi ou le dossier distant n'existe pas, l'upload échouera mais la copie locale restera.
 */
fun savePresence(context: Context, presence: Presence) {
    val courseName = presence.coursId.replace(" ", "_")
    val dateParts = presence.date.replace(":", "").replace("-", "").replace(" ", "_")
    val fileName = "presence_${courseName}_$dateParts.json"

    // 1) Écrire localement (toujours faire une copie locale)
    val localFile = File(context.filesDir, fileName)
    try {
        val gson = Gson()
        val json = gson.toJson(presence)
        localFile.writeText(json)
        Log.i("SAVE_PRESENCE", "Copie locale écrite : ${localFile.absolutePath}")
    } catch (e: Exception) {
        Log.e("SAVE_PRESENCE", "Impossible d’écrire localement $fileName : ${e.localizedMessage}")
        // On tente quand même l’upload
    }

    // 2) Upload via SFTP vers la Pi
    val pair = openSftpChannel()
    if (pair == null) {
        Log.e("SAVE_PRESENCE", "Impossible d’ouvrir le canal SFTP pour l’upload.")
        return
    }
    val (session, channel) = pair
    try {
        channel.cd(REMOTE_DATA_DIR)
        channel.put(localFile.absolutePath, fileName)
        Log.i("SAVE_PRESENCE", "Upload de $fileName vers Pi/data/ réussi.")
    } catch (e: Exception) {
        Log.e("SAVE_PRESENCE", "❌ Échec de l’upload de $fileName : ${e.localizedMessage}")
    } finally {
        channel.disconnect()
        session.disconnect()
    }
}
