package com.ganeshgfx.projectmanagement.views

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.Utils.toDate
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.adapters.TaskOnClickListener
import com.ganeshgfx.projectmanagement.databinding.FragmentTasksListsBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.viewModels.TaskListViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.days

class TasksListsFragment : Fragment() {
    private lateinit var binding: FragmentTasksListsBinding
    private lateinit var viewModel: TaskListViewModel
    private lateinit var taskListAdapter: TaskListRecyclerViewAdapter

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
            viewModel.getTasks(activity.viewModel.currentProjectId)
        } else {
            throw Exception("tasksContainer is null")
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks_lists, container, false)

        viewModel.titleError.observe(viewLifecycleOwner) {
            binding.formTaskTitle.error = if (it) "Task Title Required" else null
        }
        viewModel.descriptionError.observe(viewLifecycleOwner) {
            binding.formTaskDescription.error = if (it) "Task Description Required" else null
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.filters.setOnCheckedStateChangeListener { group, checkedIds ->
            val checked = binding.filters.checkedChipIds
            val filters = checked.map { chipIdToStatus(it) }
            viewModel.setFilters(filters)
        }

        taskListAdapter = viewModel.taskListAdapter

        taskListAdapter.setClickListener(TaskOnClickListener {
            val pos = it
            val _holder = binding.taskListRecyclerview.findViewHolderForAdapterPosition(pos)
            if (_holder != null) {
                val holder =
                    _holder as TaskListRecyclerViewAdapter.TaskListViewHolder
                val task = taskListAdapter.getItem(pos)
                with(holder.binding) {
                    doneTask.setOnClickListener {
                        if (task.status != Status.DONE)
                            updateTask(pos, Status.DONE, mainCard)
                        //else log("Same Status")
                    }
                    pendingTask.setOnClickListener {
                        if (task.status != Status.PENDING)
                            updateTask(pos, Status.PENDING, mainCard)
                        //else log("Same Status")
                    }
                    doingTask.setOnClickListener {
                        if (task.status != Status.IN_PROGRESS)
                            updateTask(pos, Status.IN_PROGRESS, mainCard)
                        //else log("Same Status")
                    }
                }
            }
        })
        viewModel.tasks.observe(viewLifecycleOwner) {
            hideSoftKeyBord(binding.root)
        }
        //binding.toolbar.setOnM
        binding.toolbar.setupWithNavController(findNavController())
        binding.formTaskDueDate.setOnFocusChangeListener { view, hasFocus ->
            log(hasFocus)
            if (hasFocus) showDateInput(view)
        }
        return binding.root
    }

    private fun showDateInput(view: View) {
        val constrains = CalendarConstraints.Builder()
            .setStart(MaterialDatePicker.todayInUtcMilliseconds())
            .setValidator(DateValidatorPointForward.now())
            .build()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Task Due Date")
                .setCalendarConstraints(constrains)
                .build()
        datePicker.addOnPositiveButtonClickListener {
            //log(Date(it))
            viewModel.date = it
            viewModel.dateString.postValue(toDate(it))
        }
        datePicker.show(parentFragmentManager, "Select Task Due Date")
        view.clearFocus()
    }

    private fun updateTask(pos: Int, status: Status, view: View) = lifecycleScope.launch {
        val task = taskListAdapter.getItem(pos)
        task.status = status
        val result = viewModel.updateTask(task)
        if (result == 1) {
            taskListAdapter.updateData(pos, task)
            view.clearFocus()
        }
    }

    private fun chipIdToStatus(id: Int): Status = when (id) {
        R.id.filter_done -> Status.DONE
        R.id.filter_in_progress -> Status.IN_PROGRESS
        R.id.filter_pending -> Status.PENDING
        else -> throw IllegalArgumentException("Invalid chip ID: $id")
    }
}