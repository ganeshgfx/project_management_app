package com.ganeshgfx.projectmanagement.database

import com.google.firebase.auth.FirebaseAuth

class FirebaseHelper {
    private val user = FirebaseAuth.getInstance()
    fun getLoggedUser() = user.currentUser
}