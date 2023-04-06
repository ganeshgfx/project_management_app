package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectOverviewBinding
import com.ganeshgfx.projectmanagement.viewModels.ProjectOverviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.eazegraph.lib.models.PieModel

@AndroidEntryPoint
class ProjectOverviewFragment : Fragment() {
    private lateinit var _binding: FragmentProjectOverviewBinding
    val binding get() = _binding
    private val viewModel: ProjectOverviewViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project_overview, container, false)

        val activity = requireActivity() as MainActivity

        viewModel.setCurrentProject(activity.viewModel.currentProjectId)
        viewModel.getProjectInfo()

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