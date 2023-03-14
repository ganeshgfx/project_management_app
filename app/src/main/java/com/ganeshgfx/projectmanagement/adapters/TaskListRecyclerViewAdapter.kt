package com.ganeshgfx.projectmanagement.adapters

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.TaskListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.TaskListItemBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.android.material.button.MaterialButton

class TaskListRecyclerViewAdapter(private var taskList: MutableList<Task> = mutableListOf()) :
    RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>() {
    private lateinit var clickListener: TaskOnClickListener

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
//        holder.binding.mainCard.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                clickListener.onClick(position)
//                toggleTaskEditView(holder,true)
//            } else {
//                toggleTaskEditView(holder,false)
//            }
//        }
        holder.binding.mainCard.setOnClickListener {
            clickListener.onClick(position)
            toggleTaskEditView(holder, true)
        }
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

    fun toggleTaskEditView(holder: TaskListViewHolder,toggle : Boolean) {
       if(toggle){
           holder.binding.taskView.visibility = View.GONE
           holder.binding.taskEdit.visibility = View.VISIBLE
       }else{
           holder.binding.taskView.visibility = View.VISIBLE
           holder.binding.taskEdit.visibility = View.GONE
       }
    }

    override fun getItemCount(): Int = taskList.size

    fun setData(newList: List<Task>) {
        val diffUtil = TaskListDiffUtil(taskList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        taskList.clear()
        taskList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateData(pos: Int,task: Task) {
        taskList[pos] = task
        notifyItemChanged(pos)
        //log(taskList.remove(task).toString())
    }

    fun clearList() {
        setData(emptyList<Task>())
    }

    infix fun getItem(pos: Int) = taskList[pos]

    fun setClickListner(clickListener: TaskOnClickListener) {
        this.clickListener = clickListener
    }
}

private fun MaterialButton.setStatusOfTask(
    iconDrawable: Int,
    iconColor: Int,
    backColor: Int
) {
    icon = ContextCompat.getDrawable(context, iconDrawable)
    iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
    setBackgroundColor(ContextCompat.getColor(context, backColor))
}