package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirebaseAuthHelper
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val dao: UserDAO,
    private val remote: FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val Xauth: FirebaseAuthHelper,
    private val store: FirestoreHelper
) {
    val isLogged = Xauth.isLogged

    fun getUsers() = store.getUsers()

    suspend fun addLoggedUser() {
        Xauth.loggedUser?.let {
            dao.insertUser(it)
            store.addUser(it)
        }
    }

}