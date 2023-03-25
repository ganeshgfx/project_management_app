package com.ganeshgfx.projectmanagement.repositories

import android.icu.text.StringSearch
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.UserDAO
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val dao: UserDAO,
    private val helper: FirestoreHelper
) {
    suspend fun searchUser(search: String) = helper.searchUsers(search)
}