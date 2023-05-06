package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.toDate
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.adapters.OnClickListener
import com.ganeshgfx.projectmanagement.databinding.FragmentTasksListsBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.viewModels.TaskListViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksListsFragment : Fragment() {
    private lateinit var binding: FragmentTasksListsBinding
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var taskListAdapter: TaskListRecyclerViewAdapter

    private var actionMode: ActionMode? = null


    override fun onPause() {
        //hideSelectContext()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity = requireActivity() as MainActivity

        viewModel.getTasks(activity.viewModel.currentProjectId)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks_lists, container, false)

        viewModel.titleError.observe(viewLifecycleOwner) {
            binding.formTaskTitle.error = if (it) "Task Title Required" else null
        }
        viewModel.descriptionError.observe(viewLifecycleOwner) {
            binding.formTaskDescription.error = if (it) "Task Description Required" else null
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.addTaskButton.setOnClickListener {
            viewModel.viewForm()
            viewModel.clearInputs()
        }

        binding.filters.setOnCheckedStateChangeListener { group, checkedIds ->
            val checked = binding.filters.checkedChipIds
            val filters = checked.map { chipIdToStatus(it) }
            viewModel.setFilters(filters)
        }

        taskListAdapter = viewModel.taskListAdapter

        taskListAdapter.setClickListener(OnClickListener { pos ->
            if (pos != null) {
                val _holder = binding.taskListRecyclerview.findViewHolderForAdapterPosition(pos)
                if (_holder != null) {
                    val holder =
                        _holder as TaskListRecyclerViewAdapter.TaskListViewHolder
                    val task = taskListAdapter.getItem(pos)
                    viewModel.selectedTask = task

                    actionMode = binding.toolbar.startActionMode(callback)
                    actionMode?.title = task.title

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
            } else {
                binding.toolbar.collapseActionView()
                hideSelectContext()
                //viewModel.selectedTask = null
            }
        })
        viewModel.tasks.observe(viewLifecycleOwner) {
            hideSoftKeyBord(binding.root)
        }
        //binding.toolbar.setOnM
        binding.toolbar.setupWithNavController(findNavController())
        binding.formTaskDueDate.setOnFocusChangeListener { view, hasFocus ->
            //log(hasFocus)
            if (hasFocus) showDateInput(view)
        }
        return binding.root
    }

    val callback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            if (mode != null) {
                mode.menuInflater.inflate(R.menu.task_select_menu, menu)
                binding.toolbar.visibility = View.GONE
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.delete_task -> {
                    binding.toolbar.visibility = View.VISIBLE
                    viewModel.selectedTask?.let { deleteTask(it) }
                    true
                }

                R.id.edit_task -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.cancelFab.setOnClickListener {
                        viewModel.viewForm()
                        hideSelectContext()
                    }
                    viewModel.selectedTask?.let { editTask(it) }
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    private fun editTask(task: Task) {
        viewModel.selectedTask = task
        viewModel.viewForm()
        viewModel.title.postValue(task.title)
        viewModel.description.postValue(task.description)
        viewModel.startDate = task.startDate
        viewModel.endDate = task.endDate
        viewModel.dateString.postValue(toDate(task.startDate, task.startDate))
        viewModel.taskId = task.id
//        binding.okFab.setOnClickListener {
//            log("Edit Task")
//        }
    }

    private fun deleteTask(task: Task) {
        hideSelectContext()
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.twotone_delete_forever_24)
            .setTitle("Are you sure about that ?")
            .setMessage("If you delete the task all the progress and tasks will be lost")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun hideSelectContext() {
        actionMode?.finish()
        actionMode = null
    }

    private fun showDateInput(view: View) {
        val constrainsBuilder = CalendarConstraints.Builder()
        //.setStart(MaterialDatePicker.todayInUtcMilliseconds())
        // .setValidator(DateValidatorPointForward.now())

        val constrains = constrainsBuilder.build()
        val builder = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Task Due Date Range")
            .setCalendarConstraints(constrains)

        viewModel.startDate?.let {
            builder.setSelection(Pair(it, viewModel.endDate))
        }

        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener {
            //log(Date(it))
            viewModel.startDate = it.first
            viewModel.endDate = it.second
            viewModel.dateString.postValue(toDate(it.first, it.second))
        }
        datePicker.show(parentFragmentManager, "Select Task Due Date")
        view.clearFocus()
    }

    private fun updateTask(pos: Int, status: Status, view: View) = lifecycleScope.launch {
        val task = taskListAdapter.getItem(pos)
        task.status = status
        val result = viewModel.updateTask(task)
        if (result == 1L) {
            //taskListAdapter.updateData(pos, task)
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