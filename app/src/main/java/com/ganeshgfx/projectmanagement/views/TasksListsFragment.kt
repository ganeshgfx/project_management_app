package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.adapters.TaskOnClickListener
import com.ganeshgfx.projectmanagement.databinding.FragmentTasksListsBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.viewModels.TaskListViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TasksListsFragment : Fragment() {
    private lateinit var binding: FragmentTasksListsBinding
    private lateinit var viewModel: TaskListViewModel
    private lateinit var taskListRecyclerViewAdapter: TaskListRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity = requireActivity() as MainActivity
        val tasksContainer = activity.appContainer.taskListContainer
        if (tasksContainer != null) {
            viewModel = ViewModelProvider(
                viewModelStore,
                tasksContainer.taskListViewModelFactory
            )[TaskListViewModel::class.java]
            viewModel.setProjectId(activity.viewModel.currentProjectId)
        } else {
            throw Exception("tasksContainer is null")
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks_lists, container, false)

        viewModel.titleError.observe(viewLifecycleOwner) {
            binding.formProjectTitle.error = if (it) "Task Title Required" else null
        }
        viewModel.descriptionError.observe(viewLifecycleOwner) {
            binding.formProjectTitle.error = if (it) "Task Description Required" else null
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        taskListRecyclerViewAdapter = TaskListRecyclerViewAdapter()
        with(taskListRecyclerViewAdapter) {
            setClickListner(TaskOnClickListener {
                val pos = it
                val holder = binding.taskListRecyclerview.findViewHolderForAdapterPosition(pos) as TaskListRecyclerViewAdapter.TaskListViewHolder
                val task = getItem(pos)
                with(holder.binding) {
                    doneTask.setOnClickListener {
                        task.status = Status.DONE
                        lifecycleScope.launch {
                            val result = viewModel.updateTask(task)
                            if(result==1){
                                toggleTaskEditView(holder,false)
                                updateData(pos,task)
                            }
                        }
                    }
                    pendingTask.setOnClickListener {
                        task.status = Status.PENDING
                        lifecycleScope.launch {
                            val result = viewModel.updateTask(task)
                            if(result==1){
                                toggleTaskEditView(holder,false)
                                updateData(pos,task)
                            }
                        }
                    }
                    doingTask.setOnClickListener {
                        task.status = Status.IN_PROGRESS
                        lifecycleScope.launch {
                            val result = viewModel.updateTask(task)
                            if(result==1){
                                toggleTaskEditView(holder,false)
                                updateData(pos,task)
                            }
                        }
                    }
                }
            })
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            taskListRecyclerViewAdapter.setData(it)
        }
        binding.taskListRecyclerview.adapter = taskListRecyclerViewAdapter
        binding.toolbar.setupWithNavController(findNavController())

        return binding.root
    }
}