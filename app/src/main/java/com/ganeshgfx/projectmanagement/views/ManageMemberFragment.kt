package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.hideSoftKeyBord
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentManageMemberBinding
import com.ganeshgfx.projectmanagement.viewModels.ManageMemberViewModel
import com.google.android.material.snackbar.Snackbar
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
        val activity = (requireActivity() as MainActivity)
        viewModel.setProjectId(activity.viewModel.currentProjectId)
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_manage_member, container, false)
        with(viewModel){
            error.observe(viewLifecycleOwner){
                if(it.isNotBlank()){
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        with(binding) {
            toolbar.setupWithNavController(findNavController())
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            searchButton.setOnClickListener {
                viewModel.search()
                hideSoftKeyBord(it)
                searchText.clearFocus()
            }
            clearSearchButton.setOnClickListener {
                viewModel.search.postValue("")
            }
            searchText.setOnEditorActionListener { view, action, _ ->
                if(action==EditorInfo.IME_ACTION_SEARCH){
                    viewModel.search()
                    hideSoftKeyBord(view)
                    searchText.clearFocus()
                }
                true
            }
//        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
//        userList.layoutManager = staggeredGridLayoutManager
        }

        return binding.root
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        log("destroying")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        log("pausing")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        log("resuming")
//    }
}