package com.ganeshgfx.projectmanagement.adapters

import com.ganeshgfx.projectmanagement.models.ProjectWithTasks

class ProjectOnClickListener(val clickListener: (project: ProjectWithTasks) -> Unit) {
    fun onClick(projectWithTasks: ProjectWithTasks) = clickListener(projectWithTasks)
}