package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

private const val USERS = "users"
private const val PROJECTS = "projects"

class FirestoreHelper(private val db: FirebaseFirestore) {

    //Use
    fun addUser(user: User) {
        db.collection(USERS).document(user.uid).set(user)
            .addOnSuccessListener {
                log("User inserted : $user")
            }
            .addOnFailureListener {
                log("Error inserting user : ${it.stackTrace}")
            }
    }

    fun getUsers() = db.collection(USERS).getData<User>()

    //Project
    // fun addProject(project: Project) = db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)

//    fun addProject(project: Project) = callbackFlow{
//        db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)
//            .addOnSuccessListener {
//                trySend(true)
//            }
//            .addOnFailureListener {
//                trySend(false)
//            }
//        awaitClose()
//    }

//    fun addProject(project: Project, callback: (Boolean) -> Unit) {
//        db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)
//            .addOnSuccessListener {
//                callback(true)
//            }
//            .addOnFailureListener {
//                callback(false)
//            }
//    }


    inline fun <reified T> CollectionReference.getData() = callbackFlow {
        val listener =
            EventListener<QuerySnapshot> { list, error ->
                //TODO HANDLE ERROR !
                trySend(list!!.map { it.toObject<T>() })
            }
        val registration = addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }
}