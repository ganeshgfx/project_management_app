package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.model.Document
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class UserRepo @Inject constructor(
    private val dao: UserDAO,
    private val remote: FirestoreHelper
) {
    suspend fun searchUser(search: String) = remote.searchUsers(search)

    suspend fun getProjectMembers(projectId: String): Flow<List<ProjectMember>> {
        val ctx = coroutineContext
        withContext(Dispatchers.IO) {
            remote.getProjectMembers(projectId)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        log(error)
                        return@addSnapshotListener
                    }
                    snapshots?.documentChanges?.forEach {
                        val member = Member(
                            projectId = it.document["projectId"].toString(),
                            uid = it.document["userId"].toString()
                        )
                        when (it.type) {
                            Type.ADDED -> CoroutineScope(ctx).launch{ dao.addMember(member) }
                            Type.MODIFIED -> {  }//log("MODIFIED", member)
                            Type.REMOVED -> CoroutineScope(ctx).launch{ dao.deleteMember(member) }
                        }
                    }
                }
        }
        return dao.getProjectMember(projectId)
    }

    suspend fun addMember(user: User, projectID: String) {
        try {
            remote.addMember(user.uid, projectID)
            dao.insertUser(user)
            dao.addMember(
                Member(uid = user.uid, projectId = projectID)
            )
        } catch (error: FirebaseException) {
            log("Error adding member to firestore : ", error)
        }
    }

    suspend fun deleteProjectMember(projectId: String, uid: String) {
        try {
            remote.removeMember(userId = uid, projectID = projectId)
            dao.deleteProjectMember(projectId, uid)
        } catch (error: FirebaseException) {
            log("Error deleting user from firestore : ", error)
        }
    }
}