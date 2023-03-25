package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.databinding.FragmentManageMemberBinding
import com.ganeshgfx.projectmanagement.viewModels.ManageMemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageMemberFragment : Fragment() {
    private var _binding: FragmentManageMemberBinding? = null
    private val binding get() = _binding!!
    val viewModel: ManageMemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manage_member, container, false)
        binding.toolbar.setupWithNavController(findNavController())
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchButton.setOnClickListener{
            viewModel.search()
        }
        return binding.root
    }

}