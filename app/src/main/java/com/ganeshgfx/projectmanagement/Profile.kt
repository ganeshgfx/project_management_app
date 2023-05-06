package com.ganeshgfx.projectmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.databinding.FragmentProfileBinding
import com.ganeshgfx.projectmanagement.databinding.FragmentProjectManageBinding
import com.ganeshgfx.projectmanagement.views.ManageProjectFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Profile : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.signOutBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.twotone_cloud_off_24)
                .setTitle("Are you sure about that ?")
                .setMessage("Do you want to LogOut?")
                .setPositiveButton("Yes") { dialog, which ->
                    Firebase.auth.signOut()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.toolbar.setupWithNavController(findNavController())

        return binding.root
    }
}