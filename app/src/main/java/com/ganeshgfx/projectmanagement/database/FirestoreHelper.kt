package com.ganeshgfx.projectmanagement.database

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Flow

private const val USERS = "users"
private const val PROJECTS = "projects"

class FirestoreHelper(private val db: FirebaseFirestore) {

    fun addUser(user: User) {
        db.collection(USERS).document(user.uid).set(user)
            .addOnSuccessListener {
                log("User inserted : $user")
            }
            .addOnFailureListener {
                log("Error inserting user : ${it.stackTrace}")
            }
    }

    fun addProject(project: Project) {
        db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)
            .addOnSuccessListener {
                log("Project inserted : $project")
            }
            .addOnFailureListener {
                log("Error inserting project : ${it.stackTrace}")
            }
    }

    private val doc = db.collection(USERS)
    fun getUser() {
        val listener =
            EventListener<QuerySnapshot> { list, error ->
                val users = mutableListOf<User>()
                users.addAll(list!!.map { it.toObject<User>() })
                log(users)
            }
        doc.addSnapshotListener(listener)
    }

    fun getUsers() = doc.getData<User>()
}

inline fun <reified T> CollectionReference.getData() = callbackFlow {
    val listener =
        EventListener<QuerySnapshot> { list, error ->
            //TODO HANDLE ERROR !
            trySend(list!!.map { it.toObject<T>() })
        }
    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}

