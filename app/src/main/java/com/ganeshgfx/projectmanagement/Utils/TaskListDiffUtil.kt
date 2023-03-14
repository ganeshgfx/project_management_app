package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.Task

class TaskListDiffUtil(
    private val oldList: List<Task>,
    private val newList: List<Task>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val result = oldList[oldItemPosition].id == newList[newItemPosition].id
//        log("areItemsTheSame $result")
        return result
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val result = when {
            oldList[oldItemPosition].status != newList[newItemPosition].status -> false
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].projectId != newList[newItemPosition].projectId -> false
            oldList[oldItemPosition].title != newList[newItemPosition].title -> false
            oldList[oldItemPosition].description != newList[newItemPosition].description -> false
            else -> true
        }
       // log("areContentsTheSame $result")
        return result
    }
}