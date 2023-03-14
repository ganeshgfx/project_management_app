package com.ganeshgfx.projectmanagement.adapters

import com.ganeshgfx.projectmanagement.models.Task

class TaskOnClickListener(val clickListener: (pos: Int) -> Unit) {
    fun onClick(pos: Int) = clickListener(pos)
}