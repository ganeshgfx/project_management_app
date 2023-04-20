package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.adapters.ProjectOnClickListener
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectBinding
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectFragment : Fragment() {

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProjectViewModel by viewModels()

//    private lateinit var projectListRecyclerViewAdapter: ProjectListRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project, container, false)

        val activity = requireActivity() as MainActivity

        binding.addfab.setOnLongClickListener {
            Firebase.auth.signOut()
            true
        }

        viewModel.addingProject.observe(viewLifecycleOwner){
            hideSoftKeyBord(binding.root)
        }

        viewModel.formProjectTitleError.observe(viewLifecycleOwner) {
            binding.formProjectTitle.error = if (it) "Project Title Required" else null
        }
        viewModel.formProjectDescriptionError.observe(viewLifecycleOwner) {
            binding.formProjectDescription.error = if (it) "Project Description Required" else null
        }

        viewModel.projectListAdapter.setListener(
            ProjectOnClickListener {
                activity.viewModel.changeProject(it.project.id)
                findNavController().navigate(
                    ProjectFragmentDirections.actionProjectFragmentToCalenderFragment()
                )
            }
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.filter_off ->{
                    binding.toolbar.menu.findItem(R.id.filter_off).isVisible = false
                    binding.toolbar.menu.findItem(R.id.filter_on).isVisible = true
                    binding.filters.visibility = View.GONE
                }
                R.id.filter_on->{
                    binding.toolbar.menu.findItem(R.id.filter_off).isVisible = true
                    binding.toolbar.menu.findItem(R.id.filter_on).isVisible = false
                    binding.filters.visibility = View.VISIBLE
                }
            }
            true
        }

        return binding.root
    }
}