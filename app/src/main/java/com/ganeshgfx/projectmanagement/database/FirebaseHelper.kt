package com.ganeshgfx.projectmanagement.database

import com.google.firebase.auth.FirebaseAuth

class FirebaseHelper(private val auth:FirebaseAuth) {
    fun getLoggedUser() = auth.currentUser
}