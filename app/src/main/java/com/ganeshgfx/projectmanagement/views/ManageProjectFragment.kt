package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectManageBinding
import com.ganeshgfx.projectmanagement.viewModels.ManageProjectVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageProjectFragment : Fragment() {
    private var _binding: FragmentProjectManageBinding? = null
    private val binding get() = _binding!!

    val viewModel : ManageProjectVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_manage, container, false)
        return binding.root
    }
}