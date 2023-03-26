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
import com.google.firebase.database.ktx.database
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
private const val MEMBERS = "members"
private const val PROJECTDATA = "projectData"

class FirestoreHelper(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    //Use
    suspend fun addUser(user: User): Boolean {
        try {
            db.collection(USERS).document(user.uid).set(user).await()
            return (true)
        } catch (error: Exception) {
            log("Error adding User to firestore : ", error)
            return (false)
        }
    }

    fun getUsers() = db.collection(USERS).getData<User>()
    suspend fun searchUsers(search:String):List<User>{
         val result = db.collection(USERS).get().await().documents
            .map { it.toObject<User>()!! }
            .filter {
                val user = it
                val result = user.displayName.lowercase().contains(search.lowercase())
                //log(user,result)
                result
            }
        return result
    }

    //Project
    suspend fun addProject(project: Project): Project {
        val userID = auth.uid!!
        val ref = db.collection(PROJECTS)
        val id = ref.document().id
        val data = project.let {
            Project(id = id, uid = userID, title = it.title, description = it.description)
        }
        ref.document(id).set(data).await()
        return data
    }

    suspend fun addMember(userId: String,projectID:String) {
        val userRef = db.collection(USERS).document(userId)
        val data = hashMapOf(
            "ref" to userRef
        )
        db.collection(PROJECTDATA).document(projectID).collection(MEMBERS).document(userId).set(data).await()
    }
    suspend fun removeMember(userId: String,projectID:String){
        db.collection(PROJECTDATA).document(projectID).collection(MEMBERS).document(userId).delete().await()
    }


    /*
    fun addProject(project: Project) = flow<Boolean> {
        val result = try {
            val ref = db.collection(PROJECTS).document(project.uid).collection(PROJECTS)
            val id = ref.document().id
            val data = project.let {
                Project(id = id, uid = it.uid, title = it.title, description = it.description)
            }
            ref.add(data).await()
            true
        } catch (e: Exception) {
            log("Error adding project : ", e)
            false
        }
        emit(result)
    }
    fun addProject(project: Project) = callbackFlow{
        db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }
    fun addProject(project: Project, callback: (Boolean) -> Unit) {
        db.collection(PROJECTS).document(project.uid).collection(PROJECTS).add(project)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    */

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