package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.ProjectOnClickListener
import com.ganeshgfx.projectmanagement.adapters.ProjectListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectBinding
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModel

class ProjectFragment : Fragment() {

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProjectViewModel

    private lateinit var projectListRecyclerViewAdapter: ProjectListRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project, container, false)

        checkLogin()

        val activity = requireActivity() as MainActivity
        val projectContainer = activity.appContainer.projectContainer

        if (projectContainer != null) {
            viewModel = ViewModelProvider(
                viewModelStore,
                projectContainer.projectViewModelFactory
            )[ProjectViewModel::class.java]
        }

        binding.addfab.setOnLongClickListener {
            viewModel.deleteAllProjects()
            true
        }

        viewModel.formProjectTitleError.observe(viewLifecycleOwner) {
            binding.formProjectTitle.error = if (it) "Project Title Required" else null
        }
        viewModel.formProjectDescriptionError.observe(viewLifecycleOwner) {
            binding.formProjectDescription.error = if (it) "Project Description Required" else null
        }

        projectListRecyclerViewAdapter = ProjectListRecyclerViewAdapter(
            ProjectOnClickListener {
                activity.viewModel.changeProject(it.project.id!!)
                findNavController().navigate(
                    ProjectFragmentDirections.actionProjectFragmentToTaskOverviewFragment(
                        it.project.id
                    )
                )
            }
        )
        binding.projectList.adapter = projectListRecyclerViewAdapter
        viewModel.projectWithTasksFlow.observe(viewLifecycleOwner) { projectListRecyclerViewAdapter.setData(it) }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    private fun checkLogin() {
        if (false) {
            Toast.makeText(context, "Login Failed...", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_projectFragment_to_loginFragment)
        }
    }

}