package com.ganeshgfx.projectmanagement.repositories

import android.database.sqlite.SQLiteConstraintException
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.State
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.snapshots
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

    fun getProjectMembers(projectId: String): Flow<List<ProjectMember>> {
        return dao.getProjectMember(projectId)
    }

    suspend fun addMember(user: User, projectID: String): State<String> {
        try {
            remote.addMember(user.uid, projectID)
            dao.insertUser(user)
            dao.addMember(Member(uid = user.uid, projectId = projectID))
            return State.Success("Member Added")
        } catch (error: FirebaseException) {
            log("Error adding member to firestore : ", error)
            return State.Error(error.message!!)
        } catch (error: SQLiteConstraintException) {
            log("SQL Error : ", error)
            return State.Error(error.message!!)
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