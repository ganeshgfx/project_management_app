package com.ganeshgfx.projectmanagement.database

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

private const val USERS = "users"
private const val PROJECTS = "projects"
private const val MEMBERS = "members"
private const val PROJECT_DATA = "projectData"

class FirestoreHelper(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    //USER RELATED CODE START
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

    suspend fun searchUsers(search: String): List<User> {
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

    suspend fun addMember(userId: String, projectID: String) {
        //TODO add also to members
        val data = hashMapOf(
            "ownerId" to auth.uid!!,
            "projectId" to projectID,
            "userId" to userId
        )
        db.collection(MEMBERS).document("${projectID}_${userId}").set(data).await()
    }

    suspend fun removeMember(userId: String, projectID: String) {
        db.collection(MEMBERS).document("${projectID}_${userId}").delete()
            .await()
    }

    fun getProjectMembers(projectID: String) =
        db.collection(MEMBERS).whereEqualTo("projectId", projectID)

    //USER RELATED CODE END

    //PROJECT RELATED CODE START
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

    fun getOwnProjects() =
        db.collection(PROJECTS).whereEqualTo("uid", auth.uid)
    //PROJECT RELATED CODE END
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