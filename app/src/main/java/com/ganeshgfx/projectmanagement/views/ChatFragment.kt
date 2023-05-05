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
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentChatBinding
import com.ganeshgfx.projectmanagement.viewModels.CalenderViewModel
import com.ganeshgfx.projectmanagement.viewModels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding:FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        binding.toolbar.setupWithNavController(findNavController())

        val activity = requireActivity() as MainActivity
        viewModel.setCurrentProjectId(activity.viewModel.currentProjectId)

        viewModel.receiving.observe(viewLifecycleOwner){
            hideSoftKeyBord(binding.root)
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        return binding.root
    }

}