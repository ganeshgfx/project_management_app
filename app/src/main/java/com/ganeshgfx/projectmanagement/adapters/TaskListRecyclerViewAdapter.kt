package com.ganeshgfx.projectmanagement.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.TaskListDiffUtil
import com.ganeshgfx.projectmanagement.databinding.TaskListItemBinding
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.SurfaceColors

class TaskListRecyclerViewAdapter :
    RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>() {

    private var taskList = emptyList<Task>()

    class TaskListViewHolder(val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder =
        TaskListViewHolder(
            TaskListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding.data = task
        with(holder.binding.taskStatusIcon) {
            when (task.status) {
                Status.IN_PROGRESS -> {
                    setStatusOfTask(
                        R.drawable.twotone_draw_24,
                        R.color.taskIn,
                        R.color.taskInBack
                    )
                }
                Status.DONE -> {
                    setStatusOfTask(
                        R.drawable.round_done_outline_24,
                        R.color.taskDone,
                        R.color.taskDoneBack
                    )
                }
                Status.PENDING -> {
                    setStatusOfTask(
                        R.drawable.outline_circle_24,
                        R.color.control,
                        R.color.controlBack
                    )
                }
            }
        }
    }
    override fun getItemCount(): Int = taskList.size
    fun setData(newList: List<Task>) {
        val diffUtil = TaskListDiffUtil(taskList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        taskList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList(){
        setData(emptyList<Task>())
    }
}
private fun MaterialButton.setStatusOfTask(
    iconDrawable: Int,
    iconColor: Int,
    backColor: Int
){
    icon = ContextCompat.getDrawable(context,iconDrawable)
    iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
    setBackgroundColor(ContextCompat.getColor(context, backColor))
}