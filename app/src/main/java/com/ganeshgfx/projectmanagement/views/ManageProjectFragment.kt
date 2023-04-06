package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectManageBinding
import com.ganeshgfx.projectmanagement.viewModels.ProjectOverviewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManageProjectFragment : Fragment() {
    private var _binding: FragmentProjectManageBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProjectOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project_manage, container, false)
        val activity = (requireActivity() as MainActivity)
        viewModel.setCurrentProject(activity.viewModel.currentProjectId)
        with(binding) {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            toolbar.setupWithNavController(findNavController())
            addMember.setOnClickListener {
                findNavController().navigate(
                    ManageProjectFragmentDirections.actionManageProjectFragmentToAddMemberFragment()
                )
            }
            deleteProject.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.twotone_delete_forever_24)
                    .setTitle("Are you sure about that ?")
                    .setMessage("If you delete the project all the progress and tasks will be lost")
                    .setPositiveButton("Yes") { dialog, which ->
                        viewModel.deleteProject()
                        findNavController().navigate(ManageProjectFragmentDirections.actionManageProjectFragmentToProjectFragment())
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        return binding.root
    }
}