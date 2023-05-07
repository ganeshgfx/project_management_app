package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status

class DateNavListDiffUtil(
    private val oldList: List<String>,
    private val newList: List<String>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]

        val new = newList[newItemPosition]


        val result = when {
            old != new -> false
            else -> true
        }
        return result
    }
}