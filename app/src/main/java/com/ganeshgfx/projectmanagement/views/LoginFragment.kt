package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var _binding : FragmentLoginBinding
    val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_projectFragment)
        }
        return binding.root
    }
}