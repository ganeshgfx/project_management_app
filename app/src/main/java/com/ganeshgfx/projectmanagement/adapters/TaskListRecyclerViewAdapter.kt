package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.databinding.TaskListItemBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.gson.Gson

class TaskListRecyclerViewAdapter :
    RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>() {

    private var taskList = emptyList<Task>()

    init {
        val status = listOf(Status.IN_PROGRESS, Status.DONE, Status.PENDING)
        val temp = mutableListOf<Task>()
        for (i in 0..10) {
            temp.add(
                Task(
                    id = i.toLong(),
                    title = "Test",
                    description = "Testing",
                    status = status.random()
                )
            )
        }
        taskList = temp
    }

    class TaskListViewHolder(val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder =
        TaskListViewHolder(
            TaskListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.binding.data = taskList[position]
        holder.binding.txt = taskList[position].toString()
    }

    override fun getItemCount(): Int = taskList.size
}