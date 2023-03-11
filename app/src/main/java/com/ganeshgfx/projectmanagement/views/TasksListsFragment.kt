package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.databinding.FragmentTasksListsBinding
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModel
import com.ganeshgfx.projectmanagement.viewModels.TaskListViewModel

class TasksListsFragment : Fragment() {
    private lateinit var binding : FragmentTasksListsBinding
    private lateinit var viewModel : TaskListViewModel
    private lateinit var taskListRecyclerViewAdapter : TaskListRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val  tasksContainer =  (requireActivity() as MainActivity).appContainer.taskListContainer
        if (tasksContainer != null) {
            viewModel = ViewModelProvider(viewModelStore,tasksContainer.taskListViewModelFactory)[TaskListViewModel::class.java]
        }else{
            throw Exception("tasksContainer is null")
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tasks_lists,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        taskListRecyclerViewAdapter = TaskListRecyclerViewAdapter()
        viewModel.tasks.observe(viewLifecycleOwner){
          taskListRecyclerViewAdapter.setData(it)
        }
        binding.taskListRecyclerview.adapter = taskListRecyclerViewAdapter
        binding.toolbar.setupWithNavController(findNavController())
        return binding.root
    }
}