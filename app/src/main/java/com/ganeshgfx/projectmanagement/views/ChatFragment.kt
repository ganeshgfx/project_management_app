package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentChatBinding
import com.ganeshgfx.projectmanagement.viewModels.CalenderViewModel
import com.ganeshgfx.projectmanagement.viewModels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        binding.toolbar.setupWithNavController(findNavController())

        val activity = requireActivity() as MainActivity
        viewModel.setCurrentProjectId(activity.viewModel.currentProjectId,args.chat ?: "")

        viewModel.receiving.observe(viewLifecycleOwner) {
            hideSoftKeyBord(binding.root)
            if (!it) {
                smoothScrollChatList()
                lifecycleScope.launch {
                    delay(500)
                    smoothScrollChatList()
                }

            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        return binding.root
    }

    fun smoothScrollChatList() {
        binding.chatsList.post {
            binding.chatsList.smoothScrollToPosition(viewModel.chatsAdapter.itemCount)
        }
    }

}