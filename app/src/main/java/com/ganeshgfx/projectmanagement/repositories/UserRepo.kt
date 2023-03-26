package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.User
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val dao: UserDAO,
    private val remote: FirestoreHelper
) {
    suspend fun searchUser(search: String) = remote.searchUsers(search)

    suspend fun addMember(user: User, projectID: String) {
        remote.addMember(user.uid, projectID)
        dao.insertUser(user)
        dao.addMember(
            Member(uid = user.uid, projectId = projectID)
        )
    }

    fun getProjectMembers(projectId: String) = dao.getProjectMember(projectId)
    suspend fun deleteProjectMember(projectId: String, uid: String) {
        remote.removeMember(projectId, uid)
        dao.deleteProjectMember(projectId, uid)
    }
}