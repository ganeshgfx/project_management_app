package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.databinding.ProjectListItemBinding
import com.ganeshgfx.projectmanagement.models.Project

class ProjectListRecyclerViewAdapter() :
    RecyclerView.Adapter<ProjectListRecyclerViewAdapter.ProjectListViewHolder>() {

    private var projectList = emptyList<Project>()

    class ProjectListViewHolder(val binding: ProjectListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder =
        ProjectListViewHolder(
            ProjectListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        holder.binding.data = projectList[position]
        holder.binding.card.setOnClickListener{
            it.findNavController().navigate(R.id.action_projectFragment_to_taskOverviewFragment)
        }
    }

    override fun getItemCount(): Int = projectList.size

    fun setData(newList: List<Project>) {
        val diffUtil = ProjectListDiffUtil(projectList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        projectList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList(){
        setData(emptyList<Project>())
    }
}