package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks

class ProjectListDiffUtil(
    private val oldList: List<ProjectWithTasks>,
    private val newList: List<ProjectWithTasks>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].project.id == newList[newItemPosition].project.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].project.id != newList[newItemPosition].project.id -> false
            oldList[oldItemPosition].project.title != newList[newItemPosition].project.title -> false
            oldList[oldItemPosition].project.description != newList[newItemPosition].project.description -> false
            else -> true
        }
    }
}