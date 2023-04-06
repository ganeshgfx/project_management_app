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
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        val result = when {
            old.status != new.status -> {
                false
            }
            old.id != new.id -> false
            old.projectId != new.projectId -> false
            old.title != new.title -> false
            old.description != new.description -> false
            else -> true
        }
       //log("areContentsTheSame $result")
        //log(old,new)
        return result
    }
}