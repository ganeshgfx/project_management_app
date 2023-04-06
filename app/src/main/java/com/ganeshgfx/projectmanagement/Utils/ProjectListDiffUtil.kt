package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status

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
        val old = oldList[oldItemPosition]
        val oldPending = old.tasks.filter { it.status == Status.PENDING }.size
        val oldProgress = old.tasks.filter { it.status == Status.IN_PROGRESS }.size
        val oldDone = old.tasks.filter { it.status == Status.DONE }.size

        val new = newList[newItemPosition]
        val newPending = new.tasks.filter { it.status == Status.PENDING }.size
        val newProgress = new.tasks.filter { it.status == Status.IN_PROGRESS }.size
        val newDone = new.tasks.filter { it.status == Status.DONE }.size

        val result = when {
            old.project.id != new.project.id -> false
            old.project.title != new.project.title -> false
            old.project.description != new.project.description -> false
            old.tasks.size != new.tasks.size -> false
            oldPending != newPending -> false
            oldProgress != newProgress -> false
            oldDone != newDone -> false
            else -> true
        }
        return result
    }
}