package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ProjectListItemBinding
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status

class ProjectListRecyclerViewAdapter(private val projectOnClickListener: ProjectOnClickListener) :
    RecyclerView.Adapter<ProjectListRecyclerViewAdapter.ProjectListViewHolder>() {

    private var projectList = emptyList<ProjectWithTasks>()

    class ProjectListViewHolder(val binding: ProjectListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder =
        ProjectListViewHolder(
            ProjectListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        val item = projectList[position]
        holder.binding.card.setOnClickListener{
            projectOnClickListener.onClick(item)
        }
        holder.binding.data = item
    }

    override fun getItemCount(): Int = projectList.size

    fun setData(newList: List<ProjectWithTasks>) {
        val diffUtil = ProjectListDiffUtil(projectList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        projectList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList(){
        setData(emptyList<ProjectWithTasks>())
    }
}