package com.ganeshgfx.projectmanagement.database

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

private const val USERS = "users"
private const val MEMBERS = "members"
private const val PROJECTS = "projects"
private const val TASKS = "tasks"
private const val ID = "id"
private const val USER_ID = "userId"
private const val PROJECT_ID = "projectId"
private const val OWNER_ID = "ownerId"

class FirestoreHelper(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    //USER RELATED CODE START
    val myUid get() = auth.uid

    suspend fun addUser(user: User): Boolean {
        return try {
            db.collection(USERS).document(user.uid).set(user).await()
            true
        } catch (error: Exception) {
            log("Error adding User to firestore : ", error)
            false
        }
    }

    // TODO: change to chunk of 10s
    suspend fun getUsers(ids: List<String>): List<User?> {
        return ids.chunked(10).map {
            db.collection(USERS)
                .whereIn("uid", it)
                .get().await()
                .map { it.toObject<User>() }
        }.flatten()
        //db.collection(USERS).whereIn("uid", ids).get().await().documents.map { it.toObject() }
    }

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
        val data = makeMember(projectID, userId)
        getMemberDocRef(projectID, userId).set(data).await()
    }

    private fun makeMember(
        projectID: String,
        userId: String
    ) = hashMapOf(
        OWNER_ID to auth.uid!!,
        PROJECT_ID to projectID,
        USER_ID to userId
    )

    suspend fun removeMember(userId: String, projectID: String) {
        getMemberDocRef(projectID, userId).delete()
            .await()
    }

    private fun getMemberDocRef(
        projectID: String,
        userId: String
    ) = db.collection(MEMBERS).document("${projectID}_${userId}")

    fun getProjectMembers(projectID: String) =
        db.collection(MEMBERS).whereEqualTo(PROJECT_ID, projectID)

    fun getProjectMembersAll(projectIds: List<String>) =
        projectIds.chunked(10).map { db.collection(MEMBERS).whereIn(PROJECT_ID, it).snapshots() }
            .asFlow().flattenConcat()

    //USER RELATED CODE END

    //PROJECT RELATED CODE START
    suspend fun addProject(project: Project): Project {
        val userID = auth.uid!!
        val ref = db.collection(PROJECTS)
        val id = ref.document().id
        val data = project.let {
            Project(id = id, uid = userID, title = it.title, description = it.description)
        }
        //ref.document(id).set(data).await()

        db.runTransaction { transaction ->
            transaction.set(ref.document(id), data)
            transaction.set(getMemberDocRef(id, userID), makeMember(id, userID))
        }.await()

        return data
    }

    suspend fun deleteProject(id: String) {
        db.collection(PROJECTS).document(id).delete().await()
    }

    fun getProjectImIn() = db.collection(MEMBERS).whereEqualTo(USER_ID, auth.uid).snapshots()
    fun getProjectList(ids: List<String>): Flow<QuerySnapshot> =
        ids.chunked(10).map { db.collection(PROJECTS).whereIn(ID, it).snapshots() }.asFlow()
            .flattenConcat()

    suspend fun updateProject(project: Project) {
        db.runTransaction { transaction ->
            transaction.update(db.collection(PROJECTS).document(project.id), "title", project.title)
            transaction.update(
                db.collection(PROJECTS).document(project.id),
                "description",
                project.description
            )
        }.await()
    }

//PROJECT RELATED CODE END

    //TASK RELATED CODE START
    suspend fun addTask(task: Task): Task {
        val ref = db.collection(TASKS)
        val id = ref.document().id
        val data = Task(
            id = id,
            projectId = task.projectId,
            title = task.title,
            description = task.description,
            status = task.status,
            startDate = task.startDate,
            endDate = task.endDate,
            assignedTo = task.assignedTo
        )
        ref.document(id).set(data).await()
        return data
    }

    suspend fun deleteTask(task: Task): Boolean {
        val ref = db.collection(TASKS)
        ref.document(task.id).delete().await()
        return true
    }

    fun getTasks(projectId: String) =
        db.collection(TASKS).whereEqualTo(PROJECT_ID, projectId).getData()

    fun getAllTasks(projectIds: List<String>) =
        projectIds.chunked(10)
            .map {
                db.collection(TASKS)
                    .whereIn(PROJECT_ID, it)
                    .snapshots()
            }
            .asFlow()
            .flattenConcat()


    suspend fun updateTask(task: Task) {
        db.collection(TASKS).document(task.id).set(task).await()
    }
//TASK RELATED CODE END
}

inline fun <reified T> CollectionReference.getData() = callbackFlow {
    val listener =
        EventListener<QuerySnapshot> { list, error ->
            if (error != null) {
                log(error)
                return@EventListener
            }
            trySend(list!!.map { it.toObject<T>() })
        }
    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}

inline fun Query.getData() = callbackFlow<List<DocumentChange>> {
    val listener =
        EventListener<QuerySnapshot> { list, error ->
            if (error != null) {
                log("Query.getData()", error)
                return@EventListener
            }
            trySend(list!!.documentChanges)
        }
    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}