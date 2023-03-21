package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.database.FirebaseHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import javax.inject.Inject

class ManageProjectRepo @Inject constructor(
    private val firebaseHelper: FirebaseHelper,
    private val dao: ProjectDAO
    ) {

    fun getUser() = firebaseHelper.getLoggedUser()?.uid
    fun getProject(id:Long) = dao.getProject(id)

}