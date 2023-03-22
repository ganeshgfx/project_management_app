package com.ganeshgfx.projectmanagement.database

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Flow

class FirebaseAuthHelper(private val auth: FirebaseAuth) {

    val isLogged = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val isAuthenticated = firebaseAuth.currentUser != null
            trySend(isAuthenticated)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    val loggedUser = auth.currentUser?.let  {
        val id = it.uid
        val name = it.displayName
        User(uid = id, displayName = name)
    }
}