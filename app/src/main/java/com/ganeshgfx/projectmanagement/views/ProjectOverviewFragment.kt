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
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectOverviewBinding

class ProjectOverviewFragment : Fragment() {
    private lateinit var _binding : FragmentProjectOverviewBinding
    val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_project_overview, container, false)
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.project_settings -> {
                    findNavController().navigate(R.id.action_taskProjectFragment_to_manageFragment)
                    true
                }
                else -> false
            }
        }
        return binding.root
    }
}