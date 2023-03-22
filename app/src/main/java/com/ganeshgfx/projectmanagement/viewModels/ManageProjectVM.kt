package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.ViewModel
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageProjectVM @Inject constructor(private val repo: ProjectRepository) : ViewModel() {

}