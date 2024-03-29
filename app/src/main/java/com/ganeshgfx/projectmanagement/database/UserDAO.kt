package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Upsert
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMember(member: Member): Long

    @Delete
    suspend fun deleteMember(member: Member): Int

    @Query("SELECT user.* from user INNER JOIN member ON user.uid = member.uid WHERE member.projectId = :projectId;")
    fun getMembers(projectId: String): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM member WHERE projectId = :projectId")
    fun getMemberCount(projectId: String): Flow<Int>


    @Query("SELECT user.* from user INNER JOIN member ON user.uid = member.uid WHERE member.projectId = :projectId;")
    suspend fun getMembersList(projectId: String): List<User>

    @Query("SELECT * from member WHERE projectId=:projectId")
    fun getProjectMember(projectId: String): Flow<List<ProjectMember>>

    @Query("DELETE from member WHERE projectId = :projectId AND uid = :uid")
    suspend fun deleteProjectMember(projectId: String, uid: String)

    @Query("SELECT * from user WHERE uid=:id")
    suspend fun getUser(id: String): User
}