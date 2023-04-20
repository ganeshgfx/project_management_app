package com.ganeshgfx.projectmanagement.Utils

import androidx.recyclerview.widget.DiffUtil
import com.ganeshgfx.projectmanagement.models.Day

class CalenderListDiffUtil(
    private val oldList: List<List<Day>>,
    private val newList: List<List<Day>>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val old = oldList[oldPosition]
        val new = newList[newPosition]
        val result: Boolean = old.size == new.size && old.zip(new)
            .all { (oldDay, newDay) -> oldDay.day == newDay.day }

        //log("areItemsTheSame", result)

        return result
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        val result = when {
            old.size != new.size -> false
            else -> old.zip(new).all { (oldDay, newDay) -> oldDay == newDay }
        }
        //log("areContentsTheSame", result)
        return result
    }
}
