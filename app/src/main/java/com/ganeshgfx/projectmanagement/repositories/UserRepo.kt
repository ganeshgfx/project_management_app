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
    private val remote: FirestoreHelper,
    private val scope: CoroutineScope
) {
    suspend fun searchUser(search: String) = remote.searchUsers(search)

    fun getProjectMembers(projectId: String): Flow<List<ProjectMember>> {
//        remote.getProjectMembers(projectId)
//            .snapshots().onEach { snapshot ->
//                val memberList = mutableListOf<Member>()
//                snapshot.documentChanges.forEach {
//                    val member = Member(
//                        projectId = it.document["projectId"].toString(),
//                        uid = it.document["userId"].toString()
//                    )
//                    when (it.type) {
//                        Type.ADDED -> {
//                            if (remote.myUid != member.uid) {
//                                memberList.add(member)
//                            }
//                        }
//                        Type.MODIFIED -> {}
//                        Type.REMOVED -> scope.launch { dao.deleteMember(member) }
//                    }
//                }
//                if (memberList.size > 0) {
//                    val users = remote.getUsers(memberList.map { it.uid })
//                    users.forEach {
//                        dao.insertUser(it!!)
//                    }
//                    memberList.forEach {
//                        dao.addMember(it)
//                    }
//                }
//            }.launchIn(scope)
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