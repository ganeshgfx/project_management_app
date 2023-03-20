package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectOverviewBinding
import com.ganeshgfx.projectmanagement.models.ProjectTaskCount
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.viewModels.ProjectOverviewViewModel
import org.eazegraph.lib.models.PieModel


class ProjectOverviewFragment : Fragment() {
    private lateinit var _binding: FragmentProjectOverviewBinding
    private val args: ProjectOverviewFragmentArgs by navArgs()
    val binding get() = _binding
    private lateinit var viewModel: ProjectOverviewViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project_overview, container, false)

        val activity = requireActivity() as MainActivity
        val projectContainer = activity.appContainer.projectContainer

        if (projectContainer != null) {
            viewModel = ViewModelProvider(
                viewModelStore,
                projectContainer.projectOverviewViewModelFactory
            )[ProjectOverviewViewModel::class.java]
            viewModel.getTasksStatus(activity.viewModel.currentProjectId)
        } else log("No proj id : ${args.projectId}")

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.project_settings -> {
                    findNavController().navigate(R.id.action_taskProjectFragment_to_manageFragment)
                    true
                }
                else -> false
            }
        }

        viewModel.taskStatusCount.observe(viewLifecycleOwner) {
            binding.pieChart.clearChart()
            if (it.isNotEmpty()) {
                val pending = viewModel.pendingTasks.value!!
                val inProgress = viewModel.doingTasks.value!!
                val done = viewModel.doneTasks.value!!
                with(binding.pieChart) {
                    if (pending > 0) {
                        addPieSlice(
                            PieModel(
                                "Pending", pending.toFloat(),
                                ContextCompat.getColor(requireContext(), R.color.control)
                            )
                        )
                    }
                    if (inProgress > 0) {
                        addPieSlice(
                            PieModel(
                                "In Progress", inProgress.toFloat(),
                                ContextCompat.getColor(requireContext(), R.color.taskInBack)
                            )
                        )
                    }
                    if (done > 0) {
                        addPieSlice(
                            PieModel(
                                "Done", done.toFloat(),
                                ContextCompat.getColor(requireContext(), R.color.taskDoneBack)
                            )
                        )
                    }
                    binding.pieChart.startAnimation()
                }
            }
        }
        return binding.root
    }
}