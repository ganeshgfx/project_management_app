package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.databinding.FragmentTasksListsBinding

class TasksListsFragment : Fragment() {
    private lateinit var binding : FragmentTasksListsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tasks_lists,container,false)
        binding.taskListRecyclerview.adapter = TaskListRecyclerViewAdapter()

        binding.toolbar.setupWithNavController(findNavController())

        return binding.root
    }
}