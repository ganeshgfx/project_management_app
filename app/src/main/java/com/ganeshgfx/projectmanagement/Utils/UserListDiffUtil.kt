package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.User

class UserListDiffUtil(
    private val oldList: List<ProjectMember>,
    private val newList: List<ProjectMember>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean =
        oldList[oldPosition].user.uid == newList[newPosition].user.uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        when {
            oldList[oldItemPosition].user.uid != newList[newItemPosition].user.uid -> false
            oldList[oldItemPosition].user.displayName != newList[newItemPosition].user.displayName -> false
            else -> true
        }
}