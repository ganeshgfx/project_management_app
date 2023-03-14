package com.ganeshgfx.projectmanagement.adapters

import com.ganeshgfx.projectmanagement.models.Project

class ProjectOnClickListener(val clickListener: (project: Project) -> Unit) {
    fun onClick(project: Project) = clickListener(project)
}